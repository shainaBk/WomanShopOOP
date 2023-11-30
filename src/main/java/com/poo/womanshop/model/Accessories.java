package com.poo.womanshop.model;

public class Accessories extends Product {

    public Accessories(String name, double price, int nbItems) {
        super(name, price, nbItems);
    }
    public Accessories(int id, String name, double price, int nbItems) {
        super(id, name, price, nbItems);
    }
    public Accessories(int id,String name, double price, int nbItems, double incomes) {
        super(id,name, price, nbItems, incomes);
    }
    @Override
    public String toString() {
        return "Accessories{" + super.toString() + "}";
    }

    @Override
    public void applyDiscount() {
        this.setPrice(this.getPrice() * (1 - DISCOUNT_ACCESSORIES));

    }
}
