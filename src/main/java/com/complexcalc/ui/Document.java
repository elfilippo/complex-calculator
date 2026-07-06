package com.complexcalc.ui;

import java.util.LinkedList;
import java.util.List;

public class Document {

    private List<String> expressions = new LinkedList<>();
    private int currentIndex;
    private RenderService renderEngine;

    public Document(RenderService renderEngine) {
        currentIndex = 3;
        this.renderEngine = renderEngine;
    }

    public void update() {
        String renderString = "";
        for (int i = 0; i < expressions.size(); i++) {
            if (i != currentIndex) renderString += expressions.get(i) + "\\]\\[";
            else renderString += "\\boxed{" + expressions.get(i) + "}\\]\\[";
        }
        if (currentIndex >= expressions.size()) renderString += "\\boxed{\\space}";
        renderEngine.render(renderString);
    }

    public void add(String expression) {
        if (currentIndex >= expressions.size()) expressions.add(expression);
        else expressions.set(currentIndex, expression);
        update();
    }

    public void moveUp() {
        if (currentIndex - 1 >= 0) {
            currentIndex--;
            update();
        }
    }

    public void moveDown() {
        if (currentIndex < expressions.size()) {
            currentIndex++;
            update();
        }
    }

    public void delete() {
        if (currentIndex > expressions.size() - 1) return;
        expressions.remove(currentIndex);
        update();
    }
}
