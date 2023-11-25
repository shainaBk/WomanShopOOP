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
    private  void addProduct(Product p) throws SQLException {
        //ON CONTROLER
        this.listProducts.add(p);
        //ON BDD
        ProductLoader.addProduct(p);
    }

    //TODO: TESTER
    private  void deleteProduct(Product p) throws SQLException {
        //ON CONTROLER
        this.listProducts.remove(p);
        //ON BDD
        ProductLoader.deleteProduct(p.getId());
    }
    //TODO: TESTER
    private  void updateProduct(Product pOld, Product p) throws SQLException {
        //ON CONTROLER
        int index = this.listProducts.indexOf(p);
        this.listProducts.add(index,p);
        //ON BDD
        ProductLoader.updateProduct(p);
    }
}
