package com.complexcalc.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Controller {

    @FXML
    private GridPane keyboardGrid;

    @FXML
    public void initialize() {
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
