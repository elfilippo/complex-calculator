package com.complexcalc.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CalculatorUI {

    private Scene scene;
    private GridPane gp;
    private Button[] buttons = {
        new Button("0"),
        new Button("1"),
        new Button("2"),
        new Button("3"),
        new Button("4"),
        new Button("5"),
        new Button("6"),
        new Button("7"),
        new Button("8"),
        new Button("9"),
        new Button("."),
        new Button("="),
    };

    /*private Button button0 = new Button("0"),
        button1 = new Button("1"),
        button2 = new Button("2"),
        button3 = new Button("3"),
        button4 = new Button("4"),
        button5 = new Button("5"),
        button6 = new Button("6"),
        button7 = new Button("7"),
        button8 = new Button("8"),
        button9 = new Button("9"),
        buttonComma = new Button("."),
        ButtonEquals = new Button("=");*/

    public CalculatorUI(Stage primaryStage) {
        gp = new GridPane(600, 400);
        scene = new Scene(gp);

        gp.add(buttons[1], 0, 0);
        gp.add(buttons[2], 1, 0);
        gp.add(buttons[3], 2, 0);
        gp.add(buttons[4], 0, 1);
        gp.add(buttons[5], 1, 1);
        gp.add(buttons[6], 2, 1);
        gp.add(buttons[7], 0, 2);
        gp.add(buttons[8], 1, 2);
        gp.add(buttons[9], 2, 2);
        gp.add(buttons[10], 0, 3);
        gp.add(buttons[0], 1, 3);
        gp.add(buttons[11], 2, 3);
    }

    public Scene getScene() {
        return scene;
    }
}
