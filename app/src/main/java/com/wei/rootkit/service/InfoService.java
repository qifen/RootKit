package com.wei.rootkit.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
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

    /*加载内核模块
    * */
    private void insertModule(){

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream("/data/data/com.wei.rootkit/files/Log");
            String info="insertModule;";
            outStream.write(info.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.v("TAG", "进入insertModule");

        Process p;
        String cmd="su -c " + "\"insmod /data/data/com.wei.rootkit/files/rootkit.ko\"";

        try {
            // 执行命令
            Log.v("TAG","执行命令");

            try {
                outStream = new FileOutputStream("/data/data/com.wei.rootkit/files/Log");
                String info="start;";
                outStream.write(info.getBytes());
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            p = Runtime.getRuntime().exec(cmd);
            Log.v("TAG","执行命令完毕");

            try {
                outStream = new FileOutputStream("/data/data/com.wei.rootkit/files/Log");
                String info="finish;";
                outStream.write(info.getBytes());
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            Log.v("TAG","异常");

            try {
                outStream = new FileOutputStream("/data/data/com.wei.rootkit/files/Log");
                String info="error;";
                outStream.write(info.getBytes());
                outStream.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            e.printStackTrace();
        }
    }

    /*
    取消检测内核模块——删除myLog，卸载内核模块rootkit.ko
     */
    public void cancelDetect(){
        String logPath="/data/data/com.wei.rootkit/files/myLog";
        File f=new File(logPath);

        if(f.exists()){
            f.delete();
        }

        Process p;
        String cmd="su -c " + "\"rmmod rootkit\"";

        try {
            // 执行命令
            p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void analyze(){

        Log.v("TAG", "开始进行日志分析");
    }

}
