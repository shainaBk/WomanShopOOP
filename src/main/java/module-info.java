module com.poo.womanshop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires java.dotenv;


    opens com.poo.womanshop.model to javafx.base;

    opens com.poo.womanshop to javafx.fxml;
    exports com.poo.womanshop;
    exports com.poo.womanshop.controller;
    opens com.poo.womanshop.controller to javafx.fxml;
}