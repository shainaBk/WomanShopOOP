package com.poo.womanshop.contoler;

import com.poo.womanshop.dao.ProductLoader;
import com.poo.womanshop.model.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
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
    Alert a = new Alert(Alert.AlertType.NONE);
    private FilteredList<Product> filteredList;
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
    private TableColumn<Product, Integer> cl_incomes;

    @FXML
    private TableColumn<Product, Integer> cl_costs;

    @FXML
    private TextField capital_field;

    @FXML
    private TextField incomes_field;

    @FXML
    private TextField globalCost_field;

    @FXML
    private TableView<Product> tableProduct;

    @FXML
    private Slider slider_clothes;

    @FXML
    private Slider slider_shoes;

    @FXML
    private Slider slider_accessories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Load the products from the database
            admin = new Administrator(ProductLoader.loadProduct(), 100);
        } catch (SQLException e) {
            logger.error("ERROR WHILE LOADING PRODUCTS FROM DATABASE");
        }

        this.initCheckBox(); // Init check box: all selected by default
        this.initProductType(); // Init product type on the combo box
        this.initTableView(); // Init table view
        this.initInfoAdmin(); // Init the info admin

        clearMenu(); // Clear the different fields
        this.loadTable(); // Load the data in the table view
    }

    public void handleError(Exception e, Alert.AlertType type) {
        a.setAlertType(type);
        a.setContentText(e.getMessage());
        a.show();
        logger.error(e.getMessage());
    }

    /**
     * INIT VIEW PART
     **/
    public void initCheckBox() {
        cb_clothes.setSelected(true);
        cb_shoes.setSelected(true);
        cb_accessories.setSelected(true);

        // Listener on the checkbox to update the filter
        cb_clothes.setOnAction(e -> updateFilter());
        cb_shoes.setOnAction(e -> updateFilter());
        cb_accessories.setOnAction(e -> updateFilter());
    }

    public void initProductType() {
        List<String> productTypes = List.of("Clothes", "Shoes", "Accessories");
        ObservableList<String> productTypesList = FXCollections.observableArrayList(productTypes);
        cb_product_types.setItems(productTypesList);
    }

    public void initTableView() {
        cl_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        cl_product.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getClass().getSimpleName()));
        cl_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        cl_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        cl_stock.setCellValueFactory(new PropertyValueFactory<>("nbItems"));
        cl_incomes.setCellValueFactory(new PropertyValueFactory<>("incomes"));
        cl_costs.setCellValueFactory(new PropertyValueFactory<>("costs"));
        cl_size.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(
                    switch (product) {
                        case Clothes clothes -> clothes.getSize();
                        case Shoes shoes -> shoes.getShoeSize();
                        default -> null;
                    }
            );
        });

        // Actions on the table: select a product
        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Product selectedProduct = tableProduct.getSelectionModel().getSelectedItem();
                changeProductType();
                updateEditMenu(selectedProduct);
            }
        });

        // Actions on the table: escape key to clear the selection
        tableProduct.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                tableProduct.getSelectionModel().clearSelection();
                clearMenu();
            }
        });
    }

    public void initInfoAdmin() {
        capital_field.textProperty().bind(admin.capitalProperty().asString("%.2f €"));
        incomes_field.textProperty().bind(admin.totalIncomesProperty().asString("%.2f €"));
        globalCost_field.textProperty().bind(admin.totalCostsProperty().asString("%.2f €"));
    }

    public void loadTable() {
        ObservableList<Product> originalList = admin.getListProducts();
        filteredList = new FilteredList<>(originalList);
        tableProduct.setItems(filteredList);
        updateFilter();
    }

    private void updateFilter() {
        if (filteredList != null) {
            filteredList.setPredicate(product ->
                    (cb_clothes.isSelected() || !(product instanceof Clothes)) &&
                            (cb_shoes.isSelected() || !(product instanceof Shoes)) &&
                            (cb_accessories.isSelected() || !(product instanceof Accessories))
            );
        }
    }

    /**
     * VALIDATION PART
     **/
    private boolean validateField(TextField field, Predicate<String> validationRule) {
        String text = field.getText();
        if (text == null || !validationRule.test(text)) {
            field.getStyleClass().add("text-field-error");
            return false;
        } else {
            field.getStyleClass().removeAll("text-field-error");
            return true;
        }
    }

    private boolean invalidProductFields(String productType) {
        boolean isValid = true;

        isValid &= validateField(tf_pd_name, s -> !s.isEmpty());
        isValid &= validateField(tf_pd_price, s -> s.matches("\\d+(\\.\\d+)?") && Double.parseDouble(s) > 0);

        if (!productType.equals("Accessories"))
            isValid &= validateField(tf_pd_size, s -> s.matches("\\d+") && Integer.parseInt(s) > 0);

        return !isValid;
    }

    public void clearMenuTextFields() {
        tf_pd_name.clear();
        tf_pd_price.clear();
        tf_pd_stock.clear();
        tf_pd_size.clear();
    }

    public void setDisableMenuTextFields(Boolean disable) {
        tf_pd_name.setDisable(disable);
        tf_pd_price.setDisable(disable);
        tf_pd_stock.setDisable(disable);
        tf_pd_size.setDisable(Objects.equals(cb_product_types.getValue(), "Accessories") || disable);
    }

    public void clearMenuStyle() {
        tf_pd_name.getStyleClass().removeAll("text-field-error");
        tf_pd_price.getStyleClass().removeAll("text-field-error");
        tf_pd_stock.getStyleClass().removeAll("text-field-error");
        tf_pd_size.getStyleClass().removeAll("text-field-error");
    }

    @FXML
    public void changeProductType() {
        clearMenuTextFields();
        setDisableMenuTextFields(false);
        clearMenuStyle();

        tf_pd_stock.setDisable(true);
        btn_add.setDisable(false);
    }

    private void clearMenu() {
        cb_product_types.getSelectionModel().clearSelection();
        cb_product_types.setDisable(false);

        clearMenuTextFields();
        setDisableMenuTextFields(true);

        btn_add.setDisable(true);
        btn_edit.setDisable(true);
        btn_delete.setDisable(true);

        selectedProduct = null;
        tableProduct.getSelectionModel().clearSelection();
    }

    private void updateEditMenu(Product product) {
        this.selectedProduct = product;
        if (product != null) {
            cb_product_types.setValue(product.getClass().getSimpleName());
            tf_pd_name.setText(product.getName());
            tf_pd_price.setText(String.valueOf(product.getPrice()));
            tf_pd_stock.setText(String.valueOf(product.getNbItems()));
            switch (product) {
                case Clothes clothes -> tf_pd_size.setText(String.valueOf(clothes.getSize()));
                case Shoes shoes -> tf_pd_size.setText(String.valueOf(shoes.getShoeSize()));
                default -> tf_pd_size.setText("");
            }
            tf_pd_stock.setDisable(true);
            tf_pd_size.setDisable(true);

            cb_product_types.setDisable(true);
            tf_pd_name.setDisable(false);
            tf_pd_price.setDisable(false);

            btn_add.setDisable(true);
            btn_edit.setDisable(false);
            btn_delete.setDisable(false);
        }
    }

    @FXML
    private void updateProduct() throws SQLException {
        try {
            if (selectedProduct == null)
                throw new IllegalArgumentException("Please select a product to edit.");

            String productType = cb_product_types.getValue();

            if (invalidProductFields(productType))
                throw new IllegalArgumentException("Please fill all the fields correctly.");

            String name = tf_pd_name.getText();
            double price = Double.parseDouble(tf_pd_price.getText());
            selectedProduct.setName(name);
            selectedProduct.setPrice(price);

            tableProduct.refresh();
            admin.updateProduct(selectedProduct);
            clearMenu();
        } catch (IllegalArgumentException e) {
            handleError(e, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void addProduct() {
        try {
            String productType = cb_product_types.getValue();

            if (invalidProductFields(productType))
                throw new IllegalArgumentException("Please fill all the fields correctly.");

            String name = tf_pd_name.getText();
            double price = Double.parseDouble(tf_pd_price.getText());
            int size = productType.equals("Accessories") ? 1 : Integer.parseInt(tf_pd_size.getText());
            Product product = admin.createProduct(productType, name, price, 0, size);

            if (product != null) {
                admin.addProduct(product);
                clearMenu();
                tableProduct.refresh();
            }
        } catch (IllegalArgumentException | SQLException e) {
            handleError(e, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void deleteProduct() {
        try {
            if (selectedProduct == null)
                throw new IllegalArgumentException("Please select a product to delete.");
            admin.deleteProduct(selectedProduct);
            clearMenu();
            tableProduct.refresh();
        } catch (SQLException | IllegalArgumentException e) {
            handleError(e, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void sellProduct() {
        try {
            if (selectedProduct == null)
                throw new IllegalArgumentException("Please select a product to sell.");
            admin.sellProduct(selectedProduct, 1);
            tableProduct.refresh();
        } catch (SQLException | IllegalArgumentException e) {
            handleError(e, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void buyProduct() {
        try {
            if (selectedProduct == null)
                throw new IllegalArgumentException("Please select a product to buy.");
            admin.buyProduct(selectedProduct, 1);
            tableProduct.refresh();
        } catch (SQLException | IllegalArgumentException e) {
            handleError(e, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void applyDiscount() {
        try {
            double discount_cl = slider_clothes.getValue() / 100;
            double discount_sh = slider_shoes.getValue() / 100;
            double discount_ac = slider_accessories.getValue() / 100;

            admin.applyDiscount(discount_cl, discount_sh, discount_ac);
            System.out.println("Discounts applied: cl=" + discount_cl + ", sh=" + discount_sh + ", ac=" + discount_ac);
            tableProduct.refresh();

            slider_clothes.setValue(0);
            slider_shoes.setValue(0);
            slider_accessories.setValue(0);
        } catch (SQLException | IllegalArgumentException e) {
            handleError(e, Alert.AlertType.WARNING);
        }
    }
}