package com.complexcalc;

import com.complexcalc.ui.Bridge;
import com.complexcalc.ui.Controller;
import com.complexcalc.ui.RenderService;
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
        launch(args);
    }

    @Override
    @SuppressWarnings("exports")
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        var bridge = new Bridge();
        var renderService = new RenderService(controller.getPreviewEngine());
        controller.setRenderService(renderService);

        scene = new Scene(root, 700, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("sigma");
        stage.show();
    }
}
