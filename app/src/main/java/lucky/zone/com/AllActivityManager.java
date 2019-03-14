
package lucky.zone.com;


import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * activity管理类
 */
public class AllActivityManager {
    public static long pauseSystemTime;

    private LinkedList<Activity> mActs;
    private static AllActivityManager instance = null;
    public static Context appContext = null;

    private AllActivityManager() {
        mActs = new LinkedList<Activity>();
    }

    public static void setPauseSystemTimeTime()
    {
        pauseSystemTime = System.currentTimeMillis();
    }

    public static long getPauseTime()
    {
        return pauseSystemTime;
    }

    public synchronized static AllActivityManager getInstance() {
        if (instance == null) {
            instance = new AllActivityManager();
        }
        return instance;
    }

    public void addActivity(Activity act) {
        synchronized (AllActivityManager.this) {
            mActs.addFirst(act);
        }
    }

    public void removeActivity(Activity act) {
        synchronized (AllActivityManager.this) {
            if (mActs != null && mActs.indexOf(act) >= 0) {
                mActs.remove(act);
            }
        }
    }

    /**
     * 当打开activity使用标志:Intent.FLAG_ACTIVITY_REORDER_TO_FRONT,
     * 需要把CloseActivity对应的activity移动到顶端
     */
    public void reorderActivityToFront(Activity act) {
        synchronized (AllActivityManager.this) {
            if (mActs != null && mActs.indexOf(act) > 0) {
                mActs.remove(act);
                mActs.addFirst(act);
            }
        }
    }

    public Activity getTopActivity() {
        synchronized (AllActivityManager.this) {
            return (mActs == null || mActs.size() <= 0) ? null : mActs.get(0);
        }
    }

    public Activity getSecondActivity() {
        synchronized (AllActivityManager.this) {
            return (mActs == null || mActs.size() <= 1) ? null : mActs.get(1);
        }
    }

    public void close() {
        synchronized (AllActivityManager.this) {
            Activity act;
            while (mActs.size() != 0) {
                act = mActs.poll();
                act.finish();
            }
        }
    }

    /**
     * 关闭其他activity，唯独排除activityClass指定的activity
     *
     * @param activityClass
     */
    public void closeExcept(Class<?> activityClass) {
        synchronized (AllActivityManager.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                if (!act.getClass().getName().equals(activityClass.getName())) {
                    act.finish();
                    activityIterator.remove();
                }
            }
        }
    }

    public void whatActivityInProgram() {
        synchronized (AllActivityManager.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            Logger.d("AllActivityManager", "===============begin=================");
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                Logger.d("AllActivityManager", "close():" + act.getClass().getName());
            }
            Logger.d("AllActivityManager", "=================end===============");
        }
    }

    public boolean isActivityExist(Class<?> activityClass) {
        synchronized (AllActivityManager.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                if (act.getClass().getName().equals(activityClass.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 关闭activityClass指定的activity
     *
     * @param activityClass
     */
    public void closeTarget(Class<?> activityClass) {
        synchronized (AllActivityManager.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                if (act.getClass().getName().equals(activityClass.getName())) {
                    act.finish();
                    activityIterator.remove();
                }
            }
        }
    }


    /**
     * @Title: backToMainActivity
     * @Description: 回到首页界面
     */
    public void backToMainActivity() {
        synchronized (this) {
            Logger.d("AllActivityManager", "===============backToMainActivity=================");
            while (mActs.size() > 0) {
                Activity activity = mActs.getFirst();
                if (activity instanceof MainActivity) {
                    break;
                } else {
                    activity.finish();
                    mActs.remove(activity);
                }
            }
        }
    }

    public int getSize() {
        return mActs.size();
    }

    public ArrayList<Activity> getTargetActivity(Class<?> activityClass) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        synchronized (AllActivityManager.this) {
            Activity act;
            int size = mActs.size();
            for (int i = 0; i < size; i++) {
                act = mActs.get(i);
                if (act.getClass().getName().equals(activityClass.getName())) {
                    activities.add(act);
                }
            }
        }

        return activities;
    }
    public Activity getTargetActivityOne(Class<?> activityClass) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        synchronized (AllActivityManager.this) {
            Activity act;
            int size = mActs.size();
            if (size > 0){
                for (int i = 0; i < size; i++) {
                    act = mActs.get(i);
                    if (act.getClass().getName().equals(activityClass.getName())) {
                        return act;
                    }
                }
            }else{
                return null;
            }

        }
        if(activities.size()==0) {
            return null;
        }
        return activities.get(0);
    }
}
