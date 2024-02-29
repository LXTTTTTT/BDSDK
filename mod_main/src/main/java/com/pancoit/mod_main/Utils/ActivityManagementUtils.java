package com.pancoit.mod_main.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// 页面管理器
public class ActivityManagementUtils {

// 单例 --------------------------------------------------
    private static ActivityManagementUtils activityManagementUtil;
    public static ActivityManagementUtils getInstance() {
        if (activityManagementUtil == null) {
            activityManagementUtil = new ActivityManagementUtils();
        }
        return activityManagementUtil;
    }

    private List<Activity> tasks = new LinkedList<>();

    public void push(Activity activity) {
        tasks.add(activity);
    }

    public void pop(Activity activity) {
        tasks.remove(activity);
    }

    // 返回最上面的 Activity
    public Activity top() {
        return tasks.isEmpty() ? null : tasks.get(tasks.size() - 1);
    }



    // 关闭所有 Activity
    public void finishAllActivity(Runnable callback) {
        Iterator<Activity> it = tasks.iterator();
        while (it.hasNext()) {
            Activity item = it.next();
            it.remove();
            item.finish();
        }
        if (callback != null) {
            callback.run();
        }
    }

    // 关闭其他 Activity
    public void finishOtherActivity(Class<? extends Activity> clazz) {
        Iterator<Activity> it = tasks.iterator();
        while (it.hasNext()) {
            Activity item = it.next();
            if (item.getClass() != clazz) {
                it.remove();
                item.finish();
            }
        }
    }

    // 关闭这个 Activity
    public void finishActivity(Class<? extends Activity> clazz) {
        Iterator<Activity> it = tasks.iterator();
        while (it.hasNext()) {
            Activity item = it.next();
            if (item.getClass() == clazz) {
                it.remove();
                item.finish();
                break;
            }
        }
    }

    // Activity 是否存在
    public boolean isActivityExistsInStack(Class<? extends Activity> clazz) {
        if (clazz != null) {
            for (Activity task : tasks) {
                if (task.getClass() == clazz) {
                    return true;
                }
            }
        }
        return false;
    }

    // Activity 是否已销毁
    public boolean isActivityDestroy(Context context) {
        Activity activity = findActivity(context);
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return activity.isDestroyed() || activity.isFinishing();
            } else {
                return activity.isFinishing();
            }
        } else {
            return true;
        }
    }

    /**
     * ContextWrapper是context的包装类，AppcompatActivity，service，application实际上都是ContextWrapper的子类
     * AppcompatXXX类的context都会被包装成TintContextWrapper
     * @param context
     */
    private Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }


}
