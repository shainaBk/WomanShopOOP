package com.poo.womanshop.model;

import com.poo.womanshop.dao.ProductLoader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class Administrator {
    private final DoubleProperty totalIncomes = new SimpleDoubleProperty(); // Total of the price of sold products
    private final DoubleProperty totalCosts = new SimpleDoubleProperty(); // Total of the price of bought products
    private final DoubleProperty capital = new SimpleDoubleProperty(); // Total of the price of sold products - Total of the price of bought products
    private final ObservableList<Product> listProducts;
    private final ObservableList<Discount> listDiscounts;

    public Administrator(
            ObservableList<Product> listP,
            ObservableList<Discount> listD,
            double initialCapital
    ) {
        this.listProducts = listP;
        this.listDiscounts = listD;
        this.totalIncomes.set(listP.stream().mapToDouble(Product::getIncomes).sum());
        this.totalCosts.set(listP.stream().mapToDouble(Product::getCosts).sum());
        this.capital.set(initialCapital + this.totalIncomes.get() - this.totalCosts.get());
    }

    public ObservableList<Product> getListProducts() {
        return this.listProducts;
    }

    public ObservableList<Discount> getListDiscounts() {
        return this.listDiscounts;
    }

    public double getTotalIncomes() {
        return totalIncomes.get();
    }

    public void setTotalIncomes(double value) {
        totalIncomes.set(value);
    }

    public DoubleProperty totalIncomesProperty() {
        return totalIncomes;
    }

    public double getTotalCosts() {
        return totalCosts.get();
    }

    public void setTotalCosts(double value) {
        totalCosts.set(value);
    }

    public DoubleProperty totalCostsProperty() {
        return totalCosts;
    }

    public double getCapital() {
        return capital.get();
    }

    public void setCapital(double value) {
        capital.set(value);
    }

    public DoubleProperty capitalProperty() {
        return capital;
    }

    public void addProduct(Product p) throws SQLException {
        this.listProducts.add(p); //ON CONTROLLER
        ProductLoader.addProduct(p); //ON BDD
    }

    public void deleteProduct(Product p) throws SQLException {
        this.listProducts.remove(p);

        this.setCapital(this.getCapital() - p.getIncomes() + p.getCosts());
        this.setTotalIncomes(this.getTotalIncomes() - p.getIncomes());
        this.setTotalCosts(this.getTotalCosts() - p.getCosts());

        ProductLoader.deleteProduct(p.getId());
    }

    public void updateProduct(Product p) throws SQLException {
        for (int i = 0; i < listProducts.size(); i++) {
            if (listProducts.get(i).getId() == p.getId()) {
                listProducts.set(i, p); //ON CONTROLLER
                break;
            }
        }
        ProductLoader.updateProduct(p); //ON BDD
    }

    public void updateAllProducts() throws SQLException {
        for (Product p : listProducts) {
            ProductLoader.updateProduct(p);
        }
    }

    public void sellProduct(Product p, int nbItems) throws SQLException {
        if (p.getNbItems() < nbItems) throw new IllegalArgumentException("Not enough items in stock");

        p.sell(nbItems);
        this.setCapital(this.getCapital() + p.getPrice() * nbItems);
        this.setTotalIncomes(this.getTotalIncomes() + p.getPrice() * nbItems);
        this.updateProduct(p);
        ProductLoader.updateProduct(p);
    }

    public void buyProduct(Product p, int nbItems) throws SQLException {
        if (this.getCapital() < p.getPrice() * nbItems) throw new IllegalArgumentException("Not enough money");

        p.buy(nbItems);
        this.setCapital(this.getCapital() - p.getPrice() * nbItems);
        this.setTotalCosts(this.getTotalCosts() + p.getPrice() * nbItems);
        this.updateProduct(p);
        ProductLoader.updateProduct(p);
    }

    public Product createProduct(String productType, String name, double price, int stock, int size) {
        int id = this.listProducts.isEmpty() ? 1 : this.listProducts.getLast().getId() + 1;
        return switch (productType) {
            case "Clothes" -> new Clothes(id, name, price, stock, size);
            case "Shoes" -> new Shoes(id, name, price, stock, size);
            case "Accessories" -> new Accessories(id, name, price, stock);
            default -> throw new IllegalArgumentException("Invalid product type: " + productType);
        };
    }

    @Override
    public String toString() {
        return "List of product:\n" + listProducts.stream().map(Product::toString).collect(Collectors.joining("\n"));
    }

    public void applyDiscount(double discountCl, double discountSh, double discountAc) throws SQLException {
        validateDiscount(discountCl);
        validateDiscount(discountSh);
        validateDiscount(discountAc);

        for (Product p : this.listProducts) {
            double oldDiscountRate;
            switch (p.getClass().getSimpleName()) {
                case "Clothes":
                    oldDiscountRate = listDiscounts.get(0).getDiscountRate();
                    listDiscounts.set(0, new Discount("CLOTHES", discountCl));
                    break;
                case "Shoes":
                    oldDiscountRate = listDiscounts.get(1).getDiscountRate();
                    listDiscounts.set(1, new Discount("SHOES", discountSh));
                    break;
                case "Accessories":
                    oldDiscountRate = listDiscounts.get(2).getDiscountRate();
                    listDiscounts.set(2, new Discount("ACCESSORIES", discountAc));
                    break;
                default:
                    continue;
            }

            ProductLoader.updateDiscount(listDiscounts);

            double originalPrice = p.getPrice() / (1 - oldDiscountRate);

            double newDiscountRate = switch (p.getClass().getSimpleName()) {
                case "Clothes" -> discountCl;
                case "Shoes" -> discountSh;
                case "Accessories" -> discountAc;
                default -> 0.0;
            };
            p.setPrice(originalPrice * (1 - newDiscountRate));
        }

        updateAllProducts();
    }

    private void validateDiscount(double discount) {
        if (discount < 0 || discount >= 1) {
            throw new IllegalArgumentException("Discount must be between 0% and 90%");
        }
    }
}
