package com.wei.rootkit.service;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
        deleteOldFiles();
        insertModule();
    }

    /*
    删除已存在的日志以及图片文件
     */
    private void deleteOldFiles(){
        //删除已有的myLog文件
        String logPath="/data/data/com.wei.rootkit/files/myLog";
        File f=new File(logPath);

        if(f.exists()){
            f.delete();
        }

        logPath="sdcard/myLog";
        f=new File(logPath);
        if(f.exists()){
            f.delete();
        }

        //删除log目录下所有文件
        String logDir="/data/data/com.wei.rootkit/files/log";
        File logFile=new File(logDir);
        File files[] = logFile.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        //删除生成图
        String picDir="/sdcard/out.png";
        File picFile=new File(picDir);
        if(picFile.exists()){
            picFile.delete();
        }
    }

    /*加载内核模块
    * */
    private void insertModule(){

        Log.e("TAG", "进入insertModule");

        //String[] command={"su","-c","\"insmod /data/data/com.wei.rootkit/files/rootkit.ko\""};
        Process process = null;

        try {
            // 执行命令
            Log.e("TAG","执行命令");

            Runtime runtime=Runtime.getRuntime();
            //获得root权限
            process=runtime.exec("su");
            //从Process对象获得输出流
            OutputStream localOutputStream = process.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);

            String cmd="";

            cmd="cd /data/data/com.wei.rootkit/files\n";
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();

            Log.e("TAG","开始执行insmod");

            cmd="insmod rootkit.ko\n";
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();

            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();

            Log.e("TAG","执行命令完毕");

            process.waitFor();


        } catch (IOException e) {
            Log.e("insmod","IO异常");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            if (null != process)
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

        /*
        String logPath="/data/data/com.wei.rootkit/files/myLog";
        File f=new File(logPath);

        if(f.exists()){
            f.delete();
        }
        */

    }


    /*
    结束监测——卸载内核模块rootkit.ko
    */
    public void finishDetect(){

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
            localDataOutputStream.flush();

            cmd="chmod 777 /data/data/com.wei.rootkit/files/myLog\n";
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();

            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();

            Log.v("TAG","执行命令完毕");
            int v=process.waitFor();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
