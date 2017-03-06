package com.wei.rootkit.model;

import android.graphics.drawable.Drawable;

/**
 * Created by weiyilin on 17/3/6.
 */

public class Icon {
    private String id;          //uid
    private String appName;     //应用名
    private Drawable icon;      //应用图标

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
