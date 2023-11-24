package com.poo.womanshop.model;

public interface Discount {
  double DISCOUNT_CLOTHES=0.3;
  double DISCOUNT_SHOES=0.2;
  double DISCOUNT_ACCESSORIES= 0.5;

  public void applyDiscount();

}
