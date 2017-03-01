package com.wei.rootkit.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by weiyilin on 16/12/27.
 */

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;          //uid
    private String appName;     //应用名
    private String packageName; //包名
    private Drawable icon;      //应用图标
    private String versionName; //版本名称
    private String versionId;   //版本号
    private String firstInstallTime;    //第一次安装日期
    private String lastUpdateTime;      //上一次更新日期

    public Item(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(String firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
