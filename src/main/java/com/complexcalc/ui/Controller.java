package com.complexcalc.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

public class Controller {

    @FXML
    private GridPane keyboardGrid;

    @FXML
    private WebView latexWebPreview;

    @FXML
    private SplitPane calcSplitPane;

    @FXML
    public void initialize() {
        String url = getClass().getResource("/com/complexcalc/index.html").toExternalForm();
        latexWebPreview.getEngine().load(url);

        latexWebPreview.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoom = latexWebPreview.getZoom();

                if (event.getDeltaY() > 0) {
                    zoom *= 1.1;
                } else {
                    zoom /= 1.1;
                }

                zoom = Math.max(0.5, Math.min(3.0, zoom));
                latexWebPreview.setZoom(zoom);

                event.consume();
            }
        });

        calcSplitPane.setDividerPosition(0, 0.30);
        calcSplitPane.setDividerPosition(1, 0.35);

        for (Node node : keyboardGrid.getChildren()) {
            if (node instanceof Button button) {
                ImageView icon = (ImageView) button.getGraphic();
                if (icon != null) {
                    icon.setPreserveRatio(true);
                    icon.fitWidthProperty().bind(button.widthProperty().multiply(0.6));
                    icon.fitHeightProperty().bind(button.heightProperty().multiply(0.6));
                }
            }
            if (node instanceof SplitMenuButton menuButton) {
                ImageView icon = (ImageView) menuButton.getGraphic();
                if (icon != null) {
                    icon.setPreserveRatio(true);
                    icon.fitWidthProperty().bind(menuButton.widthProperty().multiply(0.8));
                    icon.fitHeightProperty().bind(menuButton.heightProperty().multiply(0.8));
                }
            }
            if (node instanceof MenuButton menuButton) {
                ImageView icon = (ImageView) menuButton.getGraphic();
                if (icon != null) {
                    icon.setPreserveRatio(true);
                    icon.fitWidthProperty().bind(menuButton.widthProperty().multiply(0.8));
                    icon.fitHeightProperty().bind(menuButton.heightProperty().multiply(0.8));
                }
            }
        }
    }
}
