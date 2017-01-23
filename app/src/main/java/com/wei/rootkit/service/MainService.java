package com.wei.rootkit.service;

import android.content.Context;
import android.content.res.AssetManager;

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
        //判断是否为初次启动
        String uid_file_path="/data/data/com.wei.rootkit/files/uid_file";
        File f=new File(uid_file_path);
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //拷贝assets中的文件
            copyAssetFileToFiles(context,"rootkit.ko");
            /*
            String filePath="/data/data/com.wei.rootkit/files/";
            File ko=new File(filePath+"rootkit.ko");
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in=context.getAssets().open("rootkit.ko");// 从assets目录下复制
                out = new FileOutputStream(ko);

                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1)
                {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            */

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
}
