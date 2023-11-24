package com.poo.womanshop.model;

public class Shoes extends Product {

    private int shoeSize;

    public Shoes(String name, double price, int nbItems, int shoeSize) {
        super(name, price, nbItems);
        this.shoeSize = shoeSize;
    }
  public int getShoeSize() {
    return shoeSize;
  }
  public void setShoeSize(int shoeSize) {
    this.shoeSize = shoeSize;
  }

  @Override
  public String toString() {
    return "Shoes{" + super.toString()+
            " shoeSize=" + shoeSize +
            '}';
  }
  @Override
  public void applyDiscount() {
    this.setPrice(this.getPrice()*(1-DISCOUNT_SHOES));

    }
}
