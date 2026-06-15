package com.complexcalc.ui;

import javafx.scene.web.WebEngine;

public class RenderService {

    private WebEngine webEngine;
    private String escapedExpr;

    public RenderService(@SuppressWarnings("exports") WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void render(String expression) {
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
            .replace("\u2029", "\\u2029");
        String js = String.format(
            "document.getElementById('output').innerHTML = '\\\\[%s\\\\]';" +
                "MathJax.typesetPromise([document.getElementById('output')]).then(() => window.javabridge.onRenderComplete());",
            escapedExpr
        );
        System.out.println("JS = " + js);
        webEngine.executeScript(js);
    }

    private void flushQueue() {}
}
