package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="restaurants")
public class Sequence extends PersistentObject {
    
    private String name;
    
    private Long counter;

    public Sequence() {
    }

    public Sequence(String name, Long counter) {
        this.name = name;
        this.counter = counter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }
}
