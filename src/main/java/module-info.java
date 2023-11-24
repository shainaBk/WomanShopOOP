module com.poo.womanshop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.poo.womanshop to javafx.fxml;
    exports com.poo.womanshop;
    exports com.poo.womanshop.contoler;
    opens com.poo.womanshop.contoler to javafx.fxml;
}