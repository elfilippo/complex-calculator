package com.complexcalc.ui;

public class Bridge {

    private volatile boolean ready = false;

    public void onReady() {
        ready = true;
    }

    public void onRenderComplete() {}

    public void onError() {}
}
