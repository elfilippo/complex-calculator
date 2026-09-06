package com.complexcalc.ui;

import com.complexcalc.parser.LatexComplexEvaluator;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class Controller {

    private RenderService previewRenderer, documentRenderer;
    private WebEngine previewEngine;
    private WebEngine documentEngine;
    private UIManager uiManager;
    private BorderlessScene scene;
    private Document document;

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
    private SplitMenuButton approxBtn, lParenBtn, rParenBtn, derivBtn, integralBtn, leftArrowBtn, rightArrowBtn, docInsertBtn;

    @FXML
    private ToggleGroup themeGroup;

    @FXML
    private RadioMenuItem lapisBlueTheme, charcoalTheme, flashbangTheme, clownTheme, cyberpunkTheme, deepOceanTheme, tokyoNightTheme;

    @FXML
    private ImageView pIcon, sIcon;

    @FXML
    private ToolBar toolBar;

    @FXML
    private ToggleButton invToggle, hypToggle;

    @FXML
    private MenuItem upArrowBtn, downArrowBtn, docDeleteBtn;

    @FXML
    private CustomMenuItem trigMenuItem;

    @FXML
    private MenuButton fileBtn;

    @FXML
    public void initialize() {
        String documentUrl = getClass().getResource("/com/complexcalc/document.html").toExternalForm();
        String previewUrl = getClass().getResource("/com/complexcalc/preview.html").toExternalForm();
        previewEngine = webPreview.getEngine();
        documentEngine = documentWebView.getEngine();

        previewRenderer = new RenderService(previewEngine);
        documentRenderer = new RenderService(documentEngine);

        installMathJaxReadyCheck(previewEngine, previewRenderer);
        installMathJaxReadyCheck(documentEngine, documentRenderer);

        ChangeListener<Worker.State> loadDocumentAfterPreview = new ChangeListener<>() {
            @Override
            public void changed(
                ObservableValue<? extends Worker.State> obs,
                Worker.State oldState,
                Worker.State newState
            ) {
                if (newState == Worker.State.SUCCEEDED) {
                    previewEngine.getLoadWorker().stateProperty().removeListener(this);
                    documentEngine.load(documentUrl);
                }
            }
        };
        previewEngine.getLoadWorker().stateProperty().addListener(loadDocumentAfterPreview);
        previewEngine.load(previewUrl);

        document = new Document(documentRenderer);

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

                if (newToggle == lapisBlueTheme) {
                    uiManager.setTheme(0);
                } else if (newToggle == charcoalTheme) {
                    uiManager.setTheme(1);
                } else if (newToggle == flashbangTheme) {
                    uiManager.setTheme(2);
                } else if (newToggle == clownTheme) {
                    uiManager.setTheme(3);
                } else if (newToggle == cyberpunkTheme) {
                    uiManager.setTheme(4);
                } else if (newToggle == deepOceanTheme) {
                    uiManager.setTheme(5);
                } else if (newToggle == tokyoNightTheme) {
                    uiManager.setTheme(6);
                }
            });

        calcSplitPane.setDividerPosition(0, 0.30);
        calcSplitPane.setDividerPosition(1, 0.40);

        Divider divider = documentSplitPane.getDividers().get(0);
        divider.setPosition(0.2);
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

        for (Node node : toolBar.getItems()) {
            if (node instanceof MenuButton button) {
                button
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
            }
        }

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
                        previewRenderer.render(latexInput.getText(), document.isOnTitle());
                    });
                    continue;
                }

                button.setOnAction(event -> {
                    String text = ((String) button.getUserData()).replace("e@", "\\");
                    latexInput.insertText(latexInput.getCaretPosition(), text);
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
                } else if (menuButton == leftArrowBtn) {
                    menuButton.setOnAction(event -> {
                        if (latexInput.getCaretPosition() == 0) return;
                        latexInput.positionCaret(latexInput.getCaretPosition() - 1);
                    });
                } else if (menuButton == rightArrowBtn) {
                    menuButton.setOnAction(event -> latexInput.positionCaret(latexInput.getCaretPosition() + 1));
                } else if (menuButton == docInsertBtn) {
                    menuButton.setOnAction(event -> document.add(latexInput.getText()));
                } else {
                    menuButton.setOnAction(event -> {
                        String text = ((String) menuButton.getUserData()).replace("e@", "\\");
                        latexInput.insertText(latexInput.getCaretPosition(), text);
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
                }

                for (MenuItem item : menuButton.getItems()) {
                    if (item instanceof CustomMenuItem menuItem) {
                        if (menuItem.getContent() instanceof GridPane grid) {
                            for (Node child : grid.getChildren()) {
                                if (child instanceof Button button) {
                                    icon = (ImageView) button.getGraphic();
                                    setIconScaling(icon);
                                    button.setOnAction(event -> {
                                        String text = ((String) button.getUserData()).replace("e@", "\\");
                                        latexInput.insertText(latexInput.getCaretPosition(), text);
                                        autoEval(text);
                                    });
                                }
                            }
                        }
                    } else {
                        icon = (ImageView) menuButton.getGraphic();
                        setIconScaling(icon);
                        item.setOnAction(event -> {
                            String text = ((String) item.getUserData()).replace("e@", "\\");
                            latexInput.insertText(latexInput.getCaretPosition(), text);
                            autoEval(text);
                        });
                    }
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

                if (menuButton.getItems().get(0) instanceof CustomMenuItem menuItem) {
                    if (menuItem.getContent() instanceof GridPane grid) {
                        for (Node child : grid.getChildren()) {
                            if (child instanceof Button button) {
                                icon = (ImageView) button.getGraphic();
                                setIconScaling(icon);
                                button.setOnAction(event -> {
                                    String text = ((String) button.getUserData()).replace("e@", "\\");
                                    latexInput.insertText(latexInput.getCaretPosition(), text);
                                    autoEval(text);
                                });
                            }
                        }
                    } else if (menuItem.getContent() instanceof VBox panel) {
                        for (Node vboxChild : panel.getChildren()) {
                            if (vboxChild instanceof GridPane grid) {
                                for (Node child : grid.getChildren()) {
                                    if (child instanceof Button button) {
                                        icon = (ImageView) button.getGraphic();
                                        setIconScaling(icon);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        upArrowBtn.setOnAction(event -> {
            if (document.movedUp()) {
                latexInput.setText(document.getCurrent());
                previewRenderer.render(latexInput.getText(), document.isOnTitle());
            }
        });
        downArrowBtn.setOnAction(event -> {
            if (document.movedDown()) {
                latexInput.setText(document.getCurrent());
                previewRenderer.render(latexInput.getText(), document.isOnTitle());
            }
        });
        docDeleteBtn.setOnAction(event -> {
            document.deleteCurrent();
            latexInput.setText(document.getCurrent());
            previewRenderer.render(latexInput.getText(), document.isOnTitle());
        });
    }

    private void installMathJaxReadyCheck(WebEngine engine, RenderService renderer) {
        engine
            .getLoadWorker()
            .stateProperty()
            .addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    pollMathJaxReady(engine, renderer, 0);
                }
            });
    }

    private void pollMathJaxReady(WebEngine engine, RenderService renderer, int attempt) {
        if (Boolean.TRUE.equals(engine.executeScript("window.__mathJaxReady === true"))) {
            renderer.markMathJaxReady();
            return;
        }
        if (attempt >= 50) {
            return;
        }
        PauseTransition pause = new PauseTransition(Duration.millis(20));
        pause.setOnFinished(e -> pollMathJaxReady(engine, renderer, attempt + 1));
        pause.play();
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
        if (event.isControlDown() ^ event.isAltDown()) {
            previewRenderer.render(latexInput.getText(), document.isOnTitle());
            return;
        }

        int pos = latexInput.getCaretPosition();

        if (event.getCharacter().hashCode() == 9) {
            latexInput.clear();
            previewEngine.reload();
            return;
        } else if (event.getCharacter().hashCode() == 27) {
            Platform.exit();
        } else if (event.getCharacter().equals(" ")) {
            String text = latexInput.getText().replace("=  ", "≈ ");
            latexInput.setText(text);
            if (pos > text.length()) pos--;
        }

        int equalsIndex;
        equalsIndex = latexInput.getText().lastIndexOf(" ≈ ") + 3;
        boolean beforeEquals = pos < equalsIndex && equalsIndex != 2;
        if ((event.getCharacter().hashCode() != 8 && event.getCharacter().hashCode() != 127) || beforeEquals) {
            if (beforeEquals) {
                latexInput.deleteText(equalsIndex - 3, latexInput.getText().length());
                latexInput.appendText(" ≈ ");
                evaluateExpr();
            } else if ((latexInput.getText().contains(" ≈ ")) && event.getCharacter().equals(" ")) {
                latexInput.deleteText(equalsIndex - 3, latexInput.getText().length());
                latexInput.appendText(" ≈ ");
                evaluateExpr();
            }
            latexInput.positionCaret(pos);
        }

        previewRenderer.render(latexInput.getText(), document.isOnTitle());
    }

    private void autoEval(String text) {
        int pos = latexInput.getCaretPosition();
        int equalsIndex;
        equalsIndex = latexInput.getText().lastIndexOf(" ≈ ") + 3;
        boolean beforeEquals = equalsIndex != 2 && pos < equalsIndex;
        if (beforeEquals) {
            latexInput.deleteText(equalsIndex - 3, latexInput.getText().length());
            latexInput.appendText(" ≈ ");
            evaluateExpr();
        }
        if (latexInput.getText().endsWith(" ≈ ") && text.equals("equals")) evaluateExpr();
        latexInput.positionCaret(pos);
        previewRenderer.render(latexInput.getText(), document.isOnTitle());
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
            if (
                e.getMessage() != null && (e instanceof IllegalArgumentException || e instanceof IllegalStateException)
            ) {
                if (e.getMessage().contains("fromIndex")) result = "argument of sum or prod has to be in braces";
                else result = e.getMessage().replace(" ", " ");
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

    @FXML
    private void onTrigFunctionClicked(ActionEvent event) {
        Button src = (Button) event.getSource();
        String base = (String) src.getUserData();

        boolean inverse = invToggle.isSelected();
        boolean hyperbolic = hypToggle.isSelected();

        String fnName = hyperbolic ? base + "h" : base;
        if (inverse) {
            fnName = "arc" + fnName;
        }

        String latex = "\\" + fnName + "{}";
        latexInput.insertText(latexInput.getCaretPosition(), latex);

        trigMenuItem.getParentPopup().hide();
    }

    @FXML
    private void onSave() {
        document.save();
    }

    @FXML
    private void onLoad() {
        fileBtn.hide();
        if (!document.loadedFile()) return;
        latexInput.setText(document.getCurrent());
        previewRenderer.render(latexInput.getText(), document.isOnTitle());
    }
}
