package com.poo.womanshop.dao;

import com.poo.womanshop.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class ProductLoader {
    final static String ipServer = "localhost:3306";
    private static final Logger logger = LogManager.getLogger(ProductLoader.class);
    final private static String userName = "mathis";
    final private static String password = "SqlConnection.123";
    final private static String url = "jdbc:mysql://" + ipServer + "/woman_shop_bdd";

    private static final Connection CONNECTION;

    static {
        try {
            CONNECTION = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to connect to the database
     *
     * @return Connection
     */
    private static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            logger.info("Connection to the database established successfully");
            return connection;
        } catch (SQLException e) {
            logger.error("Error during the connection to the database: ", e);
            throw e;
        }
    }

    /**
     * Method to load all products from the database
     *
     * @return ObservableList<Product>
     */
    public static ObservableList<Product> loadProduct() throws SQLException {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM PRODUCT";

        try (PreparedStatement statement = CONNECTION.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int nbItems = resultSet.getInt("nbItems");
                double incomes = resultSet.getDouble("incomes");
                double costs = resultSet.getDouble("costs");

                Product product = createProduct(id, type, name, price, nbItems, incomes, costs);
                products.add(product);
            }
            logger.info("Products loaded successfully");
        } catch (SQLException e) {
            logger.error("Error during loading products: ", e);
            throw e;
        }
        return products;
    }

    private static Product createProduct(int id, String type, String name, double price, int nbItems, double incomes, double costs) throws SQLException {
        return switch (type) {
            case "CLOTHES" -> loadClothes(id, name, price, nbItems, incomes, costs);
            case "SHOES" -> loadShoes(id, name, price, nbItems, incomes, costs);
            case "ACCESSORIES" -> new Accessories(id, name, price, nbItems, incomes, costs);
            default -> throw new IllegalArgumentException("Unknown product type: " + type);
        };
    }

    private static Product loadShoes(int id, String name, double price, int nbItems, double incomes, double costs) throws SQLException {
        String sql = "SELECT shoeSize FROM SHOES WHERE id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int shoeSize = resultSet.getInt("shoeSize");
                    return new Shoes(id, name, price, nbItems, shoeSize, incomes, costs);
                } else {
                    throw new SQLException("Shoes not found with id: " + id);
                }
            }
        }
    }

    private static Product loadClothes(int id, String name, double price, int nbItems, double incomes, double costs) throws SQLException {
        String sql = "SELECT size FROM CLOTHES WHERE id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int size = resultSet.getInt("size");
                    return new Clothes(id, name, price, nbItems, size, incomes, costs);
                } else {
                    throw new SQLException("Clothes not found with id: " + id);
                }
            }
        }
    }

    public static void addProduct(Product p) throws SQLException {
        String sqlProduct = "INSERT INTO PRODUCT (id, type, name, price, nbItems, incomes, costs) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlSpecific;
        PreparedStatement statementSpecific = null;

        try (PreparedStatement statementProduct = CONNECTION.prepareStatement(sqlProduct)) {
            statementProduct.setInt(1, p.getId());
            statementProduct.setString(2, p.getClass().getSimpleName().toUpperCase());
            statementProduct.setString(3, p.getName());
            statementProduct.setDouble(4, p.getPrice());
            statementProduct.setInt(5, p.getNbItems());
            statementProduct.setDouble(6, p.getIncomes());
            statementProduct.setDouble(7, p.getCosts());

            int affectedRows = statementProduct.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            int productId = p.getId();

            switch (p) {
                case Clothes clothes -> {
                    sqlSpecific = "INSERT INTO CLOTHES (id, size) VALUES (?, ?)";
                    statementSpecific = CONNECTION.prepareStatement(sqlSpecific);
                    statementSpecific.setInt(1, productId);
                    statementSpecific.setInt(2, clothes.getSize());
                }
                case Shoes shoes -> {
                    sqlSpecific = "INSERT INTO SHOES (id, shoeSize) VALUES (?, ?)";
                    statementSpecific = CONNECTION.prepareStatement(sqlSpecific);
                    statementSpecific.setInt(1, productId);
                    statementSpecific.setInt(2, shoes.getShoeSize());
                }
                case Accessories ignored -> {
                    sqlSpecific = "INSERT INTO ACCESSORIES (id) VALUES (?)";
                    statementSpecific = CONNECTION.prepareStatement(sqlSpecific);
                    statementSpecific.setInt(1, productId);
                }
                default -> {
                }
            }

            if (statementSpecific != null) {
                statementSpecific.executeUpdate();
            }
        } finally {
            if (statementSpecific != null) {
                statementSpecific.close();
            }
        }
    }


    public static void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM PRODUCT WHERE id = ?";

        try (PreparedStatement statement = CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new SQLException("No product found with id: " + id);
            }
        }
    }

    public static void updateProduct(Product p) throws SQLException {
        String sqlProduct = "UPDATE PRODUCT SET name = ?, price = ?, nbItems = ?, incomes = ?, costs = ? WHERE id = ?";
        String sqlSpecific = null;

        try (PreparedStatement statementProduct = CONNECTION.prepareStatement(sqlProduct)) {
            statementProduct.setString(1, p.getName());
            statementProduct.setDouble(2, p.getPrice());
            statementProduct.setInt(3, p.getNbItems());
            statementProduct.setDouble(4, p.getIncomes());
            statementProduct.setDouble(5, p.getCosts());
            statementProduct.setInt(6, p.getId());
            int rowsUpdated = statementProduct.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("No product found with id: " + p.getId());
            }

            if (p instanceof Clothes) {
                sqlSpecific = "UPDATE CLOTHES SET size = ? WHERE id = ?";
            } else if (p instanceof Shoes) {
                sqlSpecific = "UPDATE SHOES SET shoeSize = ? WHERE id = ?";
            }

            if (sqlSpecific != null) {
                try (PreparedStatement statementSpecific = CONNECTION.prepareStatement(sqlSpecific)) {
                    if (p instanceof Clothes) {
                        statementSpecific.setInt(1, ((Clothes) p).getSize());
                    } else {
                        statementSpecific.setInt(1, ((Shoes) p).getShoeSize());
                    }
                    statementSpecific.setInt(2, p.getId());
                    statementSpecific.executeUpdate();
                }
            }
        }
    }

    public static ObservableList<Discount> loadDiscount() throws SQLException {
        ObservableList<Discount> discounts = FXCollections.observableArrayList();
        String sql = "SELECT type, discount_rate FROM DISCOUNTS";

        try (PreparedStatement statementDiscount = CONNECTION.prepareStatement(sql);
             ResultSet resultSet = statementDiscount.executeQuery()) {

            while (resultSet.next()) {
                String productType = resultSet.getString("type");
                double discountRate = resultSet.getDouble("discount_rate");
                discounts.add(new Discount(productType, discountRate));
            }
        } catch (SQLException e) {
            logger.error("ERROR DURING LOADING DISCOUNTS: ", e);
            throw e;
        }

        return discounts;
    }

    public static void updateDiscount(ObservableList<Discount> discounts) throws SQLException {
        String sqlDiscount = "UPDATE DISCOUNTS SET discount_rate = ? WHERE type = ?";

        for (Discount discount : discounts) {
            try (PreparedStatement statementDiscount = CONNECTION.prepareStatement(sqlDiscount)) {
                statementDiscount.setDouble(1, discount.getDiscountRate());
                statementDiscount.setString(2, discount.getProductType());
                int rowsUpdated = statementDiscount.executeUpdate();

                if (rowsUpdated == 0) {
                    throw new SQLException("No discount found with product_type: " + discount.getProductType());
                }
            }
        }
    }
}
