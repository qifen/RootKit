package com.wei.rootkit;

import android.os.Handler;
import android.os.Looper;

public class RootKitAppContext extends AppContextBase {

    public static String TAG = RootKitAppContext.class.getSimpleName();
    //获取到主线程
    private static Thread mainThead = null;
    //获取到主线程的id
    private static int mainTheadId;
    //获取到主线程的handler
    private static Handler mainThreadHandler = null;
    //获取到主线程的looper
    private static Looper mainThreadLooper = null;

    //app是否初始化
    private boolean isAppInited = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static RootKitAppContext getApplication() {
        return (RootKitAppContext) AppContextBase.getApplication();
    }

    public static Handler getMainThreadHandler() {
        return mainThreadHandler;
    }
    public static Looper getMainThreadLooper() {
        return mainThreadLooper;
    }
    public static Thread getMainThread() {
        return mainThead;
    }
    public static int getMainThreadId() {
        return mainTheadId;
    }

}
