package com.complexcalc.ui;

import javafx.scene.web.WebEngine;

public class RenderService {

    private WebEngine webEngine;

    public RenderService(@SuppressWarnings("exports") WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void render(String latex) {
        String escaped = latex.replace("\\", "\\\\").replace("\"", "\\\"");
        String js = String.format(
            "document.getElementById('output').innerHTML = '\\\\[%s\\\\]';" +
                "MathJax.typesetPromise([document.getElementById('output')]).then(() => window.javabridge.onRenderComplete());",
            escaped
        );
        webEngine.executeScript(js);
    }

    private void flushQueue() {}
}
