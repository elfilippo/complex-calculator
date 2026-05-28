package com.complexcalc;

import com.complexcalc.evaluator.Dual;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    public static void main(String[] args) {
        Dual test = new Dual(new double[] { 2.5, 1, 3, 1 });
        System.out.println(test);
        test = test.mult(new Dual(new double[] { 3, -2, -1, -4 }));
        System.out.println(test);
        //launch(args);
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
