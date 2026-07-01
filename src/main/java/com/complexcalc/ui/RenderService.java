package com.complexcalc.ui;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

public class RenderService {

    private WebEngine webEngine;
    private String escapedExpr;

    public RenderService(@SuppressWarnings("exports") WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void render(String expression) {
        if (webEngine.getLoadWorker().getState() != Worker.State.SUCCEEDED) {
            return;
        }

        escapedExpr = expression
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("'", "\\'")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            .replace("\u2028", "\\u2028")
            .replace("\u2029", "\\u2029")
            .replace("²", "^{2}")
            .replace("³", "^{3}");

        String js = String.format(
            "document.getElementById('output').innerHTML = '\\\\[%s\\\\]';" +
                "MathJax.typesetPromise([document.getElementById('output')]).then(() => window.javabridge.onRenderComplete());",
            escapedExpr
        );

        webEngine.executeScript(js);
    }
}
