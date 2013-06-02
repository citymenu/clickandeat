package com.ezar.clickandeat.model;

public class SimpleValueObject {

    private String id;

    private double value;

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ValueObject [id=" + id + ", value=" + value + "]";
    }
}
