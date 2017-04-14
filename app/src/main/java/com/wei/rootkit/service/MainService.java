package com.wei.rootkit.service;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
 
    public void copyFiles(Context context){

        Log.v("TAG","进入copyfiles");

        //判断是否为初次启动
        String uid_file_path="/data/data/com.wei.rootkit/files/uid_file";
        File f=new File(uid_file_path);
        if(!f.exists()){

            Log.v("TAG","拷贝资源文件");

            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //创建log目录，存放单个app的日志
            File logDir= new File(context.getFilesDir() + "/log");
            logDir.mkdir();

            //创建pic目录，生成图
            File picDir= new File("/sdcard/pic");
            picDir.mkdir();

            picDir= new File("/data/data/com.wei.rootkit/files/pic");
            picDir.mkdir();

            //拷贝assets中的文件
            copyAssetFileToFiles(context,"rootkit.ko");

            //拷贝aidl文件
            File dir = new File(context.getFilesDir() + "/aidl");
            dir.mkdir();

            AssetManager assetManager = context.getAssets();
            try {
                String[] children = assetManager.list("aidl");
                for (String child : children) {
                    child = "aidl/" + child;
                    copyAssetFileToFiles(context, child);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void copyAssetFileToFiles(Context context, String filename){
        try {
            InputStream is = context.getAssets().open(filename);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            File of = new File(context.getFilesDir() + "/" + filename);
            of.createNewFile();
            FileOutputStream os = new FileOutputStream(of);
            os.write(buffer);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(){

    }
}
