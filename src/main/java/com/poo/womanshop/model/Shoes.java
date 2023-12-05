package com.poo.womanshop.model;

public class Shoes extends Product {
    private static double DISCOUNT_SHOES = 0.20d;
    private final int shoeSize;

    public Shoes(String name, double price, int nbItems, int shoeSize) {
        super(name, price, nbItems);
        this.shoeSize = shoeSize;
    }

    public Shoes(int id, String name, double price, int nbItems, int shoeSize) {
        super(id, name, price, nbItems);
        this.shoeSize = shoeSize;
    }

    public Shoes(int id, String name, double price, int nbItems, int shoeSize, double incomes, double costs) {
        super(id, name, price, nbItems, incomes, costs);
        this.shoeSize = shoeSize;
    }

    public static double setDiscountShoes(double discount) {
        return DISCOUNT_SHOES = discount;
    }

    public int getShoeSize() {
        return shoeSize;
    }

    @Override
    public String toString() {
        return "Shoes{" + super.toString() + " shoeSize=" + shoeSize + '}';
    }

    @Override
    public void applyDiscount() {
        this.setPrice(this.getPrice() * (1 - DISCOUNT_SHOES));
    }
}
