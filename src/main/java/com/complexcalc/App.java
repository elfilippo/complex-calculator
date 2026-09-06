package com.complexcalc;

import com.complexcalc.ui.Controller;
import com.complexcalc.ui.UIManager;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//TODO: auto create documents folder, fix wrong popup scaling
//TODO: fix file explorer pointing to project root instead of document folder, fix being able to see/select files other than fslf
public class App extends Application {

    private static BorderlessScene scene;

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        launch(args);
    }

    @Override
    @SuppressWarnings("exports")
    public void start(Stage stage) throws IOException {
        stage.setTitle("complex-calculator v0.7.7 ©Filip M. 2026");
        stage.setHeight(600);
        stage.setWidth(800);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/complexcalc/textures/calculatorIcon.png")));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        scene = new BorderlessScene(stage, StageStyle.UNDECORATED, root, 400, 550);
        scene.getStylesheets().add(getClass().getResource("/com/complexcalc/themes/base.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/com/complexcalc/themes/lapis-blue.css").toExternalForm());
        stage.setScene(scene);

        scene.setMoveControl(controller.getToolBar());

        var uiManager = new UIManager(scene);
        controller.setUiManager(uiManager);
        controller.setBorderlessScene(scene);

        stage.show();
    }
}
