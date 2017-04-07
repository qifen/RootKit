package com.wei.rootkit.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sharon on 2017/3/7.
 */

public class ResultService {
    private static ResultService resultService = new ResultService();

    public static ResultService getInstance() {
        return resultService;
    }

    private ResultService() {
    }

    public void generateLog(String packageName,String uid){

        String logPath="/data/data/com.wei.rootkit/files/log/"+packageName.trim();
        //判断在此次检测中是否生成过app日志文件
        File f=new File(logPath);
        if(!f.exists()){
            //从myLog读取相应日志
            String inputPath = "/data/data/com.wei.rootkit/files/myLog";
            //String inputPath = "/sdcard/myLog";
            String content = ""; //文件内容字符串

            File inputFile = new File(inputPath);
            if(inputFile.exists()){
                try {
                    InputStream instream = new FileInputStream(inputFile);
                    if (instream != null){
                        InputStreamReader inputreader = new InputStreamReader(instream);
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line;
                        //分行读取
                        while (( line = buffreader.readLine()) != null) {
                            //判断是否为此应用的日志
                            if(line.contains("sys_call_table") || line.contains(uid.trim())){
                                content=content+line+"\n";
                             }
                        }

                        instream.close();
                    }
                }catch (java.io.FileNotFoundException e){
                    Log.d("ReadLogFile", "The File doesn't not exist.");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("ReadLogFile", "Input error!");
                    e.printStackTrace();
                }
            }
            //将读取内容写入app日志文件

            FileWriter writer;
            try {
                f.createNewFile();
                writer = new FileWriter(f, true);
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
