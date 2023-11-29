package com.poo.womanshop.contoler;

import com.poo.womanshop.dao.ProductLoader;
import com.poo.womanshop.model.Administrator;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class WomanShopController implements Initializable {

    private static final Logger logger = LogManager.getLogger(WomanShopController.class);
    //private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private Product selectedProduct;
    private Administrator admin;
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
        System.out.println("test");
        /**LOADING DATA PART**/
        try {
            admin = new Administrator(ProductLoader.loadProduct());
            System.out.println("test");
        } catch (SQLException e) {
            logger.error("ERREUR CHARGEMENT DES PRODUIT: ", e);
        }

        /**INIT VIEW PART**/
        // Initialize the checkboxes: all of them are selected by default
        cb_clothes.setSelected(true);
        cb_shoes.setSelected(true);
        cb_accessories.setSelected(true);

        // Initialize the different types of products
        List<String> productTypes = List.of("Clothes", "Shoes", "Accessories");
        ObservableList<String> productTypesList = FXCollections.observableArrayList(productTypes);
        cb_product_types.setItems(productTypesList);

        // Initialize buttons status
        //TODO: Put in loop
        clearMenu(true);

        // Initialize the table view
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

        //load product to table
        tableProduct.setItems(admin.getListProducts());
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
            if (productType.isEmpty()) {
                System.out.println("Please select a product type");
                return;
            } else {
                product = admin.createProduct(productType, name, price, stock, size);
            }

            if (product != null) {
                admin.addProduct(product); // Utiliser admin pour ajouter le produit
                tableProduct.refresh();
            }
        } catch (NumberFormatException e) {
            logger.error("ERROR | Please enter a valid number");
        } catch (IllegalArgumentException e) {
            logger.error("ERROR | " + e.getMessage());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
    private void uptadeProduct() throws SQLException {
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
            //TODO: update product in database & modify admin.updateProduct(selectedProduct) to adaptet to the new method
            admin.updateProduct(selectedProduct);
            logger.info("Product edited successfully: " + selectedProduct);
            clearMenu(true);
        }
        else{
            logger.error("ERROR | Please select a product to edit.");
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