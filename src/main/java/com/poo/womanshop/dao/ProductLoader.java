package com.poo.womanshop.dao;
import com.poo.womanshop.model.Clothes;
import com.poo.womanshop.model.Product;
import com.poo.womanshop.model.Accessories;
import com.poo.womanshop.model.Shoes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ProductLoader {
    private static final Logger logger = LogManager.getLogger(ProductLoader.class);
    private static String userName = "shaina";
    private static String password = null;
    private static  String ipServer = "34.155.192.152";
    private static String url = "jdbc:mysql://"+ipServer+"/woman_shop_bdd";

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try{
             connection = DriverManager.getConnection(url,userName,password);
            logger.info("----- CONNECTION EFFECTUÉ -----");
        }catch (SQLException e) {
            logger.error("ERROR CONNECTION BDD: ",e);
        }
        return connection;
    }

    public static List<Product> loadProduct() throws SQLException {
        List<Product> products = new ArrayList<>();
        Connection connection = getConnection();
        String sql = "SELECT * FROM PRODUCT";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int nbItems = resultSet.getInt("nbItems");

                Product product = createProduct(connection, id, type, name, price, nbItems);
                products.add(product);
            }
            logger.info("DONE CREATION PRODUCT");
        } catch (SQLException e) {
            logger.error("ERREUR PREPARATION DE REQUETE: ",e);
        }
        return products;
    }

    private static Product createProduct(Connection connection, int id, String type, String name, double price, int nbItems) throws SQLException {
        switch (type) {
            case "CLOTHES":
                return loadClothes(connection, id, name, price, nbItems);
            case "SHOES":
                return loadShoes(connection, id, name, price, nbItems);
            case "ACCESSORIES":
                return new Accessories(id, name, price, nbItems);
            default:
                throw new IllegalArgumentException("Unknown product type: " + type);
        }
    }

    private static Product loadShoes(Connection connection, int id, String name, double price, int nbItems) throws SQLException {
        String sql = "SELECT shoeSize FROM SHOES WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int shoeSize = resultSet.getInt("shoeSize");
                return new Shoes(id, name, price, nbItems, shoeSize);
            }
        }
        throw new SQLException("Shoes not found with id: " + id);
    }

    private static Product loadClothes(Connection connection, int id, String name, double price, int nbItems) throws SQLException {
        String sql = "SELECT size FROM CLOTHES WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int size = resultSet.getInt("size");
                return new Clothes(id, name, price, nbItems, size);
            }
        }
        throw new SQLException("Clothes not found with id: " + id);
    }
}
