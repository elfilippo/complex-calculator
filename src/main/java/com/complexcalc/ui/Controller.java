package com.complexcalc.ui;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class Controller {

    @FXML
    private WebView mathWebView;

    @FXML
    public void initialize() {
        mathWebView.getEngine().load(getClass().getResource("/com/complexcalc/mathlive/index.html").toExternalForm());
    }
}
