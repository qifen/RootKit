package com.wei.rootkit.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    /**
     * 根据包名得到日志信息
     * @param packageName
     * @return
     */
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

    /**
     * 根据包名和上下文,得到行为图
     * @param packageName
     * @param context
     */
    public void generatePicture(String packageName,Context context){
        ip = getIP(context);
        uploadAPK(packageName,context);
        uploadList();
        uploadFile(packageName);
    }


    /**
     * 上传apk
     * @param packageName
     * @param context
     */
//    private void uploadAPK(String packageName,Context context){
//        //补全请求地址
//        //String requestUrl = String.format("%s/%s", upload_head, actionUrl);
//        String requestUrl=ip;
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        //设置类型
//        builder.setType(MultipartBody.FORM);
//        //追加参数
//        builder.addFormDataPart("PackageName",packageName);
//        packageManager = context.getPackageManager();
//        PackageInfo packInfo = null;
//        try {
//            packInfo = packageManager.getPackageInfo(packageName.trim(),0);
//            ApplicationInfo appInfo=packInfo.applicationInfo;
//            String apkPath=appInfo.sourceDir;
//            File apkFile=new File(apkPath);
//
//            if(apkFile.exists()){
//                RequestBody requestBody=RequestBody.create(MediaType.parse("apk"),apkFile);
//                builder.addFormDataPart("APK",packageName,requestBody);
//
//                //创建RequestBody
//                RequestBody body = builder.build();
//                //创建Request
//                final Request request = new Request.Builder().url(requestUrl).post(body).build();
//                final Call call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
//
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.e("uploadAPK", "Callback--"+e.toString());
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            String string = response.body().string();
//                            Log.e("uploadAPK", "response ----->" + string);
//                        } else {
//                            Log.e("uploadAPK", "Callback--upload failed!");
//                        }
//                    }
//
//                });
//            }
//
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e("uploadAPK","  getPackageInfo");
//            e.printStackTrace();
//        }
//
//    }
    private void uploadAPK(String packageName,Context context){
        //请求地址,ip查询后更改
        String requestUrl = "http://" + "手动输入ip" + ":8080/rootKitServer/uploadApk.action";
        packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(packageName.trim(),0);
            ApplicationInfo appInfo=packInfo.applicationInfo;
            String apkPath=appInfo.sourceDir;
            String[] tmp = apkPath.split("/");
            String apkName = tmp[tmp.length - 1];
            File apkFile = new File(apkPath);

            if(apkFile.exists()){
                Map<String, String> params = new HashMap<>();
                params.put("apkName", apkName);
                params.put("apkContentType", "application/octet-stream");
                OkHttpUtils.post()
                        .addFile("apk", apkName, apkFile)
                        .url(requestUrl)
                        .params(params)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e("uploadAPK", "callback----" + e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("uploadAPK", "success");
                            }
                        });
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("uploadAPK", "getPackageInfo");
            e.printStackTrace();
        }
    }


    /**
     * 上传packages.list
     */
//    private void uploadList(){
//        //补全请求地址
//        //String requestUrl = String.format("%s/%s", upload_head, actionUrl);
//        String requestUrl=ip;
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        //设置类型
//        builder.setType(MultipartBody.FORM);
//        //追加参数
//        builder.addFormDataPart("FileName","packages.list");
//
//        String listPath="/data/system/packages.list";
//        File listFile=new File(listPath);
//
//        if(listFile.exists()){
//            RequestBody requestBody=RequestBody.create(MediaType.parse("ListFile"),listFile);
//            builder.addFormDataPart("File","packages.list",requestBody);
//
//            //创建RequestBody
//            RequestBody body = builder.build();
//            //创建Request
//            final Request request = new Request.Builder().url(requestUrl).post(body).build();
//            final Call call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
//
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.e("uploadList", "Callback--"+e.toString());
//                }
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        String string = response.body().string();
//                        Log.e("uploadList", "response ----->" + string);
//                    } else {
//                        Log.e("uploadList", "Callback--upload failed!");
//                    }
//                }
//
//            });
//        }
//
//    }
    private void uploadList(){
        //请求地址,ip查询后更改
        String requestUrl = "http://" + "手动输入ip" + ":8080/rootKitServer/uploadPackage.action";
        String fileName = "packages.list";
        String listPath="/data/system/packages.list";
        File listFile=new File(listPath);

        if(listFile.exists()){
            Map<String, String> params = new HashMap<>();
            params.put("packagesName", fileName);
            params.put("packagesContentType", "application/octet-stream");
            OkHttpUtils.post()
                    .addFile("packages", fileName, listFile)
                    .url(requestUrl)
                    .params(params)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e("uploadList", "callback----" + e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("uploadList", "success");
                        }
                    });
        }
    }

    /**
     * 得到行为图
     * @param packageName
     */
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


    // TODO: 17/3/24 该ip为本机在局域网内的ip,测试暂不可用,需要服务器ip,暂时需要每次手动查询改写
    private String getIP(Context context){
        WifiManager wifiService = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifiService.getConnectionInfo();

        int tmp=wifiinfo.getIpAddress();
        String ip=(tmp & 0xFF) + "." + ((tmp >> 8) & 0xFF) + "." + ((tmp >> 16) & 0xFF)
                + "." + (tmp >> 24 & 0xFF);

        return ip;

    }

}
