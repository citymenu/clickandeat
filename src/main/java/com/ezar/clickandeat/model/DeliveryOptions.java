package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class DeliveryOptions {

    private static final int DEFAULT_DELIVERY_TIME = 45;
    private static final int DEFAULT_COLLECTION_TIME = 20;
    
    private String deliveryOptionsSummary;

    private int deliveryTimeMinutes;
    private int collectionTimeMinutes;

    // Does the restaurant do collection orders only
    private boolean collectionOnly;

    // Costings around delivery
    private Double minimumOrderForDelivery;
    private Double deliveryCharge;
    private boolean allowFreeDelivery;
    private Double minimumOrderForFreeDelivery;
    private boolean allowDeliveryBelowMinimumForFreeDelivery;

    private Double deliveryRadiusInKilometres;
    private List<String> areasDeliveredTo;

    private SortedSet<VariableDeliveryCharge> variableDeliveryCharges;
    private List<AreaDeliveryCharge> areaDeliveryCharges;
    private List<AreaDeliveryCharge> areaMinimumOrderCharges;

    public DeliveryOptions() {
        this.deliveryTimeMinutes = DEFAULT_DELIVERY_TIME;
        this.collectionTimeMinutes = DEFAULT_COLLECTION_TIME;
        this.areasDeliveredTo = new ArrayList<String>();
        this.areaDeliveryCharges = new ArrayList<AreaDeliveryCharge>();
        this.areaMinimumOrderCharges = new ArrayList<AreaDeliveryCharge>();
        this.variableDeliveryCharges = new TreeSet<VariableDeliveryCharge>();
    }


    /**
     * @param deliveryAddress
     * @param orderItemCost
     * @return
     */

    public Double getDeliveryCharge( Address deliveryAddress, double orderItemCost ) {

        // Variable delivery charges take precedence
        if( variableDeliveryCharges.size() > 0 ) {
            for( VariableDeliveryCharge variableDeliveryCharge: variableDeliveryCharges ) {
                if( orderItemCost >= variableDeliveryCharge.getMinimumOrderValue()) {
                    return variableDeliveryCharge.getDeliveryCharge();
                }
            }
        }
        else if( StringUtils.hasText(deliveryAddress.getPostCode())) {
            String postCode = deliveryAddress.getPostCode().toUpperCase();
            for( AreaDeliveryCharge areaDeliveryCharge: areaDeliveryCharges ) {
                for( String area: areaDeliveryCharge.getAreas()) {
                    if(postCode.contains(area.toUpperCase())) {
                        return areaDeliveryCharge.getDeliveryCharge();
                    }
                }
            }
        }
        return deliveryCharge;
    }


    /**
     * @param deliveryAddress
     * @return
     */

    public Double getMinimumOrderForDelivery( Address deliveryAddress ) {
        if( StringUtils.hasText(deliveryAddress.getPostCode())) {
            String postCode = deliveryAddress.getPostCode().toUpperCase();
            for( AreaDeliveryCharge areaDeliveryCharge: areaMinimumOrderCharges ) {
                for( String area: areaDeliveryCharge.getAreas()) {
                    if(postCode.contains(area.toUpperCase())) {
                        return areaDeliveryCharge.getDeliveryCharge();
                    }
                }
            }
        }
        return minimumOrderForDelivery;
    }

    public String getMinimumDeliveryCharge() {
        return NumberUtil.format(getMinimumAndMaximumDeliveryCharge()[0]);
    }

    public String getMaximumDeliveryCharge() {
        return NumberUtil.format(getMinimumAndMaximumDeliveryCharge()[1]);
    }

    private Double[] getMinimumAndMaximumDeliveryCharge() {
        Double minCharge = deliveryCharge;
        Double maxCharge = deliveryCharge;
        for( VariableDeliveryCharge variableDeliveryCharge: variableDeliveryCharges ) {
            minCharge = Math.min(variableDeliveryCharge.getDeliveryCharge(), minCharge);
            maxCharge = Math.max(variableDeliveryCharge.getDeliveryCharge(), maxCharge);
        }
        for( AreaDeliveryCharge areaDeliveryCharge: areaDeliveryCharges ) {
            minCharge = Math.min(areaDeliveryCharge.getDeliveryCharge(), minCharge);
            maxCharge = Math.max(areaDeliveryCharge.getDeliveryCharge(), maxCharge);
        }
        return new Double[]{minCharge, maxCharge};
    }
    
    public String getDeliveryOptionsSummary() {
        return deliveryOptionsSummary;
    }

    public void setDeliveryOptionsSummary(String deliveryOptionsSummary) {
        this.deliveryOptionsSummary = deliveryOptionsSummary;
    }

    public int getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }

    public void setDeliveryTimeMinutes(int deliveryTimeMinutes) {
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }

    public int getCollectionTimeMinutes() {
        return collectionTimeMinutes;
    }

    public void setCollectionTimeMinutes(int collectionTimeMinutes) {
        this.collectionTimeMinutes = collectionTimeMinutes;
    }

    public Double getMinimumOrderForFreeDelivery() {
        return minimumOrderForFreeDelivery;
    }

    public void setMinimumOrderForFreeDelivery(Double minimumOrderForFreeDelivery) {
        this.minimumOrderForFreeDelivery = minimumOrderForFreeDelivery;
    }

    public Double getMinimumOrderForDelivery() {
        return minimumOrderForDelivery;
    }

    public void setMinimumOrderForDelivery(Double minimumOrderForDelivery) {
        this.minimumOrderForDelivery = minimumOrderForDelivery;
    }

    public boolean isAllowFreeDelivery() {
        return allowFreeDelivery;
    }

    public void setAllowFreeDelivery(boolean allowFreeDelivery) {
        this.allowFreeDelivery = allowFreeDelivery;
    }

    public boolean isAllowDeliveryBelowMinimumForFreeDelivery() {
        return allowDeliveryBelowMinimumForFreeDelivery;
    }

    public void setAllowDeliveryBelowMinimumForFreeDelivery(boolean allowDeliveryBelowMinimumForFreeDelivery) {
        this.allowDeliveryBelowMinimumForFreeDelivery = allowDeliveryBelowMinimumForFreeDelivery;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public String getFormattedDeliveryCharge() {
        return NumberUtil.format(deliveryCharge);
    }
    
    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Double getDeliveryRadiusInKilometres() {
        return deliveryRadiusInKilometres;
    }

    public void setDeliveryRadiusInKilometres(Double deliveryRadiusInKilometres) {
        this.deliveryRadiusInKilometres = deliveryRadiusInKilometres;
    }

    public List<String> getAreasDeliveredTo() {
        return areasDeliveredTo;
    }

    public void setAreasDeliveredTo(List<String> areasDeliveredTo) {
        this.areasDeliveredTo = areasDeliveredTo;
    }

    public boolean isCollectionOnly() {
        return collectionOnly;
    }

    public void setCollectionOnly(boolean collectionOnly) {
        this.collectionOnly = collectionOnly;
    }

    public String getFormattedMinimumOrderForDelivery() {
        return NumberUtil.format(minimumOrderForDelivery);
    }

    public String getFormattedMinimumOrderForFreeDelivery() {
        return NumberUtil.format(minimumOrderForFreeDelivery);
    }

    public List<AreaDeliveryCharge> getAreaDeliveryCharges() {
        return areaDeliveryCharges;
    }

    public void setAreaDeliveryCharges(List<AreaDeliveryCharge> areaDeliveryCharges) {
        this.areaDeliveryCharges = areaDeliveryCharges;
    }

    public List<AreaDeliveryCharge> getAreaMinimumOrderCharges() {
        return areaMinimumOrderCharges;
    }

    public void setAreaMinimumOrderCharges(List<AreaDeliveryCharge> areaMinimumOrderCharges) {
        this.areaMinimumOrderCharges = areaMinimumOrderCharges;
    }
    
    public boolean getHasAdditionalDeliveryCharges() {
        return variableDeliveryCharges.size() > 0 || areaDeliveryCharges.size() > 0 || areaMinimumOrderCharges.size() > 0;
    }

    public SortedSet<VariableDeliveryCharge> getVariableDeliveryCharges() {
        return variableDeliveryCharges;
    }

    public void setVariableDeliveryCharges(SortedSet<VariableDeliveryCharge> variableDeliveryCharges) {
        this.variableDeliveryCharges = variableDeliveryCharges;
    }

    public boolean getHasVariableDeliveryCharges() {
        return variableDeliveryCharges.size() > 0;
    }
}
