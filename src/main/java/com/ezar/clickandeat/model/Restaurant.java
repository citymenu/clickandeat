package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="restaurants")
public class Restaurant {

    @Indexed(unique=true)
    private String restaurantId;

    private String name;
    
    
}
