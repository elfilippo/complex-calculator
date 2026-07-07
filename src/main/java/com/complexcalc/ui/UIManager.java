package com.complexcalc.ui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
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
        css.add(path("base"));
        switch (n) {
            case 0 -> {
                css.add(path("lapis-blue"));
                colorIcons(true, true, true);
            }
            case 1 -> {
                css.add(path("charcoal"));
                colorIcons(true, true, true);
            }
            case 2 -> {
                css.add(path("flashbang"));
                colorIcons(false, false, true);
            }
            case 3 -> {
                css.add(path("clown"));
                colorIcons(true, true, true);
            }
            case 4 -> {
                css.add(path("cyberpunk"));
                colorIcons(true, true, true);
            }
            case 5 -> {
                css.add(path("deep-ocean"));
                colorIcons(true, true, true);
            }
            case 6 -> {
                css.add(path("tokyo-night"));
                colorIcons(true, true, true);
            }
            default -> {
                css.add(path("lapis-blue"));
                colorIcons(true, true, true);
            }
        }
    }

    private String path(String name) {
        return getClass().getResource("/com/complexcalc/themes/" + name + ".css").toExternalForm();
    }

    private void tintIcons(String selector, Color color) {
        double hue = color.getHue() / 180.0 - 1.0;
        double sat = color.getSaturation() * 2.0 - 1.0;
        double bri = color.getBrightness() * 2.0 - 1.0;

        for (Node node : scene.getRoot().lookupAll(selector)) {
            if (node instanceof ImageView icon) {
                if (icon.getImage() == null) continue;
                ColorAdjust ca = new ColorAdjust();
                ca.setHue(hue);
                ca.setSaturation(sat);
                ca.setBrightness(bri);
                icon.setEffect(ca);
            }
        }
    }

    private void colorIcons(boolean pLight, boolean sLight, boolean tLight) {
        if (pLight != pIconsLight) tintIcons(".pIcon", pLight ? Color.WHITE : Color.rgb(29, 27, 26));
        if (sLight != sIconsLight) tintIcons(".sIcon", sLight ? Color.WHITE : Color.rgb(29, 27, 26));
        if (tLight != tIconsLight) tintIcons(".tIcon", tLight ? Color.WHITE : Color.rgb(29, 27, 26));
        pIconsLight = pLight;
        sIconsLight = sLight;
        tIconsLight = tLight;
    }
}
