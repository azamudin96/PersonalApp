package com.azamudin.personalsecureapp.app;

import android.app.Application;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppContext extends Application
{

    private static Map<String, Object> hashMap = null;
    private static Object object = null;
    public static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Realm database setup
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("Z.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        context = getApplicationContext();
        setHashMap(new HashMap<String, Object>());

    }


    private static final String TAG = "AppContext";

    public static Map<String, Object> getHashMap()
    {
        return hashMap;
    }

    public static void setHashMap(Map<String, Object> map)
    {
        hashMap = map;
    }

    public static Context getAppContext()
    {
        return context;
    }

    public static Object getObject()
    {
        return object;
    }

    public static void setObject(Object object)
    {
        AppContext.object = object;
    }
}
