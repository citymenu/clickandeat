package com.ezar.clickandeat.maps;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.AddressLocation;
import com.ezar.clickandeat.repository.AddressLocationRepository;
import flexjson.JSONDeserializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.geo.Metrics;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component(value = "locationService")
public class LocationService {

    private static final Logger LOGGER = Logger.getLogger(LocationService.class);

    private static final String MAP_URL = "http://maps.googleapis.com/maps/api/geocode/json?address={0}&sensor=false&components=country:{1}&language={2}";

    private static final double DIVISOR = Metrics.KILOMETERS.getMultiplier();
    
    @Autowired
    private AddressLocationRepository addressLocationRepository;
    
    private String locale;
    
    private String country;

    private Double warningRadius;

    private Double invalidRadius;


    /**
     * Gets a list of matching locations from the google maps api
     * @param address
     * @return
     */

    @SuppressWarnings("unchecked")
    public List<AddressLocation> getLocations( String address ) {

        if( !StringUtils.hasText(address)) {
            return new ArrayList<AddressLocation>();
        }
        
        List<AddressLocation> locations = new ArrayList<AddressLocation>();

        AddressLocation location = addressLocationRepository.findByAddress(address);
        if( location != null ) {
            locations.add(location);
            return locations;
        }
        
        try {
            URL url = new URL(MessageFormat.format(MAP_URL, URLEncoder.encode(address, "UTF-8"),country,locale));
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Map<String,Object> json = (Map<String,Object>)new JSONDeserializer().deserialize(in);
            List<Map<String,Object>> results = (List<Map<String,Object>>)json.get("results");
            for( Map<String,Object> result: results ) {

                String formattedAddress = (String)result.get("formatted_address");
                Map<String,Object> geometry = (Map<String,Object>)result.get("geometry");
                Map<String,Object> geolocation = (Map<String,Object>)geometry.get("location");
                double[] coordinates = new double[2];
                coordinates[0] = (Double)geolocation.get("lng");
                coordinates[1] = (Double)geolocation.get("lat");

                AddressLocation addressLocation = new AddressLocation();
                addressLocation.setAddress(address);
                addressLocation.setFormattedAddress(formattedAddress);
                addressLocation.setLocation(coordinates);
                
                Map<String,Object> bounds = (Map<String,Object>)geometry.get("bounds");
                if( bounds == null ) {
                    addressLocation.setRadius(0d);
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

                    double radius = getDistance(northeastcorner, southwestcorner) / 2;
                    addressLocation.setRadius(radius);
                    addressLocation.setRadiusWarning(isWarningRadius(addressLocation));
                    addressLocation.setRadiusInvalid(isInvalidRadius(addressLocation));
                }
                locations.add(addressLocation);
            }
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
        }
        
        if( locations.size() == 1 ) {
            addressLocationRepository.save(locations.get(0));
        }
        
        return locations;
    }


    /**
     * @param address
     * @return
     */

    public AddressLocation getSingleLocation(String address ) {
        List<AddressLocation> locations = getLocations(address);
        return locations.size() == 1? locations.get(0): null;
    }


    /**
     * Gets a list of matching locations from the google maps api
     * @param address
     * @return
     */

    @SuppressWarnings("unchecked")
    public List<AddressLocation> getLocations( Address address ) {

        if( address == null ) {
            return new ArrayList<AddressLocation>();
        }
        
        StringBuilder sb = new StringBuilder();
        if( address.getAddress1() != null ) {
            sb.append(address.getAddress1());
        }
        sb.append(" ");
        if( address.getPostCode() != null ) {
            sb.append(address.getPostCode());
        }
        
        return getLocations(sb.toString().trim());

    }


    /**
     * @param address
     * @return
     */

    public AddressLocation getSingleLocation(Address address ) {
        List<AddressLocation> locations = getLocations(address);
        return locations.size() == 1? locations.get(0): null;
    }


    /**
     * @param location
     * @return
     */

    public boolean isWarningRadius(AddressLocation location) {
        return location.getRadius() > warningRadius;
    }


    /**
     * @param location
     * @return
     */

    public boolean isInvalidRadius(AddressLocation location) {
        return location.getRadius() > invalidRadius;
    }

    
    /**
     * Returns the distance in kilometres between two locations 
     * @param location1
     * @param location2
     * @return
     */

    public double getDistance(double[] location1, double[] location2) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Determining distance between locations " +
                    Arrays.toString(location1) + " and " + Arrays.toString(location2));
        }

        double dLon = Math.toRadians(location1[0]-location2[0]);
        double dLat = Math.toRadians(location1[1]-location2[1]);

        double lat1 = Math.toRadians(location1[1]);
        double lat2 = Math.toRadians(location2[1]);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) *
                Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return Metrics.KILOMETERS.getMultiplier() * c;

    }


    @Required
    @Value(value="${location.locale}")
    public void setLocale(String locale) {
        this.locale = locale;
        this.country = locale.split("_")[1];
    }

    @Required
    @Value(value="${location.warningRadius}")
    public void setWarningRadius(Double warningRadius) {
        this.warningRadius = warningRadius;
    }

    @Required
    @Value(value="${location.invalidRadius}")
    public void setInvalidRadius(Double invalidRadius) {
        this.invalidRadius = invalidRadius;
    }

}

