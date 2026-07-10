package com.complexcalc.ui;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

public class RenderService {

    private WebEngine webEngine;
    private volatile boolean pageLoaded;
    private volatile boolean mathJaxReady;
    private String pendingExpr;

    public RenderService(@SuppressWarnings("exports") WebEngine webEngine) {
        this.webEngine = webEngine;

        pageLoaded = webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED;

        webEngine
            .getLoadWorker()
            .stateProperty()
            .addListener((obs, oldState, newState) -> {
                pageLoaded = newState == Worker.State.SUCCEEDED;
                if (!pageLoaded) {
                    mathJaxReady = false;
                }
                maybeFlush();
            });
    }

    public void markMathJaxReady() {
        mathJaxReady = true;
        maybeFlush();
    }

    public void render(String expression) {
        if (!pageLoaded || !mathJaxReady) {
            pendingExpr = expression;
            return;
        }
        doRender(expression);
    }

    private void maybeFlush() {
        if (pageLoaded && mathJaxReady && pendingExpr != null) {
            String expr = pendingExpr;
            pendingExpr = null;
            doRender(expr);
        }
    }

    private void doRender(String expression) {
        String escapedExpr = expression
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
                "MathJax.typesetPromise([document.getElementById('output')])" +
                ".catch(err => console.error('typeset failed:', err));",
            escapedExpr
        );

        webEngine.executeScript(js);
    }
}
