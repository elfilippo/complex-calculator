package com.complexcalc;

import com.complexcalc.evaluator.LatexLexer;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.out.println(LatexLexer.tokenize("\\frac{3+\\sqrt{\\frac{b}{2}+8}}{4}"));
        System.exit(0);
        launch(args);
    }

    @Override
    @SuppressWarnings("exports")
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();

        scene = new Scene(root, 700, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("sigma");
        stage.show();
    }
}
