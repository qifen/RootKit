package com.wei.rootkit.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.fragment.DetailFragment;
import com.wei.rootkit.util.RootKitUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    private String ip="10.0.2.2";

    private DetailFragment detailFragment;

    public static DetailService getInstance() {

        return detailService;
    }

    private DetailService() {
    }


    public void setDetailFragment(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    /**
     * 根据包名得到日志信息
     * @param packageName
     * @return
     */
    public String getLogContent(String packageName){

        String content="";

        //模拟器使用路径
        String inputPath = "/data/data/com.wei.rootkit/files/log/"+packageName.trim();
        //真机使用路径
        //String inputPath="/sdcard/sample.log";

        File inputFile = new File(inputPath);
        if(inputFile.exists()){
            try {
                InputStream instream = new FileInputStream(inputFile);
                if (instream != null){
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line="";
                    //分行读取
                    int count=0;

                    while (( line = buffreader.readLine()) != null) {

                        count++;

                        //进行处理
                        String[] tmp;
                        String result="";
                        tmp=line.split("]");
                        if(tmp[1].contains(")")){
                            result=tmp[1];
                            int loc=result.indexOf(')');
                            result=result.substring(loc+1);

                            loc=result.indexOf(')');
                            result=result.substring(loc+1);

                        }else{
                            result=tmp[1];
                        }

                        content=content+count+".\n"+result + " ;\n";
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
        //ip = getIP(context);
        //uploadAPK(packageName,context);
        uploadList();
        uploadFile(packageName);
    }


    /**
     * 上传apk
     * @param packageName
     * @param context
     */
    private void uploadAPK(String packageName,Context context){
        //请求地址,ip查询后更改，修改ip
        String requestUrl = "http://" + "172.17.156.145" + ":8080/rootKitServer/uploadApk.action";
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
    private void uploadList(){
        Process process = null;
        Runtime runtime=Runtime.getRuntime();
        OutputStream localOutputStream=null;
        DataOutputStream localDataOutputStream=null;
        try {
            process=runtime.exec("su");
            localOutputStream = process.getOutputStream();
            localDataOutputStream = new DataOutputStream(localOutputStream);

            String cpCmd="cat /data/system/packages.list > /data/data/com.wei.rootkit/files/packages.list\n";
            localDataOutputStream.writeBytes(cpCmd);
            localDataOutputStream.flush();

            cpCmd="chmod 777 /data/data/com.wei.rootkit/files/packages.list\n";
            localDataOutputStream.writeBytes(cpCmd);
            localDataOutputStream.flush();

            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();

            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //请求地址,ip查询后更改
        String requestUrl = "http://" + ip + ":8080/test/uploadPackage.action";
        String fileName = "packages.list";
        String listPath="/data/data/com.wei.rootkit/files/packages.list";
        //String listPath="/sdcard/packages.list";
        File listFile=new File(listPath);

        //读取packages.list的内容
        String listContent="";
        if(listFile.exists()){
            try {
                InputStream instream = new FileInputStream(listFile);

                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;

                while (( line = buffreader.readLine()) != null) {
                    listContent = listContent+line+"\n";
                }
                instream.close();
                Log.e("PackagesList:",listContent);
            } catch (FileNotFoundException e) {
                Log.e("DetailService","GetPackagesListError");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("DetailService","buffreader.readLine Error");
                e.printStackTrace();
            }
        }


        Map<String, String> params = new HashMap<>();
        params.put("packagesName", fileName);
        params.put("packagesContentType", "application/octet-stream");
        params.put("packagesContent", listContent);
        OkHttpUtils.post()
//                .addFile("packages", fileName, listFile)
                .params(params)
                .url(requestUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("uploadList", "callback----" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("uploadList", response);
                        Log.e("uploadList", "success");
                    }
                });

    }

    /**
     * 得到行为图
     * @param packageName
     */
    private void uploadFile(String packageName){
        final String pn=packageName.trim();
        //补全请求地址
        String requestUrl="http://" + ip + ":8080/test/uploadLogFile.action";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        builder.addFormDataPart("PackageName",pn);

        //模拟器使用路径
        String logPath="/data/data/com.wei.rootkit/files/log/"+packageName.trim();
        //真机使用路径
        //String logPath="/sdcard/sample.log";
        File logFile=new File(logPath);

        final String returnPath="/data/data/com.wei.rootkit/files/pic/"+pn.trim();
        final String picPath="/sdcard/pic/"+pn.trim();
        final File picFile=new File(returnPath);

        if(logFile.exists() && (!picFile.exists())){
            RequestBody requestBody=RequestBody.create(MediaType.parse("text/*"),logFile);
            builder.addFormDataPart("LogFile",pn,requestBody);

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
                        //修改
                        //String picPath="/sdcard/pic/"+pn;
                        picFile.createNewFile();
                        FileOutputStream outputStream = new FileOutputStream(picFile);

                        while((count=is.read(buffer))!=-1){
                            outputStream.write(buffer, 0, count);
                        }
                        is.close();
                        outputStream.close();

                        //将图片复制到sdcard
                        Process process = null;
                        Runtime runtime=Runtime.getRuntime();
                        OutputStream localOutputStream=null;
                        DataOutputStream localDataOutputStream=null;
                        process=runtime.exec("su");
                        localOutputStream = process.getOutputStream();
                        localDataOutputStream = new DataOutputStream(localOutputStream);

                        String cpCmd="cat /data/data/com.wei.rootkit/files/pic/"+pn.trim()+" > /sdcard/pic/"+pn.trim()+"\n";
                        localDataOutputStream.writeBytes(cpCmd);
                        localDataOutputStream.flush();

                        localDataOutputStream.writeBytes("exit\n");
                        localDataOutputStream.flush();

                        try {
                            process.waitFor();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //显示图片
                        if (null != detailFragment
                                && detailFragment.getView() != null
                                && detailFragment.getArguments() != null
                                && detailFragment.getArguments().getInt("index") == 1){
                            RootKitUtil.runInMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView imageView = (ImageView) detailFragment.getView().findViewById(R.id.pinchImageView);
                                    TextView textView = (TextView) detailFragment.getView().findViewById(R.id.textView);
                                    Bitmap bm = BitmapFactory.decodeFile(picPath);
                                    imageView.setImageBitmap(bm);
                                    imageView.setVisibility(View.VISIBLE);
                                    textView.setVisibility(View.GONE);
                                }
                            });
                        }


                        Log.e("uploadFile", "Success!");
                    } else {
                        Log.e("uploadFile", "Callback--upload failed!");
                    }
                }

            });
        }else if(picFile.exists()){
            //显示图片
            if (null != detailFragment
                    && detailFragment.getView() != null
                    && detailFragment.getArguments() != null
                    && detailFragment.getArguments().getInt("index") == 1){
                RootKitUtil.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = (ImageView) detailFragment.getView().findViewById(R.id.pinchImageView);
                        TextView textView = (TextView) detailFragment.getView().findViewById(R.id.textView);
                        Bitmap bm = BitmapFactory.decodeFile(picPath);
                        imageView.setImageBitmap(bm);
                        imageView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    /**
     * 得到分析结果
     */
    public void getContent(){
        String requestUrl="http://" + ip + ":8080/test/uploadIdentify.action";
        OkHttpUtils
                .get()
                .url(requestUrl)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("getContent", "callback----" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.optInt("status");
                            final String statusstr;
                            final String contentstr;
                            if (status == 1){
                                statusstr = "恶意软件";
                                contentstr = jsonObject.optString("content");
                            }else {
                                statusstr = "非恶意软件";
                                contentstr = "无";
                            }

                            if (null != detailFragment
                                    && detailFragment.getView() != null
                                    && detailFragment.getArguments() != null
                                    && detailFragment.getArguments().getInt("index") == 2){
                                RootKitUtil.runInMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ImageView imageView = (ImageView) detailFragment.getView().findViewById(R.id.pinchImageView);
                                        TextView textView = (TextView) detailFragment.getView().findViewById(R.id.textView);
                                        LinearLayout linearLayout = (LinearLayout) detailFragment.getView().findViewById(R.id.ll_layout);
                                        TextView status = (TextView) detailFragment.getView().findViewById(R.id.status);
                                        TextView content = (TextView) detailFragment.getView().findViewById(R.id.content);

                                        imageView.setVisibility(View.GONE);
                                        textView.setVisibility(View.GONE);
                                        linearLayout.setVisibility(View.VISIBLE);
                                        status.setText(statusstr);
                                        content.setText(contentstr);
                                    }
                                });
                            }
                        }catch (JSONException e){
                            return;
                        }
                    }
                });
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
