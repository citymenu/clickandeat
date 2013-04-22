
package com.ezar.clickandeat.scheduling;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.GeoLocationRepositoryImpl;
import com.ezar.clickandeat.repository.RestaurantRepositoryImpl;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.Triple;
import org.apache.log4j.Logger;
import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Component(value="restaurantScraper")
public class RestaurantScraper implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantScraper.class);

    @Autowired
    private GeoLocationService geoLocationService;

    @Autowired
    private RestaurantRepositoryImpl restaurantRepository;

    @Autowired
    private GeoLocationRepositoryImpl geoLocationRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    
    private final SortedSet<String> postcodes = new TreeSet<String>();

    private String baseUrl = "http://just-eat.es";

    private String basePath = "resources/images/restaurant";
    
    private String bucketName;

    private S3Service s3Service;

    private String defaultCuisine = "Pizza";
    
    private String sendTo;
    
    private String from;

    private DistributedLock lock;

    private String notificationPhone = null;
    private String notificationSMS = null;
    private String notificationEmail = null;

    
    @Override
    public void afterPropertiesSet() throws Exception {

        this.lock = new DistributedLock(redisTemplate, getClass().getSimpleName());
        
        geoLocationService.setLocale("es_ES");
        restaurantRepository.setUsecache(false);
        geoLocationRepository.setUsecache(false);

        Properties props = new Properties();
        props.load(new ClassPathResource("/aws.s3.synchronize.properties").getInputStream());
        String accessKey = props.getProperty("accesskey");
        String secretKey = props.getProperty("secretkey");
        bucketName = props.getProperty("bucketname");
        AWSCredentials credentials = new AWSCredentials(accessKey, secretKey);
        s3Service = new RestS3Service(credentials);

        for(int i = 0; i < 1000; i++ ) {
            String suffix = "" + i;
            postcodes.add("0800".substring(0,5-suffix.length()) + suffix);
            postcodes.add("2800".substring(0,5-suffix.length()) + suffix);
            postcodes.add("4800".substring(0,5-suffix.length()) + suffix);
            postcodes.add("3500".substring(0,5-suffix.length()) + suffix);
            postcodes.add("2900".substring(0,5-suffix.length()) + suffix);
            postcodes.add("0300".substring(0,5-suffix.length()) + suffix);
        }
    }


    @Scheduled(cron="0 0 10 * * WED")
    public void scrapeData() throws Exception {
        try {
            if(true || lock.acquire()) {
                List<Pair<Restaurant,String>> restaurants = new ArrayList<Pair<Restaurant,String>>();
                final List<String> errorUrls = new ArrayList<String>();
                final List<Restaurant> newRestaurants = new ArrayList<Restaurant>();
                
                Map<String,Triple<Set<String>,List<String>,Integer>> detailsByPostcode = new HashMap<String, Triple<Set<String>, List<String>,Integer>>();
                for( String postcode: postcodes ) {
                    for(Triple<String,String,String> restaurantDetails :getRestaurantDetails(postcode)) {
                        String url = restaurantDetails.first;
                        List<String> cuisines = new ArrayList<String>();
                        List<String> cuisineList = Arrays.asList(StringUtils.delimitedListToStringArray(restaurantDetails.second, ", "));
                        for(String cuisine: cuisineList) {
                            cuisines.add(cuisine.trim());
                        }
                        Triple<Set<String>,List<String>,Integer> details = detailsByPostcode.get(url);
                        if( details == null ) {
                            Set<String> postcodes = new HashSet<String>();
                            Integer rating = Integer.valueOf(restaurantDetails.third);
                            details = new Triple<Set<String>, List<String>,Integer>(postcodes,cuisines,rating);
                            detailsByPostcode.put(url,details);
                        }
                        details.first.add(postcode);
                    }
                }
                LOGGER.info("Extracted " + detailsByPostcode.size() + " restaurants");
        
                for(Map.Entry<String,Triple<Set<String>,List<String>,Integer>> entry: detailsByPostcode.entrySet()) {
                    String url = baseUrl + entry.getKey();
                    Set<String> postcodes = entry.getValue().first;
                    List<String> cuisines = entry.getValue().second;
                    Integer rating = entry.getValue().third;
                    try {
                        Restaurant existingRestaurant = restaurantRepository.findByExternalId(url);
                        if( existingRestaurant != null ) {
                            restaurantRepository.deleteRestaurant(existingRestaurant);
                        }
                        Pair<Restaurant,String> restaurant = buildRestaurant(url, postcodes, cuisines, rating);
                        restaurants.add(restaurant);
                    }
                    catch( Exception ex ) {
                        LOGGER.error("Error occurred loading from url: " + url,ex);
                        errorUrls.add(url);
                    }
                }
                
                // Now save new restaurant details
                for(Pair<Restaurant,String> pair: restaurants) {
                    Restaurant restaurant = pair.first;
                    String imageUrl = pair.second;
                    if(restaurantRepository.findByName(restaurant.getName()) == null ) {

                        boolean hasUploadedImage = true;

                        // Read image into memory
                        try {
                            if(!imageUrl.startsWith("http:")) {
                                imageUrl = "http:" + imageUrl;
                            }
                            URL url = new URL(imageUrl);
                            InputStream is = url.openStream();
                            File file = new File("c:/workspace/clickandeat/src/main/webapp/resources/images/restaurant/" + restaurant.getRestaurantId());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            FileOutputStream faos = new FileOutputStream(file);
                            byte[] b = new byte[2048];
                            int length;
                            while ((length = is.read(b)) != -1) {
                                baos.write(b, 0, length);
                                faos.write(b, 0, length);
                            }
                            is.close();
                            baos.close();
                            faos.close();
                        }
                        catch(Exception ex ) {
                            LOGGER.error("",ex);
                            hasUploadedImage = false;
                        }
        
                        // Save new restaurant
                        restaurant.setHasUploadedImage(hasUploadedImage);
                        restaurant = restaurantRepository.saveRestaurant(restaurant);
                        String restaurantId = restaurant.getRestaurantId();
                        LOGGER.info("Saved [" + restaurant.getName() + "] with id: " + restaurantId);
        
                        String imageType = imageUrl.substring(imageUrl.lastIndexOf(".")+1);
        
                        // Now upload the restaurant image
        //                S3Object object = new S3Object(basePath + "/" + restaurantId);
        //                ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        //                object.setDataInputStream(bis);
        //                object.setContentLength(baos.size());
        //                object.setContentType("image/"+imageType);
                        //S3Bucket bucket = s3Service.getBucket(bucketName);
                        //s3Service.putObject(bucket, object);
                        LOGGER.info("Uploaded image for restaurant id: " + restaurantId);
                        newRestaurants.add(restaurant);
                    }
                }
        
                // Send out an email report
                MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
                    @Override
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                        //message.setTo("soporte@llamarycomer.com");
                        message.setTo("mishimaltd@gmail.com");
                        message.setFrom("noreply@llamarycomer.com");
                        message.setSubject("Restaurant scraper report");
                        StringBuilder sb = new StringBuilder();
                        if(newRestaurants.size() > 0 ) {
                            sb.append("Created the following new restaurants:\n");
                            for(Restaurant restaurant: newRestaurants ) {
                                sb.append("\n").append(restaurant.getName()).append(" (").append(restaurant.getExternalId()).append(")");
                            }
                        }
                        else {
                            sb.append("No new restaurants created\n");
                        }
                        if(errorUrls.size() > 0 ) {
                            sb.append("\n\nCould not parse the following urls:\n\n");
                            for(String errorUrl: errorUrls ) {
                                sb.append(errorUrl).append("\n");
                            }
                        }
                        message.setText(sb.toString());
                    }
                };
                javaMailSender.send(mimeMessagePreparator);
            }
        }
        catch (Exception ex ) {
            LOGGER.error("",ex);
        }
        finally {
            lock.release();
        }
    }


    /**
     * @param postcode
     * @return
     */

    public List<Triple<String,String,String>> getRestaurantDetails(String postcode) throws Exception {
        List<Triple<String,String,String>> details = new ArrayList<Triple<String,String,String>>();
        String url = baseUrl + "/area/" + postcode;
        LOGGER.info("Extracting restaurants for url: " + url);
        Document doc = getDocument(url);
        Elements sponsors = doc.select("div[class=sponsorWrap]");
        for(Element sponsor: sponsors) {
            Element link = sponsor.select("section[class=menuDetails] > a[href]").first();
            Element cuisines = sponsor.select("section[class=menuDetails] > p[class=restaurantCuisines]").first();
            Element ratings = sponsor.select("span[class^=rating]").first();
            String cuisine = defaultCuisine;
            if(cuisines != null ) {
                cuisine = cuisines.text().replaceAll("Tipo","").replaceAll("Comida","").trim();                 
            }
            String rating = "0";
            if( ratings != null ) {
                String ratingText = ratings.text();
                rating = ratingText.substring(ratingText.indexOf("-") + 1);
            }
            details.add(new Triple<String,String,String>(link.attr("href"),cuisine,rating));
        }
        
        LOGGER.info("Extracted " + sponsors.size() + " urls for postcode: " + postcode);
        return details;
    }


    /**
     * @param url
     * @param postcodes
     * @param cuisines
     * @param rating
     * @return
     * @throws Exception
     */

    public Pair<Restaurant,String> buildRestaurant(String url, Set<String> postcodes, List<String> cuisines, Integer rating) throws Exception {

        LOGGER.info("Building restaurant from url: " + url);
        
        Document doc = getDocument( url );

        Restaurant restaurant = restaurantRepository.create();
        restaurant.setListOnSite(true);
        restaurant.setPhoneOrdersOnly(false);
        restaurant.setExternalId( url );
        restaurant.setJustEatRating(rating);
        restaurant.setRecommended(rating >= 45);

        // Get the image url
        String imageUrl = doc.select("img[id=ctl00_ContentPlaceHolder1_RestInfo_DefaultRestaurantImage]").first().attr("src");
        
        // Get the name
        String name = doc.select("span[id=ctl00_ContentPlaceHolder1_RestInfo_lblRestName]").first().text();
        restaurant.setName(name);

        // Get the cuisines
        restaurant.setCuisines(cuisines);

        // Get any discount
        Element discountElement = doc.select("p[class=discountSummary]").first();
        if(discountElement != null) {
            String[] discountElems = discountElement.text().split(" ");
            String dicountAmount = discountElems[0];
            String orderValue = discountElems[discountElems.length-1];
            Discount discount = new Discount();
            discount.setCollection(true);
            discount.setDelivery(true);
            discount.setDiscountType(Discount.DISCOUNT_PERCENTAGE);
            discount.setTitle(discountElement.text());
            discount.setDiscountAmount(Double.valueOf(dicountAmount.replace("%","")));
            discount.setMinimumOrderValue(Double.valueOf(orderValue.replace("€","")));
            for(int i = 1; i <=7; i++ ) {
                ApplicableTime time = new ApplicableTime();
                time.setDayOfWeek(i);
                time.setApplicable(true);
                discount.getDiscountApplicableTimes().add(time);
            }
            restaurant.getDiscounts().add(discount);
        }
        
        // Get the location
        String streetAddress = doc.select("span[id=ctl00_ContentPlaceHolder1_RestInfo_lblRestAddress]").first().text();
        String postCode = doc.select("span[id=ctl00_ContentPlaceHolder1_RestInfo_lblRestZip]").first().text();
        String lookupAddress = (streetAddress + " " + postCode);
        LOGGER.info("Checking location for address: " + lookupAddress);
        GeoLocation location = geoLocationService.getLocation(lookupAddress);
        if( location == null || location.getLocationComponents().get("route") == null ) {
            throw new Exception("Location not found for address: " + lookupAddress);
        }
        else {
            Address address = new Address();
            Map<String,String> components = location.getLocationComponents();
            String streetNumber = components.get("street_number");
            address.setAddress1(components.get("route") + (streetNumber == null?"": ", " + streetNumber));
            address.setTown(components.get("locality"));
            address.setPostCode(components.get("postal_code"));
            address.setLocation(location.getLocation());
            address.setRadius(location.getRadius());
            address.setRadiusWarning(location.getRadiusWarning());
            LOGGER.info("Generated address: " + address.getSummary());
            restaurant.setAddress(address);
        }
        
        // Set notification options
        NotificationOptions notificationOptions = restaurant.getNotificationOptions();
        notificationOptions.setReceiveNotificationCall(true);
        notificationOptions.setReceiveSMSNotification(true);
        notificationOptions.setNotificationPhoneNumber(notificationPhone);
        notificationOptions.setNotificationSMSNumber(notificationSMS);
        notificationOptions.setNotificationEmailAddress(notificationEmail);

        // Get opening hours
        Element openingHoursLink = doc.select("a[onclick^=GB_showCenter").first();
        String openingHoursUrl = openingHoursLink.attr("onclick");
        String link = openingHoursUrl.substring(openingHoursUrl.indexOf("https:"),openingHoursUrl.indexOf("');return false;"));
        Document openingHoursDoc = getDocument(link);
        Elements hours = openingHoursDoc.select("span[id^=lblOpen");
        OpeningTimes openingTimes = new OpeningTimes();
        int dayOfWeek = 1;
        for(Element hour: hours ) {
            String text = hour.text();
            String[] elems = text.split(" - ");
            OpeningTime openingTime = new OpeningTime();
            openingTime.setDayOfWeek(dayOfWeek++);
            if(elems.length == 1) {
                openingTime.setOpen(false);
            }
            else {
                openingTime.setOpen(true);
                openingTime.setEarlyOpeningTime(timeFormatter.parseLocalTime(elems[0]));
                openingTime.setEarlyClosingTime(timeFormatter.parseLocalTime(elems[1]));
            }
            openingTimes.addOpeningTime(openingTime);
        }
        restaurant.setOpeningTimes(openingTimes);

        Element deliveryTable = doc.select("td[id=ctl00_ContentPlaceHolder1_RestInfo_TermsCont]").first();
        Elements deliveryCosts = deliveryTable.select("span[class=blackText]");
        Element freeDeliveryOver = doc.select("span[id=ctl00_ContentPlaceHolder1_RestInfo_lblTurningPointText]").first();
        DeliveryOptions deliveryOptions = restaurant.getDeliveryOptions();

        // If delivery costs length is 1, collection orders only
        if(deliveryCosts.size() == 1 ) {
            deliveryOptions.setCollectionOnly(true);
        }
        else {
            // If free delivery set, parse it
            if(freeDeliveryOver != null ) {
                Double freeDeliveryOrderValue = Double.valueOf(freeDeliveryOver.text().replace("€","").replace(",",".").trim());
                deliveryOptions.setAllowFreeDelivery(true);
                deliveryOptions.setMinimumOrderForFreeDelivery(freeDeliveryOrderValue);
            }
            // Now parse delivery details
            for( int i = 0; i < deliveryCosts.size(); i+=2 ) {
                String deliveryText = deliveryCosts.get(i).text();
                boolean isOrderOverValue = deliveryText.startsWith("Más");
                String orderText = deliveryText.replace("€", "").replace("Más de", "").replace("Menos de", "").replace(",", ".").replace("=","").trim();
                Double orderValue = Double.valueOf(orderText);
                String deliveryCostText = deliveryCosts.get(i+1).text().trim();
                if("Gratis".equalsIgnoreCase(deliveryCostText)) {
                    deliveryOptions.setMinimumOrderForFreeDelivery(orderValue);
                    deliveryOptions.setAllowFreeDelivery(true);
                }
                else if("No entrega".equalsIgnoreCase(deliveryCostText)) {
                    if( orderValue != 0d ) {
                        deliveryOptions.setMinimumOrderForDelivery(orderValue);
                    }
                }
                else {
                    Double deliveryCharge = Double.valueOf(deliveryCostText.replace("€","").replace(",",".").trim());
                    if(isOrderOverValue) {
                        if( orderValue == 0d ) {
                            deliveryOptions.setDeliveryCharge(deliveryCharge);
                        }
                        else {
                            VariableDeliveryCharge variableDeliveryCharge = new VariableDeliveryCharge();
                            variableDeliveryCharge.setMinimumOrderValue(orderValue);
                            variableDeliveryCharge.setDeliveryCharge(deliveryCharge);
                            deliveryOptions.getVariableDeliveryCharges().add(variableDeliveryCharge);
                        }
                    }
                    else {
                        deliveryOptions.setDeliveryCharge(deliveryCharge);
                    }
                }
            }
        }
    
        // Set delivery areas and default delivery radius
        deliveryOptions.setAreasDeliveredTo(new ArrayList<String>(postcodes));
        deliveryOptions.setDeliveryRadiusInKilometres(1d);

        // Clean up delivery options
        if( deliveryOptions.getMinimumOrderForDelivery() != null && deliveryOptions.getDeliveryCharge() == null ) {
            if(deliveryOptions.getVariableDeliveryCharges().size() == 1 ) {
                VariableDeliveryCharge variableDeliveryCharge = deliveryOptions.getVariableDeliveryCharges().first();
                deliveryOptions.setMinimumOrderForDelivery(variableDeliveryCharge.getMinimumOrderValue());
                deliveryOptions.setDeliveryCharge(variableDeliveryCharge.getDeliveryCharge());
                deliveryOptions.getVariableDeliveryCharges().clear();
            }
            if(deliveryOptions.getMinimumOrderForFreeDelivery() != null ) {
                deliveryOptions.setAllowDeliveryBelowMinimumForFreeDelivery(true);
            }
        }
        if( deliveryOptions.getMinimumOrderForDelivery() != null && deliveryOptions.getMinimumOrderForFreeDelivery() != null &&
                deliveryOptions.getMinimumOrderForDelivery().equals(deliveryOptions.getMinimumOrderForFreeDelivery())) {
            deliveryOptions.setMinimumOrderForDelivery(null);
            deliveryOptions.setAllowDeliveryBelowMinimumForFreeDelivery(false);
        }
        if( deliveryOptions.getDeliveryCharge() != null && deliveryOptions.getMinimumOrderForFreeDelivery() != null ) {
            deliveryOptions.setAllowDeliveryBelowMinimumForFreeDelivery(true);
        }

        // Get the menu
        Element menuRoot = doc.select("table[itemprop=menu]").first();
        Elements categories = menuRoot.select("h2[class=H2MC]");
        Elements itemLists = menuRoot.select("table[style=border:0px solid green;]");
        
        for(int i = 0; i < categories.size(); i++ ) {

            Element category = categories.get(i);
            String categoryName = category.text();
            Elements menuItems = itemLists.get(i).select("tr[class^=prdLi");

            if(categoryName.equals("Offertas") || categoryName.equals("Menú") ||
                    categoryName.equals("Ofertas Especiales") || categoryName.equals("Menús Especiales")) {
                if(shouldTreatAsSpecialOffer(menuItems)) {
                    for(Element menuItem: menuItems ) {
                        SpecialOffer specialOffer = buildSpecialOffer(menuItem);
                        if(specialOffer != null ) {
                            restaurant.getSpecialOffers().add(specialOffer);
                        }
                    }
                    continue;
                }
            }

            MenuCategory menuCategory = new MenuCategory();
            menuCategory.setType(MenuCategory.TYPE_STANDARD);
            menuCategory.setName(categoryName);
            String summary = category.parent().text().replaceFirst(categoryName,"");
            if(summary.startsWith("\"") && summary.endsWith("\"")) {
                summary = summary.substring(1,summary.length()-1);
            }
            if(StringUtils.hasText(summary)) {
                menuCategory.setSummary(summary);
            }

            List<List<Element>> menuItemList = new ArrayList<List<Element>>();
            String currentCls = "prdLi1";
            List<Element> elementList = new ArrayList<Element>();
            for(Element menuItem: menuItems ) {
                String cls = menuItem.attr("class");
                if(cls.equals(currentCls)) {
                    elementList.add(menuItem);                    
                }
                else {
                    if(elementList.size() > 0) {
                        menuItemList.add(elementList);
                    }
                    elementList = new ArrayList<Element>();
                    elementList.add(menuItem);
                    currentCls = cls;
                }
            }
            if(elementList.size() > 0 ) {
                menuItemList.add(elementList);
            }
            
            // Now parse out the elements
            for(List<Element> menuItemElements: menuItemList ) {
                MenuItem menuItem = new MenuItem();
                menuItem.setType(menuItemElements.size() == 1? MenuItem.TYPE_STANDARD: MenuItem.TYPE_SUBTYPE);
                Element detailElement = menuItemElements.get(0);
                String title = detailElement.select("td[class=prdDe] > h6").first().text();
                menuItem.setTitle(title);

                Element itemNumberElement = detailElement.select("td[class=prdNo] > h6").first();
                if(itemNumberElement != null ) {
                    String itemNumberText = itemNumberElement.text().replace(".","");
                    try {
                        Integer itemNumber = Integer.valueOf(itemNumberText);
                        menuItem.setNumber(itemNumber);
                    }
                    catch(NumberFormatException ignore) {
                        // Ignore on purpose;
                    }
                }

                try {
                    String description = detailElement.select("td[class=prdDe]").first().text().replaceFirst(title,"");
                    description = description.replaceAll("\n","").trim();
                    if(StringUtils.hasText(description)) {
                        menuItem.setDescription(description);
                    }
                }
                catch( Exception ex ) {
                    LOGGER.warn("Could not parse description for title: " + title);
                }
                if(menuItemElements.size() > 1 ) {
                    for(Element menuItemElement: menuItemElements ) {
                        Element subTypeElement = menuItemElement.select("td[class=prdAc]").first();
                        String subTypeName = subTypeElement.text().trim();
                        if(!StringUtils.hasText(subTypeName)) {
                            subTypeName = "-";
                        }
                        Element costElement = menuItemElement.select("td[class=prdPr]").first().select("div").get(1);
                        Double cost = Double.valueOf(costElement.text().replaceAll(",","."));
                        MenuItemSubType subType = new MenuItemSubType();
                        subType.setType(subTypeName);
                        subType.setCost(cost);
                        menuItem.getMenuItemSubTypes().add(subType);
                    }
                }
                else {
                    Element costElement = detailElement.select("td[class=prdPr]").first().select("div").get(1);
                    Double cost = Double.valueOf(costElement.text().replaceAll(",","."));
                    menuItem.setCost(cost);
                }
                menuCategory.getMenuItems().add(menuItem);
            }
            restaurant.getMenu().getMenuCategories().add(menuCategory);
        }
        return new Pair<Restaurant, String>(restaurant,imageUrl);
    }


    /**
     * @param menuItems
     * @return
     */

    private boolean shouldTreatAsSpecialOffer(Elements menuItems) {
        boolean ret = false;
        for(Element menuItem: menuItems ) {
            Element image = menuItem.select("img[class=PlusMC]").first();
            String linkOnclick = image.attr("onclick");
            String productId = linkOnclick.split("'")[1];
            String productUrl = "http://www.just-eat.es/pages/menuselectionpopupLegacy.aspx?pid=" + productId;
            try {
                Document options = getDocument(productUrl);
                Elements selects = options.select("select");
                if(selects.size() > 0) {
                    ret = true;
                    break;
                }
            }
            catch( Exception ignore) {
                // Do nothing on purpose;
            }
        }
        return ret;
    }


    /**
     * @param menuItem
     * @return
     */

    private SpecialOffer buildSpecialOffer(Element menuItem) {
        SpecialOffer specialOffer = new SpecialOffer();
        Element titleElement = menuItem.select("td[class=prdDe] > h6").first();
        if( titleElement == null ) {
            return null;
        }
        String title = titleElement.text();
        specialOffer.setTitle(title);
        Element itemNumberElement = menuItem.select("td[class=prdNo] > h6").first();
        if(itemNumberElement != null ) {
            String itemNumberText = itemNumberElement.text().replace(".","");
            try {
                Integer itemNumber = Integer.valueOf(itemNumberText);
                specialOffer.setNumber(itemNumber);
            }
            catch(NumberFormatException ignore) {
                // Ignore on purpose;
            }
        }
        try {
            String description = menuItem.select("td[class=prdDe]").first().text().replaceFirst(title,"");
            description = description.replaceAll("\n","").trim();
            if(StringUtils.hasText(description)) {
                specialOffer.setDescription(description);
            }
        }
        catch( Exception ex ) {
            LOGGER.warn("Could not parse description for title: " + title);
        }
        Element costElement = menuItem.select("td[class=prdPr]").first().select("div").get(1);
        Double cost = Double.valueOf(costElement.text().replaceAll(",","."));
        specialOffer.setCost(cost);

        // Now get the special offer item choices
        Element linkElement = menuItem.select("td[class=prdPlus]").first().select("img").first();
        String linkOnclick = linkElement.attr("onclick");
        String productId = linkOnclick.split("'")[1];
        String productUrl = "http://www.just-eat.es/pages/menuselectionpopupLegacy.aspx?pid=" + productId;
        try {
            Document options = getDocument(productUrl);
            Elements selects = options.select("select");
            for( int i = 0; i < selects.size(); i++ ) {
                Element select = selects.get(i);
                SpecialOfferItem specialOfferItem = new SpecialOfferItem();
                specialOfferItem.setTitle("Opción " + (i+1));
                Elements choices = select.select("option");
                for(int j = 1; j < choices.size(); j++) {
                    Element choice = choices.get(j);
                    String choiceText = choice.text();
                    Double additionalCost = 0d;
                    if(choiceText.contains("€")) {
                        String costText = choiceText.substring(choiceText.indexOf("€")+1).replace(",",".").trim();
                        additionalCost = Double.valueOf(costText);
                        choiceText = choiceText.substring(0,choiceText.indexOf("€")).trim();
                    }
                    specialOfferItem.getSpecialOfferItemChoices().add(choiceText);
                    specialOfferItem.getSpecialOfferItemChoiceCosts().add(additionalCost);
                }
                specialOffer.getSpecialOfferItems().add(specialOfferItem);
            }
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            return null;
        }

        for(int i = 1; i <=7; i++ ) {
            ApplicableTime time = new ApplicableTime();
            time.setDayOfWeek(i);
            time.setApplicable(true);
            specialOffer.getOfferApplicableTimes().add(time);
        }
        return specialOffer;
    }


    /**
     * @param url
     * @return
     * @throws Exception
     */

    private Document getDocument(String url) throws Exception {
        int maxAttempts = 5;
        int currentAttempt = 0;
        while(++currentAttempt < maxAttempts ) {
            try {
                return Jsoup.connect(url).get();
            }
            catch( Exception ex ) {
                LOGGER.info("Timed out, retrying....");
                Thread.sleep(2000);
            }
        }
        throw new Exception("Failed to get url: " + url);
    }


}
