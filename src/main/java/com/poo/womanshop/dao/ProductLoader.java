package com.poo.womanshop.dao;
import com.poo.womanshop.model.Clothes;
import com.poo.womanshop.model.Product;
import com.poo.womanshop.model.Accessories;
import com.poo.womanshop.model.Shoes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ProductLoader {
    private static final Logger logger = LogManager.getLogger(ProductLoader.class);
    private static String userName = "shaina";
    private static String password = null;
    private static  String ipServer = "34.155.192.152";
    private static String url = "jdbc:mysql://"+ipServer+"/woman_shop_bdd";

    private static Connection CONNECTION = null;

    static {
        try {
            CONNECTION = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getConnection() throws SQLException {
        Connection connection = null;
        try{
             connection = DriverManager.getConnection(url,userName,password);
            logger.info("----- CONNECTION EFFECTUÉ -----");
        }catch (SQLException e) {
            logger.error("ERROR CONNECTION BDD: ",e);
        }
        return connection;
    }

    public static ObservableList<Product> loadProduct() throws SQLException {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM PRODUCT";

        try(PreparedStatement statement = CONNECTION.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int nbItems = resultSet.getInt("nbItems");
                double incomes = resultSet.getDouble("incomes");

                Product product = createProduct(id, type, name, price, nbItems, incomes);
                products.add(product);
            }
            logger.info("DONE CREATION PRODUCT");
        } catch (SQLException e) {
            logger.error("ERREUR PREPARATION DE REQUETE: ",e);
        }
        return products;
    }

    private static Product createProduct(int id, String type, String name, double price, int nbItems, double incomes) throws SQLException {
        switch (type) {
            case "CLOTHES":
                return loadClothes(id, name, price, nbItems, incomes);
            case "SHOES":
                return loadShoes(id, name, price, nbItems, incomes);
            case "ACCESSORIES":
                return new Accessories(id, name, price, nbItems, incomes);
            default:
                throw new IllegalArgumentException("Unknown product type: " + type);
        }
    }

    private static Product loadShoes(int id, String name, double price, int nbItems, double incomes) throws SQLException {
        String sql = "SELECT shoeSize FROM SHOES WHERE id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int shoeSize = resultSet.getInt("shoeSize");
                return new Shoes(id, name, price, nbItems, shoeSize, incomes);
            }
        }
        throw new SQLException("Shoes not found with id: " + id);
    }

    private static Product loadClothes(int id, String name, double price, int nbItems, double incomes) throws SQLException {
        String sql = "SELECT size FROM CLOTHES WHERE id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int size = resultSet.getInt("size");
                return new Clothes(id, name, price, nbItems, size, incomes);
            }
        }
        throw new SQLException("Clothes not found with id: " + id);
    }

    //TODO: tester
    public static void addProduct(Product p) throws SQLException {
        String sqlProduct = "INSERT INTO PRODUCT (id, type, name, price, nbItems, incomes) VALUES (?, ?, ?, ?, ?,?)";
        String sqlSpecific = null;
        PreparedStatement statementSpecific = null;

        try (PreparedStatement statementProduct = CONNECTION.prepareStatement(sqlProduct)) {
            statementProduct.setInt(1, p.getId()); // Ajoutez l'id ici
            statementProduct.setString(2, p.getClass().getSimpleName().toUpperCase());
            statementProduct.setString(3, p.getName());
            statementProduct.setDouble(4, p.getPrice());
            statementProduct.setInt(5, p.getNbItems());
            statementProduct.setDouble(4, p.getIncomes());
            int affectedRows = statementProduct.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            int productId = p.getId(); // Utilisez l'id fourni
            if (p instanceof Clothes) {
                sqlSpecific = "INSERT INTO CLOTHES (id, size) VALUES (?, ?)";
                statementSpecific = CONNECTION.prepareStatement(sqlSpecific);
                statementSpecific.setInt(1, productId);
                statementSpecific.setInt(2, ((Clothes) p).getSize());
            } else if (p instanceof Shoes) {
                sqlSpecific = "INSERT INTO SHOES (id, shoeSize) VALUES (?, ?)";
                statementSpecific = CONNECTION.prepareStatement(sqlSpecific);
                statementSpecific.setInt(1, productId);
                statementSpecific.setInt(2, ((Shoes) p).getShoeSize());
            } else if (p instanceof Accessories) {
                sqlSpecific = "INSERT INTO ACCESSORIES (id) VALUES (?)";
                statementSpecific = CONNECTION.prepareStatement(sqlSpecific);
                statementSpecific.setInt(1, productId);
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

    //TODO: tester
    public static void updateProduct(Product p) throws SQLException {
        String sqlProduct = "UPDATE PRODUCT SET name = ?, price = ?, nbItems = ?, incomes = ? WHERE id = ?";
        String sqlSpecific = null;

        try (PreparedStatement statementProduct = CONNECTION.prepareStatement(sqlProduct)) {
            statementProduct.setString(1, p.getName());
            statementProduct.setDouble(2, p.getPrice());
            statementProduct.setInt(3, p.getNbItems());
            statementProduct.setDouble(4, p.getIncomes());
            statementProduct.setInt(5, p.getId());
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
                    } else if (p instanceof Shoes) {
                        statementSpecific.setInt(1, ((Shoes) p).getShoeSize());
                    }
                    statementSpecific.setInt(2, p.getId());
                    statementSpecific.executeUpdate();
                }
            }
        }
    }

}
