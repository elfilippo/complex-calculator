module com.complexcalc {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.complexcalc to javafx.fxml;
    exports com.complexcalc;
    exports com.complexcalc.ui;
}
