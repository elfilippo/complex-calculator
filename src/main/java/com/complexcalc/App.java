package com.complexcalc;

import com.complexcalc.ui.Controller;
import com.complexcalc.ui.RenderService;
import com.complexcalc.ui.UIManager;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private static BorderlessScene scene;

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
        var renderService = new RenderService(controller.getPreviewEngine());
        controller.setRenderService(renderService);

        scene = new BorderlessScene(stage, StageStyle.UNDECORATED, root, 700, 800);
        scene.getStylesheets().add(getClass().getResource("/com/complexcalc/themes/default.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/com/complexcalc/themes/blue.css").toExternalForm());
        stage.setScene(scene);

        scene.setMoveControl(controller.getToolBar());

        var uiManager = new UIManager(scene);
        controller.setUiManager(uiManager);
        controller.setBorderlessScene(scene);

        stage.setTitle("sigma");
        stage.show();
    }
}
