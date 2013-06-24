package com.ezar.clickandeat.scheduling;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.util.Pair;
import flexjson.JSONDeserializer;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.*;

//@Component(value="telephoneNumberScraper")
public class TelephoneNumberScraper implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(TelephoneNumberScraper.class);
    
    private static final String urlformat = "http://www.laneveraroja.com/buscar?your_place={0}&lat={1}&lng={2}&type=postal_code&calle={0}";

    private static final String restauranturlformat = "http://www.laneveraroja.com{0}";

    @Autowired
    private GeoLocationService geoLocationService;

    @Autowired
    private JavaMailSender javaMailSender;

    private final SortedSet<String> postcodes = new TreeSet<String>();
    
    private final JSONDeserializer deserializer = new JSONDeserializer();
    

    @Override
    public void afterPropertiesSet() throws Exception {
        
        geoLocationService.setLocale("es_ES");

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


    @SuppressWarnings("unchecked")
    public void scrapeData() throws Exception {
        final Map<String,Pair<String,String>> restaurantUrls = new HashMap<String,Pair<String,String>>();

        for(String postcode: postcodes ) {
            GeoLocation location = geoLocationService.getLocation(postcode);
            double[] latlng = location.getLocation();
            String searchUrl = MessageFormat.format(urlformat,postcode, latlng[1],latlng[0]);
            String restaurantJson = getDocumentJson(searchUrl);
            if(restaurantJson != null) {
                List<Map> jsonList = (List<Map>)deserializer.deserialize(restaurantJson);
                for(Map map: jsonList) {
                    String title = (String)map.get("tit");
                    String loc = (String)map.get("localidad");
                    String restaurantUrl = (String)map.get("url");
                    restaurantUrls.put(title,new Pair<String, String>(loc,restaurantUrl));
                }
            }
        }
        final SortedMap<String,String> telephones = new TreeMap<String, String>();
        for(Map.Entry<String,Pair<String,String>> entry: restaurantUrls.entrySet() ) {
            String url = entry.getValue().second;
            String restaurantJson = getRestaurantJson(MessageFormat.format(restauranturlformat,url));
            if(restaurantJson != null ) {
                Map jsonMap = (Map)deserializer.deserialize(restaurantJson);
                String telephone = (String)jsonMap.get("telefono");
                telephones.put(entry.getKey(),telephone);
            }
        }

        // Send an email report of all extracted telephone numbers
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo("mishimaltd@gmail.com");
                message.setFrom("noreply@llamarycomer.com");
                message.setSubject("Telephone number scraper report");
                StringBuilder sb = new StringBuilder();
                if(telephones.size() > 0 ) {
                    sb.append("Extracted the following telephone numbers:\n");
                    for(String restaurantName: telephones.keySet()) {
                        String town = restaurantUrls.get(restaurantName).first;
                        sb.append(restaurantName);
                        if( town != null ) {
                            sb.append(" (").append(town).append(")");
                        }
                        sb.append(" ").append(telephones.get(restaurantName));
                        sb.append("\n");
                    }
                }
                else {
                    sb.append("No telephone numbers extracted");
                }
                message.setText(sb.toString());
            }
        };
        javaMailSender.send(mimeMessagePreparator);
    }


    /**
     * @param url
     * @return
     * @throws Exception
     */

    private String getDocumentJson(String url) throws Exception {
        LOGGER.info("Getting restaurant list for url: " + url);
        String response = getContent(url);
        if(response.contains("obj_restaurantes=[]")) {
            return null;
        }
        int startIndex = response.indexOf("obj_restaurantes=");
        if(startIndex == -1 ) {
            return null;
        }
        return response.substring(startIndex + 17, response.indexOf("}]",startIndex) + 2);
    }


    /**
     * @param url
     * @return
     * @throws Exception
     */

    private String getRestaurantJson(String url) throws Exception {
        LOGGER.info("Getting restaurant json for url: " + url);
        String response = getContent(url);
        int startIndex = response.indexOf("estado=");
        if(startIndex == -1 ) {
            return null;
        }
        return response.substring(startIndex + 17, response.indexOf("};",startIndex) + 2);
    }

    /**
     * @param url
     * @return
     * @throws Exception
     */

    private String getContent(String url) throws Exception {
        int maxAttempts = 5;
        int currentAttempt = 0;
        while(++currentAttempt < maxAttempts ) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                return httpclient.execute(httpget, responseHandler);
            }
            catch( Exception ex ) {
                LOGGER.info("Timed out, retrying....");
                Thread.sleep(2000);
            }
        }
        throw new Exception("Failed to get url: " + url);
    }

}
