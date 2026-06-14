package com.complexcalc.ui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class UIManager {

    private Scene scene;
    private boolean pIconsLight, sIconsLight, tIconsLight;

    public UIManager(@SuppressWarnings("exports") Scene scene) {
        this.scene = scene;
        pIconsLight = true;
        sIconsLight = true;
        tIconsLight = true;
    }

    void setTheme(int n) {
        ObservableList<String> css = scene.getStylesheets();
        css.clear();
        css.add(path("default"));
        switch (n) {
            case 0 -> {
                css.add(path("blue"));
                colorIcons(true, true, true);
            }
            case 1 -> {
                css.add(path("dark"));
                colorIcons(true, true, true);
            }
            case 2 -> {
                css.add(path("light"));
                colorIcons(false, false, false);
            }
            case 3 -> {
                css.add(path("clown"));
                colorIcons(true, true, true);
            }
            case 4 -> {
                css.add(path("rose-pine"));
                colorIcons(true, true, true);
            }
            case 5 -> {
                css.add(path("solarized"));
                colorIcons(true, true, true);
            }
            case 6 -> {
                css.add(path("tokyo-night"));
                colorIcons(true, true, true);
            }
            default -> css.add(path("blue"));
        }
    }

    private String path(String name) {
        return getClass().getResource("/com/complexcalc/themes/" + name + ".css").toExternalForm();
    }

    private void tintIcons(String selector, Color color) {
        for (Node node : scene.getRoot().lookupAll(selector)) {
            if (node instanceof ImageView icon) {
                if (icon.getImage() == null) continue;
                var colorInput = new ColorInput(0, 0, icon.getImage().getWidth(), icon.getImage().getHeight(), color);

                Blend blend = new Blend(BlendMode.SRC_ATOP, null, colorInput);
                icon.setEffect(blend);
            }
        }
    }

    private void colorIcons(boolean pLight, boolean sLight, boolean tLight) {
        if (pLight != pIconsLight) tintIcons(".pIcon", pLight ? Color.WHITE : Color.BLACK);
        if (sLight != sIconsLight) tintIcons(".sIcon", sLight ? Color.WHITE : Color.BLACK);
        if (tLight != tIconsLight) tintIcons(".tIcon", tLight ? Color.WHITE : Color.BLACK);
        pIconsLight = pLight;
        sIconsLight = sLight;
        tIconsLight = tLight;
    }
}
