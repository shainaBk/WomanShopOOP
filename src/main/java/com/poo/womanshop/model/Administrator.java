package com.poo.womanshop.model;

import com.poo.womanshop.dao.ProductLoader;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Administrator {
    private ObservableList<Product> listProducts;
    public Administrator(ObservableList<Product>list){
        this.listProducts = list;
    }
    public ObservableList<Product> getListProducts(){return this.listProducts;}
    public void setListProducts(ObservableList<Product>p){this.listProducts=p;}

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
    public  void updateProduct(Product pOld, Product p) throws SQLException {
        //ON CONTROLER
        int index = this.listProducts.indexOf(p);
        this.listProducts.add(index,p);
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
