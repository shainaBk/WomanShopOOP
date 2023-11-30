package com.poo.womanshop.model;

import com.poo.womanshop.dao.ProductLoader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Administrator {
    private ObservableList<Product> listProducts;
    private DoubleProperty totalIncomes = new SimpleDoubleProperty();//Totalincomes c'est le total des prix des produits vendus
    private DoubleProperty totalCosts  = new SimpleDoubleProperty();//Total des prix des produits
    private DoubleProperty capital= new SimpleDoubleProperty();;//Capital = totalIncomes + capital c'est le capital de l'entreprise
    public Administrator(ObservableList<Product>list,double initialCapital){
        this.listProducts = list;
        this.totalIncomes.set(list.stream().mapToDouble(Product::getIncomes).sum());
        this.totalCosts.set(list.stream().mapToDouble(p -> p.getPrice() * p.getNbItems()).sum());
        this.capital.set(initialCapital + this.totalIncomes.get());

    }
    /**PART GETTER SETTER**/
    public ObservableList<Product> getListProducts(){return this.listProducts;}
    public void setListProducts(ObservableList<Product>p){this.listProducts=p;}
    public double getTotalIncomes(){return totalIncomes.get();}
    public void setTotalIncomes(double value) { totalIncomes.set(value); }
    public DoubleProperty totalIncomesProperty() { return totalIncomes; }
    public void updateTotalIncomes() {
        setTotalIncomes(this.listProducts.stream().mapToDouble(Product::getIncomes).sum());
    }
    public double getTotalCosts() { return totalCosts.get(); }
    public void setTotalCosts(double value) { totalCosts.set(value); }
    public DoubleProperty totalCostsProperty() { return totalCosts; }
    public void updateTotalCosts() {
        setTotalCosts(this.listProducts.stream().mapToDouble(p -> p.getPrice() * p.getNbItems()).sum());
    }
    public double getCapital() { return capital.get(); }
    public void setCapital(double value) { capital.set(value); }
    public DoubleProperty capitalProperty() { return capital; }

    public void updateCapital() {
        setCapital(getCapital() + getTotalIncomes());
    }


    /**PART ACTIONS**/
    //TODO: TESTER
    public  void addProduct(Product p) throws SQLException {
        //ON CONTROLER
        this.listProducts.add(p);
        //UPDATE TOTAL COSTS
        this.updateTotalCosts();
        //ON BDD
        ProductLoader.addProduct(p);
    }
    //TODO: TESTER
    //Via Produit
    public  void deleteProduct(Product p) throws SQLException {
        //ON CONTROLER
        this.listProducts.remove(p);
        //UPDATE TOTAL INCOMES => SI PRODUIT SUPPRIMER VENTE SUPPRIMER
        this.setTotalIncomes(this.getTotalIncomes()-p.getIncomes());
        //Pour update les prix des produits, on recalcule le total des prix des produits
        this.updateTotalCosts();
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
        //UPDATE TOTAL COSTS
        this.updateTotalCosts();
        //UPDATE TOTAL INCOMES au cas ou le
        this.updateTotalIncomes();
        //ON BDD
        ProductLoader.updateProduct(p);
    }

    //TODO: IMPLEMENTER DANS LA VIEW
    public void saleProduct(Product p,int nbItems) throws SQLException {
        //ON CONTROLER
        p.sell(nbItems);
        this.setTotalIncomes(this.getTotalIncomes()+p.getPrice()*nbItems);
        this.setCapital(this.getCapital()+p.getPrice()*nbItems);
        //Pour update les prix des produits, on recalcule le total des prix des produits
        this.updateTotalCosts();
        this.updateProduct(p);
        //ON BDD
        ProductLoader.updateProduct(p);
    }
    //TODO: FAIRE METHODE POUR BUY PRODUCT ET IMPLEMENTER DANS LA VIEW

    /**PART CREATION OBJ**/
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
