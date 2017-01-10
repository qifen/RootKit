package com.wei.rootkit;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.Context;

import java.util.List;
import java.util.Stack;

public abstract class AppContextBase extends Application {

    private static AppContextBase myApplication;

    private static Stack<Activity> activityStack;

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = this;
    }

    public static AppContextBase getApplication() {
        return myApplication;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isAppBackRun() {

        ActivityManager activityManager = (ActivityManager) myApplication
                .getSystemService(ACTIVITY_SERVICE);
        List tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            if (myApplication
                    .getPackageName()
                    .equals(((RunningTaskInfo) tasksInfo.get(0)).topActivity
                            .getPackageName())) {

                return false;
            }
        }
        return true;
    }

    /**
     * 获得栈顶的activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null
                && myApplication.getPackageName().equals(
                runningTaskInfos.get(0).topActivity.getPackageName())) {
            return runningTaskInfos.get(0).topActivity.getClassName();
        } else {
            return "";
        }
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
            activity = null;
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        try {
            if (activityStack == null) {
                return;
            }
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    System.err.println(activityStack.get(i).getClass().getSimpleName());
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getActivitiesSize() {
        return activityStack != null ? activityStack.size() : 0;
    }

}
