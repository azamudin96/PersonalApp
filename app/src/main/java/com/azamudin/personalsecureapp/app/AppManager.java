package com.azamudin.personalsecureapp.app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.azamudin.personalsecureapp.base.BaseActivity;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Singleton mode
 * AppManager
 * Application management class, used to manage all
 * BaseActivity
 * Created by Azamudin
 */

public class AppManager
{
    private Stack<BaseActivity> BaseActivityStack = null;
    private static AppManager appManager = null;

    /**
     * 获取AppManager实例
     *
     * @return 返回appManager实例
     */
    public static AppManager getAppManager()
    {
        if (appManager == null)
        {
            appManager = new AppManager();
        }
        return appManager;
    }

    /**
     * 传入需要添加的BaseActivity
     *
     * @param baseActivity
     */
    public void addActivity(BaseActivity baseActivity)
    {
        if (BaseActivityStack == null)
        {
            BaseActivityStack = new Stack<BaseActivity>();
        }
        BaseActivityStack.add(baseActivity);
    }

    /**
     * 移除当前的BaseActivity
     */
    public void finishActivity()
    {
        BaseActivity baseActivity = BaseActivityStack.lastElement();
        BaseActivityStack.remove(baseActivity);
        baseActivity.finish();
    }


    /**
     * 移除指定的BaseActivity'
     *
     * @param baseActivity
     */
    public void finishActivity(BaseActivity baseActivity)
    {
        BaseActivityStack.remove(baseActivity);
        baseActivity.finish();
    }

    /**
     * 通过名字去移除BaseActivity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls)
    {
        for (BaseActivity baseActivity : BaseActivityStack)
        {
            if (baseActivity.getClass().getName().equals(cls.getName()))
            {
                finishActivity(baseActivity);
            }
        }
    }

    public void finishOtherActivity()
    {
        Class<?> cls = currentActivity().getClass();
        for (BaseActivity baseActivity : BaseActivityStack)
        {
            if (!(baseActivity.getClass().getName().equals(cls.getName())))
            {
                finishActivity(baseActivity);
            }
        }
    }

    /**
     * 获取当前BaseActivity
     */
    public BaseActivity currentActivity()
    {
        BaseActivity baseActivity = BaseActivityStack.lastElement();
        return baseActivity;
    }

    /**
     * 关闭所有BaseActivity
     */
    public void finishAllBaseActivity()
    {
        for (BaseActivity baseActivity : BaseActivityStack)
        {
            finishActivity(baseActivity);
        }
    }

    /**
     * 当前BaseActivity跳转到另一个BaseActivity携带（key,value）
     *
     * @param otherBaseActivity
     * @param key
     * @param value
     */
    public void ToOtherActivity(Class<?> otherBaseActivity, String key, int value)
    {
        BaseActivity currentActivity = currentActivity();
        Intent intent = new Intent();
        intent.putExtra(key, value);
        intent.setClass(currentActivity, otherBaseActivity);
        currentActivity.startActivity(intent);
    }

    /**
     * 当前BaseActivity跳转到另一个BaseActivity携带bundle
     *
     * @param otherBaseActivity
     * @param bundle
     */
    public void ToOtherActivity(Class<?> otherBaseActivity, Bundle bundle)
    {
        BaseActivity currentActivity = currentActivity();
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(currentActivity, otherBaseActivity);
        currentActivity.startActivity(intent);
    }

    /**
     * 当前BaseActivity跳转到另一个BaseActivity携带bundle和requestCode
     *
     * @param otherBaseActivity
     * @param bundle
     * @param requestCode
     */
    public void ToOtherActivityForResult(Class<?> otherBaseActivity, Bundle bundle, int requestCode)
    {
        BaseActivity currentActivity = currentActivity();
        Intent intent = new Intent();
        if (bundle != null)
        {
            intent.putExtras(bundle);
        }
        intent.setClass(currentActivity, otherBaseActivity);
        currentActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * 当前BaseActivity跳转另一个BaseActivity不带值
     *
     * @param otherBaseActivity
     */
    public void ToOtherActivity(Class<?> otherBaseActivity)
    {
        BaseActivity currentActivity = currentActivity();
        Intent intent = new Intent();
        intent.setClass(currentActivity, otherBaseActivity);
        currentActivity.startActivity(intent);
    }

    /**
     * 当前BaseActivity跳转另一个BaseActivity，带一个String的List
     *
     * @param otherBaseActivity
     * @param name
     * @param list
     */
    public void ToOtherActivity(Class<?> otherBaseActivity, String name,
                                ArrayList<String> list)
    {
        BaseActivity currentActivity = currentActivity();
        Intent intent = new Intent();
        intent.setClass(currentActivity, otherBaseActivity);
        intent.putStringArrayListExtra(name, list);
        currentActivity.startActivity(intent);
    }

    /**
     * 退出应用程序
     *
     * @param context
     */
    public void AppExit(Context context)
    {
        try
        {
            finishAllBaseActivity();
            System.exit(0);
        } catch (Exception e)
        {
        }
    }

}
