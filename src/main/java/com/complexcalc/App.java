package com.complexcalc;

import com.complexcalc.ui.CalculatorUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        System.out.println(Character.isAlphabetic('-'));
        System.out.println(Parser.parse("-sinx^6-14y*-2.5i"));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        CalculatorUI ui = new CalculatorUI(primaryStage);

        primaryStage.setTitle("Calc");
        primaryStage.setScene(ui.getScene());
        primaryStage.show();
    }
}
