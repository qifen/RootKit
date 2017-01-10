package com.wei.rootkit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.wei.rootkit.RootKitAppContext;

import java.util.Timer;
import java.util.TimerTask;

public class UIHelper {

    public static ProgressDialog progressDialog;
    //----------------------------[ProcessDialog part begin]---------------------------------

    /**
     * 创建并显示一个转轮风格的允许手工取消的进度对话框
     * @param context
     * @param title
     * @param message
     * @param onCancelListener
     * @return
     */
    public static ProgressDialog showProDialog(Context context, CharSequence title,
                                               CharSequence message,
                                               DialogInterface.OnCancelListener onCancelListener){
        ProgressDialog dialog = createSpinnerProcessDialog(context, title, message, true, onCancelListener);
        try{
            if(dialog!=null){
                closeProgress(progressDialog);
                dialog.show();
                progressDialog = dialog;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return dialog;
    }

    /**
     * 创建并显示一个转轮风格的不能手工取消的进度对话框
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog showProDialogWithoutCancelable(Context context,
                                                                CharSequence title,
                                                                CharSequence message){
        ProgressDialog dialog = createSpinnerProcessDialog(context, title, message, false, null);
        try{
            if(dialog!=null){
                closeProgress(progressDialog);
                dialog.show();
                progressDialog = dialog;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return dialog;
    }


    /**
     * 创建一个圆圈转动风格的进度对话框
     * @param context
     * @param title 对话框标题
     * @param message 对话框提示
     * @param cancelable 是否允许取消
     * @param onCancelListener 取消的监听器
     * @return
     */
    public static ProgressDialog createSpinnerProcessDialog(Context context,
                                                            CharSequence title,
                                                            CharSequence message,
                                                            boolean cancelable,
                                                            DialogInterface.OnCancelListener onCancelListener
    ){
        try{
            if(context instanceof Activity){
                if(((Activity)context).isFinishing())
                {
                    return null;
                }
            }
            ProgressDialog dialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            dialog.setTitle(title);
            dialog.setMessage(message);

            dialog.setCancelable(cancelable);
            if(cancelable){
                dialog.setOnCancelListener(onCancelListener);
            }
            return dialog;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void closeProgress(ProgressDialog pDialog){
        if( pDialog != null ){
            try{
                pDialog.dismiss();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                pDialog = null;
            }
        }

        if(progressDialog!=null){
            try{
                progressDialog.dismiss();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                progressDialog = null;
            }
        }
    }

    public static void closeProgress(){
        UIHelper.runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog = null;
                    }
                }
            }
        });
    }

    //----------------------------[ProcessDialog part end]---------------------------------





    public static Context getContext() {
        return RootKitAppContext.getApplication();
    }

    public static Thread getMainThread() {
        return RootKitAppContext.getMainThread();
    }

    public static long getMainThreadId() {
        return RootKitAppContext.getMainThreadId();
    }

    /** 获取主线程的handler */
    public static Handler getHandler() {
        return RootKitAppContext.getMainThreadHandler();
    }

    /** 延时在主线程执行runnable */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /** 在主线程执行runnable */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /** 从主线程looper里面移除runnable */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    //判断当前的线程是不是在主线程
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    public static View inflate(int resId){
        return LayoutInflater.from(getContext()).inflate(resId,null);
    }

    /** 获取资源 */
    public static Resources getResources() {

        return getContext().getResources();
    }

    /** 获取文字 */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /** 获取文字数组 */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /** 获取dimen */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /** 获取drawable */
    public static Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }

    /** 获取颜色 */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /** 获取颜色选择器 */
    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

    public static int dip2Pixel(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + (dip >= 0 ? 0.5f : -0.5f));
    }

    /** dip转换px */
    public static int dip2Pixel(int dip, Resources res) {
        float scale = res.getDisplayMetrics().density;
        return (int) (dip * scale + (dip >= 0 ? 0.5f : -0.5f));
    }

    public static int pixel2dip(int px) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + (px >= 0 ? 0.5f : -0.5f));
    }

    /** pxz转换dip */
    public static int pixel2dip(int px, Resources res) {
        float scale = res.getDisplayMetrics().density;
        return (int) (px / scale + (px >= 0 ? 0.5f : -0.5f));
    }

    public static void hideKeyboard(Context context, EditText view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(final Context context, final EditText view) {
        Timer timer = new Timer(); // 设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // 弹出软键盘的代码
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300); // 设置300毫秒的时长
    }

    public static void removeSelfFromParent(View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(view);
            }
        }
    }

}
