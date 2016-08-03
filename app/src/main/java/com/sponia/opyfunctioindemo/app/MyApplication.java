package com.sponia.opyfunctioindemo.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.sponia.opyfunctioindemo.dao.DaoMaster;
import com.sponia.opyfunctioindemo.dao.DaoSession;
import com.sponia.opyfunctiondemo.eventindex.MyEventBusIndex;

import org.greenrobot.eventbus.EventBus;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.app
 * @description
 * @date 16/7/28
 */
public class MyApplication extends Application {

    private SQLiteDatabase db;
    private DaoSession daoSession;
    private static MyApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        setupDB("opy_demo.db");
    }

    private void setupDB(String dbName) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbName, null);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getSQLiteDatabase(){
        return db;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
