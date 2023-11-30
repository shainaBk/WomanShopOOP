package com.poo.womanshop.model;

import com.poo.womanshop.dao.ProductLoader;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Administrator {
    private ObservableList<Product> listProducts;
    private double totalSales;
    public Administrator(ObservableList<Product>list){
        this.listProducts = list;
        this.totalSales = 0;
    }
    public ObservableList<Product> getListProducts(){return this.listProducts;}
    public void setListProducts(ObservableList<Product>p){this.listProducts=p;}
    public double getTotalSales(){return this.totalSales;}
    public void setTotalSales(double totalSales){this.totalSales=totalSales;}

    //TODO: TESTER
    public  void addProduct(Product p) throws SQLException {
        //ON CONTROLER
        this.listProducts.add(p);
        //ON BDD
        ProductLoader.addProduct(p);
    }
    //TODO: TESTER
    //Via Produit
    public  void deleteProduct(Product p) throws SQLException {
        //ON CONTROLER
        this.listProducts.remove(p);
        //ON BDD
        ProductLoader.deleteProduct(p.getId());
    }
    //via ID
    public void deleteProduct(int id) throws SQLException {
        //ON CONTROLER to complete...
        Product productToRemove = null;
        for (Product product : listProducts) {
            if (product.getId() == id) {
                productToRemove = product;
                break;
            }
        }
        if (productToRemove != null) {
            listProducts.remove(productToRemove);
        }
        //ON BDD
        ProductLoader.deleteProduct(id);
    }
    //TODO: TESTER
    public  void updateProduct(Product p) throws SQLException {
        //ON CONTROLER
        for (int i = 0; i < listProducts.size(); i++) {
            if (listProducts.get(i).getId() == p.getId()) {
                listProducts.set(i, p);
                break;
            }
        }
        //ON BDD
        ProductLoader.updateProduct(p);
    }
    public void saleProduct(Product p,int nbItems) throws SQLException {
        //ON CONTROLER
        p.sell(nbItems);
        this.setTotalSales(this.getTotalSales()+p.getPrice()*nbItems);
        //ON BDD
        ProductLoader.updateProduct(p);
    }

    public Product createProduct(String productType,String name,double price,int stock, int size){
        Product product = null;
        if(!this.listProducts.isEmpty()){
            int id = this.listProducts.getLast().getId() +1;
            System.out.println("new id"+id);
            switch (productType) {
                case "Clothes":
                    product = new Clothes(id,name, price, stock, size);
                    break;
                case "Shoes":
                    product= new Shoes(id,name, price, stock, size);
                    break;
                case "Accessories":
                    product= new Accessories(id,name, price, stock);
                    break;
            }
        }
        else{
            switch (productType) {
                case "Clothes":
                    product = new Clothes(name, price, stock, size);
                    break;
                case "Shoes":
                    product= new Shoes(name, price, stock, size);
                    break;
                case "Accessories":
                    product= new Accessories(name, price, stock);
                    break;
            }
        }
        return product;
    }


    @Override
    public String toString() {
        String result ="List of product:\n";
        for (Product p:
             this.listProducts) {
            result+=p.toString()+"\n";

        }
        return result;
    }
}
