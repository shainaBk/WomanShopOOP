package com.poo.womanshop.model;

public abstract class Product implements InterDiscount, Comparable<Product> {
    private static int nb_increment = 0;

    private final int id;
    private String name;
    private double price;
    private int nbItems;

    private double incomes;
    private double costs;

    public Product(String name, double price, int nbItems) {
        this.id = ++nb_increment;
        this.name = name;
        setPrice(price);
        this.nbItems = nbItems;
        this.incomes = 0;
    }

    public Product(int id, String name, double price, int nbItems) {
        this.id = id;
        this.name = name;
        setPrice(price);
        this.nbItems = nbItems;

        this.incomes = 0;
        this.costs = 0;
    }

    public Product(int id, String name, double price, int nbItems, double incomes, double costs) {
        this.id = id;
        this.name = name;
        setPrice(price);
        this.nbItems = nbItems;
        this.incomes = incomes;
        this.costs = costs;
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
            this.price = Math.round(price * 100.0) / 100.0;
        } else throw new IllegalArgumentException("Price is negative");
    }

    public int getNbItems() {
        return nbItems;
    }

    public void setNbItems(int nbItems) {
        this.nbItems = nbItems;
    }

    public double getIncomes() {
        return incomes;
    }

    public void setIncomes(double incomes) {
        this.incomes = incomes;
    }

    public double getCosts() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs = costs;
    }

    public void sell(int nbItems) throws IllegalArgumentException {
        if (nbItems <= this.nbItems) {
            this.setNbItems(this.nbItems - nbItems);
            this.setIncomes(this.incomes + nbItems * this.price);
            System.out.println(name + " x" + nbItems + " sold  for " + nbItems * this.price + "€");
        } else throw new IllegalArgumentException("Not enough items in stock");
    }

    public void buy(int nbItems) {
        if (nbItems > 0) {
            this.setNbItems(this.nbItems + nbItems);
            this.setCosts(this.costs + nbItems * this.price);
            System.out.println(name + " x" + nbItems + " bought for " + nbItems * this.price + "€");
        } else throw new IllegalArgumentException("Negative number of items");
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name='" + name + '\'' + ", price=" + price + ", nbItems=" + nbItems + "}";
    }

    @Override
    public int compareTo(Product o) {
        return Double.compare(this.getPrice(), o.getPrice());
    }
}
