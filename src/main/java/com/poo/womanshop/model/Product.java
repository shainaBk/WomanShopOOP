package com.poo.womanshop.model;

public abstract class Product implements Discount, Comparable<Product> {

    static int nb = 0;
    static double income = 0;
    private final int id;
    private String name;
    private double price;
    private int nbItems;

    public Product(String name, double price, int nbItems) {
        this.id = ++nb;
        this.name = name;
        setPrice(price);
        this.nbItems = nbItems;
    }

    public static int getNb() {
        return nb;
    }

    public static double getIncome() {
        return income;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws IllegalArgumentException {
        if (price >= 0) {
            this.price = price;
        } else throw new IllegalArgumentException("Price is negative");
    }

    public int getNbItems() {
        return nbItems;
    }

    public void setNbItems(int nbItems) {
        this.nbItems = nbItems;
    }

    public void sell(int nbItems) throws IllegalArgumentException {
        if (nbItems < this.nbItems) {
            this.setNbItems(this.nbItems - nbItems);
            income += nbItems * this.price;
            System.out.println("Sell OK");
        } else throw new IllegalArgumentException("Unavailable product");
    }

    public void purchase(int nbItems) throws IllegalArgumentException {
        if (nbItems > 0) {
            this.setNbItems(this.nbItems + nbItems);
            System.out.println("Purchase OK !");
        } else throw new IllegalArgumentException("Purchase with negative number !!");
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", nbItems=" + nbItems +
                '}';
    }

    @Override
    public int compareTo(Product o) {
        return Double.compare(this.getPrice(), o.getPrice());
    }
}
