package com.complexcalc.ui;

import javafx.scene.web.WebEngine;

public class RenderService {

    private WebEngine webEngine;
    private String escapedExpr;

    public RenderService(@SuppressWarnings("exports") WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void render(String expression) {
        escapedExpr = expression.replace("\\", "\\\\").replace("\"", "\\\"");
        String js = String.format(
            "document.getElementById('output').innerHTML = '\\\\[%s\\\\]';" +
                "MathJax.typesetPromise([document.getElementById('output')]).then(() => window.javabridge.onRenderComplete());",
            escapedExpr
        );
        webEngine.executeScript(js);
    }

    private void flushQueue() {}
}
