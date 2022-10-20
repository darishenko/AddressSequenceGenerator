module by.darishenko.addresssequencegenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens by.darishenko.addressSequenceGenerator to javafx.fxml;
    exports by.darishenko.addressSequenceGenerator;
    exports by.darishenko.addressSequenceGenerator.generator;
    opens by.darishenko.addressSequenceGenerator.generator to javafx.fxml;
    exports by.darishenko.addressSequenceGenerator.exception;
    opens by.darishenko.addressSequenceGenerator.exception to javafx.fxml;
}