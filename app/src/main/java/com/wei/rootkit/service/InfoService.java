package com.wei.rootkit.service;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sharon on 2017/1/15.
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

        Log.v("TAG","进入insertModule");

        Process p;
        String cmd="su -c " + "\"insmod rootkit.ko\"";

        try {
            // 执行命令
            Log.v("TAG","执行命令");
            p = Runtime.getRuntime().exec(cmd);
            Log.v("TAG","执行命令完毕");
        } catch (IOException e) {
            Log.v("TAG","异常");
            e.printStackTrace();
        }

    }

}
