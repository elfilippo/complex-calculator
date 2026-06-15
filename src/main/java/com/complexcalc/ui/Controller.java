package com.complexcalc.ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Controller {

    private RenderService renderService;
    private WebEngine previewEngine;
    private WebEngine documentEngine;
    private UIManager uiManager;

    @FXML
    private GridPane keyboardGrid;

    @FXML
    private WebView webPreview, documentWebView;

    @FXML
    private SplitPane calcSplitPane;

    @FXML
    private TextArea latexInput;

    @FXML
    private Button equalsButton;

    @FXML
    private ToggleGroup themeGroup;

    @FXML
    private RadioMenuItem blueTheme, charcoalTheme, flashbangTheme, clownTheme, rosePineTheme, solarizedTheme, tokyoNightTheme;

    @FXML
    private ImageView pIcon, sIcon;

    @FXML
    public void initialize() {
        String documentUrl = getClass().getResource("/com/complexcalc/document.html").toExternalForm();
        String previewUrl = getClass().getResource("/com/complexcalc/preview.html").toExternalForm();
        previewEngine = webPreview.getEngine();
        previewEngine.load(previewUrl);
        documentEngine = documentWebView.getEngine();
        documentEngine.load(documentUrl);

        webPreview.setContextMenuEnabled(false);

        documentWebView.setOnScroll(zoom(documentWebView));
        webPreview.setOnScroll(zoom(webPreview));

        latexInput.setOnScroll(event -> {
            if (event.isControlDown()) {
                double fontSize = latexInput.getFont().getSize();

                if (event.getDeltaY() > 0) {
                    fontSize *= 1.05;
                } else {
                    fontSize /= 1.05;
                }

                fontSize = Math.max(10, Math.min(72, fontSize));
                latexInput.setFont(new Font("Cambria Math", fontSize));

                event.consume();
            }
        });

        latexInput.setOnKeyTyped(event -> {
            if (renderService != null) renderService.render(latexInput.getText());
        });

        themeGroup
            .selectedToggleProperty()
            .addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == null) return;

                if (newToggle == blueTheme) {
                    uiManager.setTheme(0);
                } else if (newToggle == charcoalTheme) {
                    uiManager.setTheme(1);
                } else if (newToggle == flashbangTheme) {
                    uiManager.setTheme(2);
                } else if (newToggle == clownTheme) {
                    uiManager.setTheme(3);
                } else if (newToggle == rosePineTheme) {
                    uiManager.setTheme(4);
                } else if (newToggle == solarizedTheme) {
                    uiManager.setTheme(5);
                } else if (newToggle == tokyoNightTheme) {
                    uiManager.setTheme(6);
                }
            });

        calcSplitPane.setDividerPosition(0, 0.30);
        calcSplitPane.setDividerPosition(1, 0.40);

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

    private EventHandler<? super ScrollEvent> zoom(WebView webView) {
        return event -> {
            if (event.isControlDown()) {
                double zoom = webView.getZoom();

                if (event.getDeltaY() > 0) {
                    zoom *= 1.1;
                } else {
                    zoom /= 1.1;
                }

                zoom = Math.max(0.25, Math.min(3.0, zoom));
                webView.setZoom(zoom);

                event.consume();
            }
        };
    }

    @FXML
    private void hEquals() {
        System.out.println("sigma");
        System.out.println(previewEngine);
        renderService.render("\\frac{3}{4} \\] \\[ \\sum^{\\infty}_{n=0}{\\frac{1}{n}}");
    }

    @SuppressWarnings("exports")
    public WebEngine getPreviewEngine() {
        return previewEngine;
    }

    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }
}
