package com.poo.womanshop.model;

import java.util.List;

public class Administrator {
    private List<Product> listProducts;
    public Administrator(List<Product>list){
        this.listProducts = list;
    }
    public List<Product> getListProducts(){return this.listProducts;}
    public void setListProducts(List<Product>p){this.listProducts=p;}
}
