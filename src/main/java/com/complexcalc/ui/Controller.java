package com.complexcalc.ui;

import com.complexcalc.parser.LatexComplexEvaluator;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Controller {

    private RenderService renderService;
    private WebEngine previewEngine;
    private WebEngine documentEngine;
    private UIManager uiManager;
    private BorderlessScene scene;

    @FXML
    private GridPane keyboardGrid;

    @FXML
    private WebView webPreview, documentWebView;

    @FXML
    private SplitPane calcSplitPane, documentSplitPane;

    @FXML
    private TextArea latexInput;

    @FXML
    private Button clearBtn, delBtn;

    @FXML
    private SplitMenuButton approxBtn, lParenBtn, rParenBtn, derivBtn, integralBtn;

    @FXML
    private ToggleGroup themeGroup;

    @FXML
    private RadioMenuItem blueTheme, charcoalTheme, flashbangTheme, clownTheme, rosePineTheme, solarizedTheme, tokyoNightTheme;

    @FXML
    private ImageView pIcon, sIcon;

    @FXML
    private ToolBar toolBar;

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

        Platform.runLater(latexInput::requestFocus);

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

        latexInput.setOnKeyTyped(this::autoEval);

        latexInput
            .focusedProperty()
            .addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    Platform.runLater(() -> {
                        if (!anyPopupShowing()) {
                            latexInput.requestFocus();
                        }
                    });
                }
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

        Divider divider = documentSplitPane.getDividers().get(0);
        divider.setPosition(0.25);
        divider
            .positionProperty()
            .addListener((obs, oldPos, newPos) -> {
                double min = 0.2;
                double max = 0.8;

                if (newPos.doubleValue() < min) {
                    divider.setPosition(min);
                } else if (newPos.doubleValue() > max) {
                    divider.setPosition(max);
                }
            });

        for (Node node : keyboardGrid.getChildren()) {
            if (node instanceof Button button) {
                ImageView icon = (ImageView) button.getGraphic();
                setIconScaling(icon);

                if (button == delBtn) {
                    delBtn.setOnAction(event -> {
                        latexInput.deletePreviousChar();
                        autoEval("");
                    });
                    continue;
                } else if (button == clearBtn) {
                    clearBtn.setOnAction(event -> {
                        latexInput.clear();
                        if (renderService != null) renderService.render(latexInput.getText());
                    });
                    continue;
                }

                button.setOnAction(event -> {
                    String text = ((String) button.getUserData()).replace("e@", "\\");
                    latexInput.insertText(latexInput.getCaretPosition(), text);
                    if (renderService != null) renderService.render(latexInput.getText());
                    autoEval(text);
                });
            } else if (node instanceof SplitMenuButton menuButton) {
                ImageView icon = (ImageView) menuButton.getGraphic();
                setIconScaling(icon);
                menuButton
                    .showingProperty()
                    .addListener((obs, wasShowing, isShowing) -> {
                        if (!isShowing) {
                            Platform.runLater(() -> {
                                if (!anyPopupShowing()) {
                                    latexInput.requestFocus();
                                }
                            });
                        }
                    });

                if (menuButton == approxBtn) {
                    approxBtn.setOnAction(event -> {
                        latexInput.insertText(latexInput.getCaretPosition(), " ≈ ");
                        autoEval("equals");
                    });
                    continue;
                }

                menuButton.setOnAction(event -> {
                    String text = ((String) menuButton.getUserData()).replace("e@", "\\");
                    latexInput.insertText(latexInput.getCaretPosition(), text);
                    if (renderService != null) renderService.render(latexInput.getText());
                    autoEval(text);
                });

                if (
                    menuButton == lParenBtn ||
                    menuButton == rParenBtn ||
                    menuButton == derivBtn ||
                    menuButton == integralBtn
                ) {
                    setIconScaling(icon, 0.8);
                }

                for (MenuItem item : menuButton.getItems()) {
                    icon = (ImageView) menuButton.getGraphic();
                    setIconScaling(icon);
                    item.setOnAction(event -> {
                        String text = ((String) item.getUserData()).replace("e@", "\\");
                        latexInput.insertText(latexInput.getCaretPosition(), text);
                        if (renderService != null) renderService.render(latexInput.getText());
                        autoEval(text);
                    });
                }
            } else if (node instanceof MenuButton menuButton) {
                ImageView icon = (ImageView) menuButton.getGraphic();
                setIconScaling(icon);

                menuButton
                    .showingProperty()
                    .addListener((obs, wasShowing, isShowing) -> {
                        if (!isShowing) {
                            Platform.runLater(() -> {
                                if (!anyPopupShowing()) {
                                    latexInput.requestFocus();
                                }
                            });
                        }
                    });

                menuButton.setOnAction(event -> {
                    String text = ((String) menuButton.getUserData()).replace("e@", "\\");
                    latexInput.insertText(latexInput.getCaretPosition(), text);
                    if (renderService != null) renderService.render(latexInput.getText());
                    autoEval(text);
                });
            }
        }
    }

    private void setIconScaling(ImageView icon) {
        if (icon != null) {
            icon.setPreserveRatio(true);
            icon.fitWidthProperty().bind(delBtn.widthProperty().multiply(0.6));
            icon.fitHeightProperty().bind(delBtn.heightProperty().multiply(0.6));
        }
    }

    private void setIconScaling(ImageView icon, double size) {
        if (icon != null) {
            icon.setPreserveRatio(true);
            icon.fitWidthProperty().bind(delBtn.widthProperty().multiply(size));
            icon.fitHeightProperty().bind(delBtn.heightProperty().multiply(size));
        }
    }

    private void autoEval(KeyEvent event) {
        if (event.getCharacter().hashCode() == 9) {
            latexInput.clear();
            previewEngine.reload();
            return;
        } else if (event.getCharacter().hashCode() == 27) {
            Platform.exit();
        }

        int pos = latexInput.getCaretPosition();
        int equalsIndex;
        if (latexInput.getText().contains(" = ")) equalsIndex = latexInput.getText().lastIndexOf(" = ") + 3;
        else equalsIndex = latexInput.getText().lastIndexOf(" ≈ ") + 3;
        boolean beforeEquals = pos < equalsIndex && equalsIndex != 2;
        if ((event.getCharacter().hashCode() != 8 && event.getCharacter().hashCode() != 127) || beforeEquals) {
            if (beforeEquals) {
                latexInput.deleteText(equalsIndex - 3, latexInput.getText().length());
                latexInput.appendText(" ≈ ");
                evaluateExpr();
            } else if (
                (latexInput.getText().endsWith(" = ") || latexInput.getText().endsWith(" ≈ ")) &&
                event.getCharacter().hashCode() == 32
            ) {
                latexInput.deleteText(equalsIndex - 3, latexInput.getText().length());
                latexInput.appendText(" ≈ ");
                evaluateExpr();
            }
            latexInput.positionCaret(pos);
        }

        if (renderService != null) renderService.render(latexInput.getText());
    }

    private void autoEval(String text) {
        int pos = latexInput.getCaretPosition();
        int equalsIndex;
        if (latexInput.getText().contains(" = ")) equalsIndex = latexInput.getText().lastIndexOf(" = ") + 3;
        else equalsIndex = latexInput.getText().lastIndexOf(" ≈ ") + 3;
        boolean beforeEquals = equalsIndex != 2 && pos < equalsIndex;
        if (beforeEquals) {
            latexInput.deleteText(equalsIndex - 3, latexInput.getText().length());
            latexInput.appendText(" ≈ ");
            evaluateExpr();
        }
        if (
            (latexInput.getText().endsWith(" = ") || latexInput.getText().endsWith(" ≈ ")) && text.equals("equals")
        ) evaluateExpr();
        latexInput.positionCaret(pos);
        if (renderService != null) renderService.render(latexInput.getText());
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

    private void evaluateExpr() {
        String result;
        try {
            result = new LatexComplexEvaluator(latexInput.getText().substring(0, latexInput.getText().length() - 2))
                .eval()
                .toLatexString();
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                if (e.getMessage().contains("fromIndex")) result = "argument of sum or prod has to be in braces";
                else result = e.getMessage().replace(" ", " \\space ");
            } else result = "";
        }
        latexInput.appendText(result);
    }

    private boolean anyPopupShowing() {
        return Window.getWindows()
            .stream()
            .anyMatch(w -> w instanceof PopupWindow pw && pw.isShowing());
    }

    @FXML
    private void onMinimize(ActionEvent event) {
        scene.minimizeStage();
    }

    @FXML
    private void onMaximizeRestore(ActionEvent event) {
        scene.maximizeStage();
        documentSplitPane.setDividerPosition(0, 0.25);
    }

    @FXML
    private void onClose(ActionEvent event) {
        ((Stage) toolBar.getScene().getWindow()).close();
    }

    @SuppressWarnings("exports")
    public ToolBar getToolBar() {
        return toolBar;
    }

    public void setBorderlessScene(@SuppressWarnings("exports") BorderlessScene scene) {
        this.scene = scene;
    }
}
