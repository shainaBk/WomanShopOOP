package com.poo.womanshop.contoler;

import com.poo.womanshop.model.Accessories;
import com.poo.womanshop.model.Clothes;
import com.poo.womanshop.model.Product;
import com.poo.womanshop.model.Shoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

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

    @FXML
    private Button btn_add;

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_edit;

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

        // Initialize buttons status
        btn_add.setDisable(true);
        btn_delete.setDisable(true);
        btn_edit.setDisable(true);
    }

    private boolean validateField(TextField field, Predicate<String> validationRule) {
        String text = field.getText();
        if (!validationRule.test(text)) {
            field.getStyleClass().add("text-field-error");
            return false;
        } else {
            field.getStyleClass().removeAll("text-field-error");
            return true;
        }
    }

    @FXML
    public void addProduct() {
        String productType = cb_product_types.getValue();
        boolean isValid = true;

        isValid &= validateField(tf_pd_name, s -> !s.isEmpty());
        isValid &= validateField(tf_pd_price, s -> s.matches("\\d+(\\.\\d+)?") && Double.parseDouble(s) > 0);
        isValid &= validateField(tf_pd_stock, s -> s.matches("\\d+") && Integer.parseInt(s) > 0);
        if (!productType.equals("Accessories"))
            isValid &= validateField(tf_pd_size, s -> s.matches("\\d+") && Integer.parseInt(s) > 0);

        if (!isValid) {
            System.out.println("ERROR | Please fill all the fields correctly.");
            return;
        }

        Product product;
        String name = tf_pd_name.getText();
        double price = Double.parseDouble(tf_pd_price.getText());
        int stock = Integer.parseInt(tf_pd_stock.getText());
        int size = productType.equals("Accessories") ? 1 : Integer.parseInt(tf_pd_size.getText());

        try {
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
        } catch (Exception e) {
            System.out.println("ERROR | " + e.getMessage());
            return;
        }
        products.forEach(System.out::println);
    }

    @FXML
    public void changeProductType() {
        // Disable the size field if the product type is Accessories
        tf_pd_size.setText(null);
        tf_pd_size.getStyleClass().removeAll("text-field-error");
        tf_pd_size.setDisable(Objects.equals(cb_product_types.getValue(), "Accessories"));
        // Activate the add button if all the fields are filled
        btn_add.setDisable(false);
    }

    // TEST
}