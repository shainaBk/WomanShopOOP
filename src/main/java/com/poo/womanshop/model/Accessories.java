package com.poo.womanshop.model;

public class Accessories extends Product {
    private static double DISCOUNT_ACCESSORIES = 0.0d;

    public Accessories(String name, double price, int nbItems) {
        super(name, price, nbItems);
    }

    public Accessories(int id, String name, double price, int nbItems) {
        super(id, name, price, nbItems);
    }

    public Accessories(int id, String name, double price, int nbItems, double incomes, double costs) {
        super(id, name, price, nbItems, incomes, costs);
    }

    public static double setDiscountAccessories(double discount) {
        return DISCOUNT_ACCESSORIES = discount;
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
