package com.wei.rootkit.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by sharon on 2017/3/7.
 */

public class DetailService {
    private static DetailService detailService = new DetailService();

    private PackageManager packageManager = null;

    public static DetailService getInstance() {
        return detailService;
    }

    private DetailService() {
    }

    public String getLogContent(String packageName){

        String content="";
        //String inputPath = "/data/data/com.wei.rootkit/files/log/"+packageName;
        String inputPath="/sdcard/"+packageName+".log";

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
                        content=content+line + "\n";
                    }
                    instream.close();
                }
            }catch (java.io.FileNotFoundException e){
                Log.d("ReadAppLogFile", "The File doesn't not exist.");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("ReadAppLogFile", "Input error!");
                e.printStackTrace();
            }
        }
        return content;
    }

    public void generatePicture(String packageName,Context context){
        uploadAPK(packageName,context);
        uploadFile(packageName);
    }

    private void uploadAPK(String packageName,Context context){
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";

        String uploadUrl="";//上传到的服务器地址
        URL url = null;
        try {
            packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(packageName.trim(),0);
            ApplicationInfo appInfo=packInfo.applicationInfo;
            String apkPath=appInfo.sourceDir;
            File f=new File(apkPath);
            //判断安装包是否存在
            if(f.exists()){
                url = new URL(uploadUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
                // 允许输入输出流
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                // 使用POST方法
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + packageName + "-apk" + "\"" + end);//上传apk的文件名
                dos.writeBytes(end);

                //取得文件的FileInputStream
                FileInputStream fis = new FileInputStream(apkPath);
                //设定每次写入1024bytes
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int count = 0;
                // 读取文件
                while ((count = fis.read(buffer)) != -1) {
                    //将数据写入DataOutputStream中
                    dos.write(buffer, 0, count);
                }
                fis.close();
                dos.writeBytes(end);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                dos.flush();

                //取得Response内容
                InputStream is = httpURLConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String result = br.readLine();

                Log.d("UploadFileResult",result);

                is.close();
                dos.close();
                httpURLConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            Log.d("UploadAPK","new URL error!");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.d("UploadAPK","ProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("UploadAPK","url.openConnection error!");
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("UploadAPK","PackageInfo not found");
            e.printStackTrace();
        }
    }

    private void uploadFile(String packageName){
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";

        String uploadUrl="";//上传到的服务器地址
        URL url = null;
        try {
            url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                    + packageName + "\"" + end);
            dos.writeBytes(end);

            String uploadFile="/data/data/com.wei.rootkit/files/log/"+packageName;
            //取得文件的FileInputStream
            FileInputStream fis = new FileInputStream(uploadFile);
            //设定每次写入1024bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1) {
                //将数据写入DataOutputStream中
                dos.write(buffer, 0, count);
            }
            fis.close();
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            //取得Response内容？？？？？
            InputStream is = httpURLConnection.getInputStream();
            String picPath="/data/data/com.wei.rootkit/files/pic/"+packageName;
            FileOutputStream outputStream = new FileOutputStream(picPath);
            while((count=is.read(buffer))!=-1){
                outputStream.write(buffer, 0, count);
            }
            is.close();
            outputStream.close();

//            InputStreamReader isr = new InputStreamReader(is, "utf-8");
//            BufferedReader br = new BufferedReader(isr);
//            String result = br.readLine();

            Log.d("UploadFileResult","Success!");

            dos.close();
            httpURLConnection.disconnect();

        } catch (MalformedURLException e) {
            Log.d("UploadFile","new URL error!");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.d("UploadFile","ProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("UploadFile","url.openConnection error!");
            e.printStackTrace();
        }
    }

}
