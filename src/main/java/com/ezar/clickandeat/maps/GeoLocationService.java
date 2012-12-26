package com.ezar.clickandeat.maps;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.repository.GeoLocationRepository;
import com.ezar.clickandeat.util.LocationUtils;
import flexjson.JSONDeserializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component(value = "locationService")
public class GeoLocationService  {

    private static final Logger LOGGER = Logger.getLogger(GeoLocationService.class);

    private static final String MAP_URL = "http://maps.googleapis.com/maps/api/geocode/json?address={0}&components=country:{1}&language={2}&sensor=false";

    private static final long CONCURRENT_WAIT_INTERVAL = 250;
    
    @Autowired
    private GeoLocationRepository geoLocationRepository;

    private String proxyUrl;
    
    private String locale;
    
    private String country;

    private Double warningRadius;

    private int minComponentMatches;
    
    private boolean useProxy;
    
    private List<String> componentPreferences = new ArrayList<String>();

    private List<String> commaBeforeComponents = new ArrayList<String>();
    
    private boolean cacheLocations = true;

    private final AtomicLong lastRequestTime = new AtomicLong(System.currentTimeMillis());


    /**
     * Gets a matching address location for a query
     * @param address
     * @return
     */

    @SuppressWarnings("unchecked")
    public GeoLocation getLocation( String address ) {

        LOGGER.debug("Looking up geolocation for address: " + address);
        
        if( !StringUtils.hasText(address)) {
            return null;
        }

        // Clean up the input
        address = address.trim();

        if( cacheLocations ) {
            GeoLocation savedLocation = geoLocationRepository.findByAddress(address);
            if( savedLocation != null ) {
                LOGGER.debug("Found saved location for address: " + address);
                if( !savedLocation.isValid()) {
                    LOGGER.warn("Saved location is not valid");
                    return null;
                }
                else {
                    return savedLocation;
                }
            }
        }

        try {

            synchronized (this) {

                // Throttle requests to maximum of 2 per second so we don't overload Google
                long lastRequestedTime = lastRequestTime.get();
                long now = System.currentTimeMillis();
                lastRequestTime.set(now);
                long difference = now - lastRequestedTime;

                if( difference < CONCURRENT_WAIT_INTERVAL ) {
                    try {
                        long wait = CONCURRENT_WAIT_INTERVAL - difference;
                        LOGGER.debug("Waiting " + wait + "ms before executing search");
                        Thread.sleep( wait );
                    }
                    catch( InterruptedException ignore ) {
                        // Ignore on purpose
                    }
                }

                URL url = new URL(MessageFormat.format(MAP_URL, URLEncoder.encode(address, "UTF-8"),country,locale));
                LOGGER.debug("Constructed url: " + url);
                URLConnection conn = useProxy? url.openConnection(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyUrl, 1080))): url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                Map<String,Object> json = (Map<String,Object>)new JSONDeserializer().deserialize(in);
                String status = (String)json.get("status");
                if( !"OK".equals(status)) {
                    LOGGER.error("Received status: " + status);
                    return null;
                }

                List<Map<String,Object>> results = (List<Map<String,Object>>)json.get("results");
                if( results.size() == 0 ) {
                    LOGGER.warn("Did not receive result for address: " + address);
                    saveInvalidGeoLocation(address);
                    return null;
                }

                LOGGER.debug("Found " + results.size() + " results for address: " + address);
                Map<String,Object> result = results.get(0);

                // Full text address
                String fullAddress = (String)result.get("formatted_address");

                // Extract address components
                Map<String,String> locationAddressComponents = new HashMap<String, String>();
                List addressComponents = (List)result.get("address_components");
                if( addressComponents != null ) {
                    for( Object entry: addressComponents ) {
                        Map<String,Object> addressComponent = (Map<String,Object>)entry;
                        List typesList = (List)addressComponent.get("types");
                        String type = (String)typesList.get(0);
                        String value = (String)addressComponent.get("long_name");
                        locationAddressComponents.put(type,value);
                    }
                }

                // Determine the geometry
                Map<String,Object> geometry = (Map<String,Object>)result.get("geometry");
                Map<String,Object> geolocation = (Map<String,Object>)geometry.get("location");
                double[] coordinates = new double[2];
                coordinates[0] = (Double)geolocation.get("lng");
                coordinates[1] = (Double)geolocation.get("lat");

                // Build the display address
                StringBuilder sb = new StringBuilder();
                String delim = "";
                int componentCount = 0;
                for( String componentPreference: componentPreferences ) {
                    String component = locationAddressComponents.get(componentPreference);
                    if( component != null ) {
                        if( commaBeforeComponents.contains(componentPreference)) {
                            sb.append(",");
                        }
                        sb.append(delim).append(component);
                        delim = " ";
                        componentCount++;
                        if( componentCount >= minComponentMatches ) {
                            break;
                        }
                    }
                }
                String concatenated = sb.toString();
                if( concatenated.endsWith(",")) {
                    concatenated = concatenated.substring(0,concatenated.length() - 1 );
                }
                String displayAddress = concatenated;
                if( !StringUtils.hasText(displayAddress)) {
                    LOGGER.warn("Address not specific enough to calculate display address");
                    saveInvalidGeoLocation(address);
                    return null;
                }

                LOGGER.debug("Constructed display address: " + displayAddress + " for address: " + address);

                // Build address location object
                GeoLocation geoLocation = new GeoLocation();
                geoLocation.setAddress(address);
                geoLocation.setDisplayAddress(displayAddress);
                geoLocation.setFullAddress(fullAddress);
                geoLocation.setLocationComponents(locationAddressComponents);
                geoLocation.setLocation(coordinates);

                // Determine the geometry
                Map<String,Object> bounds = (Map<String,Object>)geometry.get("bounds");
                if( bounds == null ) {
                    geoLocation.setRadius(0d);
                }
                else {
                    Map<String,Object> northeast = (Map<String,Object>)bounds.get("northeast");
                    double[] northeastcorner = new double[2];
                    northeastcorner[0] = (Double)northeast.get("lng");
                    northeastcorner[1] = (Double)northeast.get("lat");

                    Map<String,Object> southwest = (Map<String,Object>)bounds.get("southwest");
                    double[] southwestcorner = new double[2];
                    southwestcorner[0] = (Double)southwest.get("lng");
                    southwestcorner[1] = (Double)southwest.get("lat");

                    double radius = LocationUtils.getDistance(northeastcorner, southwestcorner) / 2;
                    geoLocation.setRadius(radius);
                    geoLocation.setRadiusWarning(radius > warningRadius);
                }

                // Save the location and return
                geoLocation.setValid(true);
                if( cacheLocations ) {
                    geoLocationRepository.saveGeoLocation(geoLocation);
                }
                return geoLocation;
            }
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            return null;
        }
    }


    /**
     * @param address
     * @return
     */

    public GeoLocation getLocation(Address address) {
        StringBuilder sb = new StringBuilder();
        if( StringUtils.hasText(address.getAddress1())) {
            sb.append(address.getAddress1()).append(" ");
        }
        if( StringUtils.hasText(address.getTown())) {
            sb.append(address.getTown()).append(" ");
        }
        if( StringUtils.hasText(address.getPostCode())) {
            sb.append(address.getPostCode());
        }
        return getLocation(sb.toString().trim());
    }
    

    /**
     * @param address
     */

    private void saveInvalidGeoLocation(String address) {
        GeoLocation geoLocation = new GeoLocation();
        geoLocation.setAddress(address);
        geoLocation.setValid(false);
        if( cacheLocations ) {
            geoLocationRepository.saveGeoLocation(geoLocation);
        }
    }


    @Required
    @Value(value="${location.proxyUrl}")
    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }


    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale.split("_")[0];
        this.country = locale.split("_")[1];
    }

    @Required
    @Value(value="${location.warningRadius}")
    public void setWarningRadius(Double warningRadius) {
        this.warningRadius = warningRadius;
    }

    @Required
    @Value(value="${location.mincomponentmatches}")
    public void setMinComponentMatches(int minComponentMatches) {
        this.minComponentMatches = minComponentMatches;
    }

    @Required
    @Value(value="${location.componentpreferences}")
    public void setComponentPreferences(String componentPreferences) {
        Collections.addAll(this.componentPreferences, componentPreferences.split(","));
    }

    @Required
    @Value(value="${location.commabeforecomponents}")
    public void setCommaBeforeComponents(String commaBeforeComponents) {
        Collections.addAll(this.commaBeforeComponents, commaBeforeComponents.split(","));
    }

    @Required
    @Value(value="${location.useProxy}")
    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    
    public void setCacheLocations(boolean cacheLocations) {
        this.cacheLocations = cacheLocations;
    }
}

