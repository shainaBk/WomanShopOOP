package com.poo.womanshop.contoler;

import com.poo.womanshop.model.Accessories;
import com.poo.womanshop.model.Clothes;
import com.poo.womanshop.model.Product;
import com.poo.womanshop.model.Shoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the checkboxes: all of them are selected by default
        cb_clothes.setSelected(true);
        cb_shoes.setSelected(true);
        cb_accessories.setSelected(true);

        // Initialize the different types of products
        List<String> productTypes = List.of("Clothes", "Shoes", "Accessories");
        ObservableList<String> products = FXCollections.observableArrayList(productTypes);
        cb_product_types.setItems(products);
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
            switch (productType) {
                case "Clothes":
                    product = new Clothes(name, price, stock, size);
                    break;
                case "Shoes":
                    product = new Shoes(name, price, stock, size);
                    break;
                case "Accessories":
                    product = new Accessories(name, price, stock);
                    break;
                default:
                    System.out.println("Please select a product type");
                    return;
            }

            products.add(product);
            products.forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("ERROR | Please enter a valid number");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR | " + e.getMessage());
        }
    }

    @FXML
    public void changeProductType() {
        // Disable the size field if the product type is Accessories
        tf_pd_size.setText(null);
        tf_pd_size.setDisable(Objects.equals(cb_product_types.getValue(), "Accessories"));
    }
}