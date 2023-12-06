package com.poo.womanshop.model;

public class Discount {
    private String productType;
    private double discount_rate;

    public Discount(String productType, double discount_rate) {
        this.productType = productType;
        this.discount_rate = discount_rate;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public double getDiscountRate() {
        return this.discount_rate;
    }

    public void setDiscountRate(double discount_rate) {
        this.discount_rate = discount_rate;
    }
}
