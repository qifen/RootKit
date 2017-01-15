package com.wei.rootkit.model;

import java.io.Serializable;

/**
 * Created by weiyilin on 16/12/27.
 */

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String icon;
    private String name;
    private String size;
    private String packageName;

    public Item(){}

    public Item(String name, String size){
        this.name = name;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
