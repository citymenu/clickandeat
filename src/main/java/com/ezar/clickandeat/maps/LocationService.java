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
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

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

    private int minComponentMatches;
    
    private List<String> componentPreferences = new ArrayList<String>();

    private List<String> commaBeforeComponents = new ArrayList<String>();
    
    
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
        
        AddressLocation savedLocation = addressLocationRepository.findByAddress(address);
        if( savedLocation != null ) {
            return Arrays.asList(savedLocation);
        }

        List<AddressLocation> locations = new ArrayList<AddressLocation>();

        try {
            URL url = new URL(MessageFormat.format(MAP_URL, URLEncoder.encode(address, "UTF-8"),country,locale));
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Map<String,Object> json = (Map<String,Object>)new JSONDeserializer().deserialize(in);
            List<Map<String,Object>> results = (List<Map<String,Object>>)json.get("results");
            for( Map<String,Object> result: results ) {

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
                String displayAddress;
                if( componentCount < minComponentMatches ) {
                    displayAddress = fullAddress;
                }
                else {
                    String concatenated = sb.toString();
                    if( concatenated.endsWith(",")) {
                        concatenated = concatenated.substring(0,concatenated.length() - 1 );
                    }
                    displayAddress = concatenated;
                }

                // Build address location object
                AddressLocation addressLocation = new AddressLocation();
                addressLocation.setAddress(address);
                addressLocation.setDisplayAddress(displayAddress);
                addressLocation.setFullAddress(fullAddress);
                addressLocation.setLocationComponents(locationAddressComponents);
                addressLocation.setLocation(coordinates);

                // Determine the geometry
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
                    addressLocation.setRadiusWarning(radius > warningRadius);
                }

                // Only include locations which are within the minimum bounds
                if( addressLocation.getRadius() < invalidRadius ) {
                    boolean locationMatchesSearch = true;
                    // Only include locations where the full address contains all words entered by the user
                    // Normalise accented characters
                    String fullAddressUpper = deAccent(addressLocation.getFullAddress().toUpperCase());
                    String[] searchCoponents = address.replace(",","").split(" " );
                    for( String searchComponent: searchCoponents ) {
                        if( !fullAddressUpper.contains(deAccent(searchComponent.toUpperCase()))) {
                            locationMatchesSearch = false;
                            break;
                        }
                    }
                    if( locationMatchesSearch ) {
                        locations.add(addressLocation);
                    }
                }
            }
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
        }

        // If we found an exact match, save the entry
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
        return DIVISOR * c;

    }


    /**
     * Remove accent from string
     * @param str
     * @return
     */

    private String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
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

}

