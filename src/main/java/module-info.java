module com.complexcalc {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires FX.BorderlessScene;

    opens com.complexcalc to javafx.fxml;
    opens com.complexcalc.ui to javafx.fxml;

    exports com.complexcalc;
    exports com.complexcalc.ui;
}
