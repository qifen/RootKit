package com.wei.rootkit.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

        Log.v("TAG", "进入insertModule");

        //String[] command={"su","-c","\"insmod /data/data/com.wei.rootkit/files/rootkit.ko\""};
        Process process = null;

        try {
            // 执行命令
            Log.d("TAG","执行命令");

            Runtime runtime=Runtime.getRuntime();
            //获得root权限
            process=runtime.exec("su");
            //从Process对象获得输出流
            OutputStream localOutputStream = process.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);

            /*
            InputStream localInputStream = process.getInputStream();
            DataInputStream localDataInputStream = new DataInputStream(localInputStream);
            */

            //String cmd="mkdir /data/data/com.wei.rootkit/files/test\n";
            String cmd="";

            cmd="cd /data/data/com.wei.rootkit/files\n";
            localDataOutputStream.writeBytes(cmd);

            cmd="mkdir testDir2\n";
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();

            Log.d("TAG","执行命令testDir2结束，开始执行insmod");

            cmd="insmod rootkit.ko\n";
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();

            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();

            int v=process.waitFor();
            Log.d("TAG","执行命令完毕");

            /*
            byte[] buffer = new byte[localInputStream.available()];
            localInputStream.read(buffer);
            localInputStream.close();
            File of = new File("/data/data/com.wei.rootkit/files/logError");
            of.createNewFile();
            FileOutputStream os = new FileOutputStream(of);
            os.write(buffer);
            os.close();
            */

        } catch (IOException e) {
            Log.v("TAG","IO异常");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.v("TAG","Interrupted异常");
            e.printStackTrace();
        }finally{
            process.destroy();
        }
    }

    /*
    取消检测内核模块——删除myLog，卸载内核模块rootkit.ko
     */
    public void cancelDetect(){

        Process process = null;

        try {
            // 执行命令
            Runtime runtime=Runtime.getRuntime();
            //获得root权限
            process=runtime.exec("su");
            //从Process对象获得输出流
            OutputStream localOutputStream = process.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);

            String cmd="rmmod rootkit\n";
            localDataOutputStream.writeBytes(cmd);

            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();

            Log.v("TAG","执行命令完毕");
            int v=process.waitFor();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String logPath="/data/data/com.wei.rootkit/files/myLog";
        File f=new File(logPath);

        if(f.exists()){
            f.delete();
        }

    }

    public void analyze(){

        Log.v("TAG", "开始进行日志分析");
    }

}
