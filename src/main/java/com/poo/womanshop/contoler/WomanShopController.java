package com.poo.womanshop.contoler;

import com.poo.womanshop.model.Accessories;
import com.poo.womanshop.model.Clothes;
import com.poo.womanshop.model.Product;
import com.poo.womanshop.model.Shoes;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class WomanShopController implements Initializable {

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private Product selectedProduct;

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

    @FXML
    private TableColumn<Product, Integer> cl_id;

    @FXML
    private TableColumn<Product, String> cl_name;

    @FXML
    private TableColumn<Product, Double> cl_price;

    @FXML
    private TableColumn<Product, String> cl_product;

    @FXML
    private TableColumn<Product, Integer> cl_size;

    @FXML
    private TableColumn<Product, Integer> cl_stock;

    @FXML
    private TableView<Product> tableProduct;

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
        clearMenu(true);

        // Initialize the table
        cl_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        cl_product.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getClass().getSimpleName()));
        cl_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        cl_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        cl_stock.setCellValueFactory(new PropertyValueFactory<>("nbItems"));
        cl_size.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            if (product instanceof Shoes) {
                return new ReadOnlyObjectWrapper<>(((Shoes) product).getShoeSize());
            } else if (product instanceof Clothes) {
                return new ReadOnlyObjectWrapper<>(((Clothes) product).getSize());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });

        // Actions on the table
        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Product selectedProduct = tableProduct.getSelectionModel().getSelectedItem();
                updateEditMenu(selectedProduct);
            }
        });
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

    private boolean validateProductFields(String productType) {
        boolean isValid = true;

        isValid &= validateField(tf_pd_name, s -> !s.isEmpty());
        isValid &= validateField(tf_pd_price, s -> s.matches("\\d+(\\.\\d+)?") && Double.parseDouble(s) > 0);
        isValid &= validateField(tf_pd_stock, s -> s.matches("\\d+") && Integer.parseInt(s) > 0);
        if (!productType.equals("Accessories"))
            isValid &= validateField(tf_pd_size, s -> s.matches("\\d+") && Integer.parseInt(s) > 0);

        return isValid;
    }

    @FXML
    public void addProduct() {
        String productType = cb_product_types.getValue();

        if (!validateProductFields(productType)) {
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

            productList.add(product);
        } catch (Exception e) {
            System.out.println("ERROR | " + e.getMessage());
            return;
        }

        productList.forEach(System.out::println);
        tableProduct.setItems(productList);
    }

    @FXML
    public void changeProductType() {
        // Disable the size field if the product type is Accessories
        tf_pd_name.setText(null);
        tf_pd_name.setDisable(false);
        tf_pd_price.setText(null);
        tf_pd_price.setDisable(false);
        tf_pd_stock.setText(null);
        tf_pd_stock.setDisable(false);
        tf_pd_size.setText(null);
        tf_pd_size.setDisable(Objects.equals(cb_product_types.getValue(), "Accessories"));
        // Remove the error class from the text fields
        tf_pd_name.getStyleClass().removeAll("text-field-error");
        tf_pd_price.getStyleClass().removeAll("text-field-error");
        tf_pd_stock.getStyleClass().removeAll("text-field-error");
        tf_pd_size.getStyleClass().removeAll("text-field-error");
        // Activate the add button if all the fields are filled
        btn_add.setDisable(false);
    }

    private void updateEditMenu(Product product) {
        this.selectedProduct = product;
        if (product != null) {
            clearMenu(false);

            tf_pd_name.setText(product.getName());
            tf_pd_price.setText(String.valueOf(product.getPrice()));
            tf_pd_stock.setText(String.valueOf(product.getNbItems()));
            if (product instanceof Clothes) {
                tf_pd_size.setText(String.valueOf(((Clothes) product).getSize()));
            } else if (product instanceof Shoes) {
                tf_pd_size.setText(String.valueOf(((Shoes) product).getShoeSize()));
            } else {
                tf_pd_size.setText("");
            }

            cb_product_types.setDisable(true);
            tf_pd_name.setDisable(false);
            tf_pd_price.setDisable(false);
            btn_edit.setDisable(false);
            btn_delete.setDisable(false);
        }
    }

    @FXML
    private void editProduct() {
        if (selectedProduct != null) {
            String productType = cb_product_types.getValue();

            if (!validateProductFields(productType)) {
                System.out.println("ERROR | Please fill all the fields correctly.");
                return;
            }

            String name = tf_pd_name.getText();
            double price = Double.parseDouble(tf_pd_price.getText());
            selectedProduct.setName(name);
            selectedProduct.setPrice(price);

            tableProduct.refresh();

            System.out.println("Product edited successfully: " + selectedProduct);
            clearMenu(true);
        }
    }

    private void clearMenu(Boolean clearSelectedProduct) {
        cb_product_types.setDisable(false);
        tf_pd_name.clear();
        tf_pd_name.setDisable(true);
        tf_pd_price.clear();
        tf_pd_price.setDisable(true);
        tf_pd_stock.clear();
        tf_pd_stock.setDisable(true);
        tf_pd_size.clear();
        tf_pd_size.setDisable(true);
        btn_add.setDisable(true);
        btn_edit.setDisable(true);
        btn_delete.setDisable(true);
        if (clearSelectedProduct) {
            selectedProduct = null;
            tableProduct.getSelectionModel().clearSelection();
        }
    }
}