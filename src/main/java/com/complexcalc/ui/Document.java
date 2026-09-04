package com.complexcalc.ui;

import java.util.LinkedList;
import java.util.List;

public class Document {

    private List<String> expressions = new LinkedList<>();
    private int currentIndex;
    private RenderService renderEngine;

    public Document(RenderService renderEngine) {
        expressions.add("Document X");
        currentIndex = 1;
        this.renderEngine = renderEngine;
        update();
    }

    public void update() {
        String renderString = "";
        if (currentIndex == 0) renderString += "\\boxed{\\textbf{" + expressions.get(0) + "}}\\]\\[";
        else renderString += "\\textbf{" + expressions.get(0) + "}\\]\\[";
        for (int i = 1; i < expressions.size(); i++) {
            if (i != currentIndex) renderString += expressions.get(i) + "\\]\\[";
            else renderString += "\\boxed{" + expressions.get(i) + "}\\]\\[";
        }
        if (currentIndex >= expressions.size()) renderString += "\\boxed{\\space}";
        renderEngine.render(renderString, false);
    }

    public void add(String expression) {
        if (currentIndex >= expressions.size()) expressions.add(expression);
        else expressions.set(currentIndex, expression);
        update();
    }

    public boolean movedUp() {
        if (currentIndex - 1 >= 0) {
            currentIndex--;
            update();
            return true;
        }
        return false;
    }

    public boolean movedDown() {
        if (currentIndex < expressions.size()) {
            currentIndex++;
            update();
            return true;
        }
        return false;
    }

    public String getCurrent() {
        if (expressions.isEmpty()) return "";
        return currentIndex < expressions.size()
            ? expressions.get(currentIndex)
            : currentIndex < 1
                ? expressions.get(currentIndex)
                : expressions.get(currentIndex - 1);
    }

    public void deleteCurrent() {
        if (currentIndex >= expressions.size()) return;
        if (currentIndex == 0) {
            expressions.set(0, "Document X");
            update();
            return;
        }
        expressions.remove(currentIndex);
        if (currentIndex > 0) currentIndex--;
        update();
    }

    public boolean isOnTitle() {
        return currentIndex == 0;
    }

    public void save() {}
}
