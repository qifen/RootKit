package com.wei.rootkit.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.framed.FrameReader;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by sharon on 2017/3/7.
 */

public class DetailService {
    private static DetailService detailService = new DetailService();

    private PackageManager packageManager = null;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private String ip="";

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
        ip=getIP(context);
        uploadAPK(packageName,context);
        uploadFile(packageName);
    }

    /*
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
    */

    private void uploadAPK(String packageName,Context context){
        //补全请求地址
        //String requestUrl = String.format("%s/%s", upload_head, actionUrl);
        String requestUrl=ip;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        builder.addFormDataPart("PackageName",packageName);
        packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(packageName.trim(),0);
            ApplicationInfo appInfo=packInfo.applicationInfo;
            String apkPath=appInfo.sourceDir;
            File apkFile=new File(apkPath);

            if(apkFile.exists()){
                RequestBody requestBody=RequestBody.create(MediaType.parse("apk"),apkFile);
                builder.addFormDataPart("APK",packageName,requestBody);

                //创建RequestBody
                RequestBody body = builder.build();
                //创建Request
                final Request request = new Request.Builder().url(requestUrl).post(body).build();
                final Call call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("uploadAPK", "Callback--"+e.toString());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String string = response.body().string();
                            Log.e("uploadAPK", "response ----->" + string);
                        } else {
                            Log.e("uploadAPK", "Callback--upload failed!");
                        }
                    }

                });
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("uploadAPK","  getPackageInfo");
            e.printStackTrace();
        }

    }

    private void uploadFile(String packageName){
        final String pn=packageName.trim();
        //补全请求地址
        //String requestUrl = String.format("%s/%s", upload_head, actionUrl);
        String requestUrl=ip;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        builder.addFormDataPart("PackageName",packageName);
        String logPath="/data/data/com.wei.rootkit/files/log/"+packageName.trim();
        File logFile=new File(logPath);

        if(logFile.exists()){
            RequestBody requestBody=RequestBody.create(MediaType.parse("text/*"),logFile);
            builder.addFormDataPart("LogFile",packageName,requestBody);

            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url(requestUrl).post(body).build();
            final Call call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);

            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("uploadFile", "Callback--"+e.toString());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.e("uploadFile", "response ----->");
                        //设定每次写入1024bytes
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        int count = 0;
                        InputStream is=response.body().byteStream();
                        String picPath="/data/data/com.wei.rootkit/files/pic/"+pn;
                        FileOutputStream outputStream = new FileOutputStream(picPath);

                        while((count=is.read(buffer))!=-1){
                            outputStream.write(buffer, 0, count);
                        }
                        is.close();
                        outputStream.close();
                    } else {
                        Log.e("uploadFile", "Callback--upload failed!");
                    }
                }

            });
        }
    }

    /*
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
    */

    private String getIP(Context context){
        WifiManager wifiService = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifiService.getConnectionInfo();

        int tmp=wifiinfo.getIpAddress();
        String ip=(tmp & 0xFF) + "." + ((tmp >> 8) & 0xFF) + "." + ((tmp >> 16) & 0xFF)
                + "." + (tmp >> 24 & 0xFF);

        return ip;

    }

}
