package com.wei.rootkit.service;

/**
 * Created by sharon on 2017/1/19.
 */
public class MainService {
    private static MainService ourInstance = new MainService();

    public static MainService getInstance() {
        return ourInstance;
    }

    private MainService() {
    }

    public void copyFiles(){

    }
}
