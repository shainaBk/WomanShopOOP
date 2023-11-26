package com.poo.womanshop.contoler;

import com.poo.womanshop.dao.ProductLoader;
import com.poo.womanshop.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class WomanShopController implements Initializable {

    List<Product> products = new ArrayList<>();

    @FXML
    private CheckBox cb_accessories;

    @FXML
    private CheckBox cb_clothes;

    @FXML
    private ComboBox<String> cb_product_types;

    @FXML
    private CheckBox cb_shoes;

    @FXML
    private TextField tf_pd_name;

    @FXML
    private TextField tf_pd_price;

    @FXML
    private TextField tf_pd_size;

    @FXML
    private TextField tf_pd_stock;

    private Administrator admin;

    private static final Logger logger = LogManager.getLogger(WomanShopController.class);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /**LOADING DATA PART**/
        try {
            admin = new Administrator(ProductLoader.loadProduct());

            //TODO: fix pb log info
            logger.info("----- CHARGEMENT RÉUSSI ------\nSOME INFO:\nList size: "+admin.getListProducts().size()
                    +"\nFirst elems:"+admin.getListProducts().getFirst().toString()
                    +"\nLast elems:"+admin.getListProducts().getLast().toString());
        } catch (SQLException e) {
            logger.error("ERREUR CHARGEMENT DES PRODUIT: ",e);
        }
        System.out.println(admin.toString());

        /**INIT VIEW PART**/
        // Initialize the checkboxes: all of them are selected by default
        cb_clothes.setSelected(true);
        cb_shoes.setSelected(true);
        cb_accessories.setSelected(true);

        // Initialize the different types of products
        List<String> productTypes = List.of("Clothes", "Shoes", "Accessories");
        ObservableList<String> productTypesList = FXCollections.observableArrayList(productTypes);
        cb_product_types.setItems(productTypesList);
    }

    @FXML
    public void addProduct() {
        try {
            String productType = cb_product_types.getValue();
            String name = tf_pd_name.getText();
            double price = Double.parseDouble(tf_pd_price.getText());
            int stock = Integer.parseInt(tf_pd_stock.getText());
            int size = Integer.parseInt(tf_pd_size.getText());

            System.out.println(
                    "Adding " + productType.toUpperCase() +
                            " - name: " + name +
                            ", price: " + price +
                            ", stock: " + stock +
                            ", size: " + size
            );

            if (name.isEmpty() || price <= 0 || stock <= 0 || size <= 0) {
                System.out.println("Please fill all the fields");
                return;
            }

            Product product;
            if (productType.isEmpty()){
                System.out.println("Please select a product type");
                return;
            }else{
                product = admin.createProduct(productType,name,price,stock,size);
            }

            if (product != null) {
                admin.addProduct(product); // Utiliser admin pour ajouter le produit
            }
            products.forEach(System.out::println);
        } catch (NumberFormatException e) {
            logger.error("ERROR | Please enter a valid number");
        } catch (IllegalArgumentException e) {
            logger.error("ERROR | " + e.getMessage());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(admin.toString());
    }

    @FXML
    public void changeProductType() {
        // Disable the size field if the product type is Accessories
        tf_pd_size.setText(null);
        tf_pd_size.setDisable(Objects.equals(cb_product_types.getValue(), "Accessories"));
    }
}