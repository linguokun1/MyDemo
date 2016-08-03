package com.sponia.opyfunctioindemo.dao;

import android.content.ContentValues;

import com.sponia.opyfunctioindemo.app.MyApplication;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.dao
 * @description
 * @date 16/7/29
 */
public class StepFactory {

    public static void insertRecord(Step step) {
        getStepDao().insertInTx(step);
    }

    public static void insertRecords(List<Step> records) {
        getStepDao().insertInTx(records);
    }
    /**清空UndoEvent表的数据*/
    public static void clearRecords() {
        getStepDao().deleteAll();
    }

    public static List<Step> loadAll() {
        return getStepDao().loadAll();
    }

    public static List<Step> queryStepRecords(String user_id, String date) {
        QueryBuilder<Step> qb = getStepDao().queryBuilder();
        qb.where(StepDao.Properties.UserId.eq(user_id), StepDao.Properties.Date.eq(date));

        return qb.list();
    }
    public static List<Step> queryWalkStepRecords(String user_id, String date) {
        QueryBuilder<Step> qb = getStepDao().queryBuilder();
        qb.where(StepDao.Properties.UserId.eq(user_id), StepDao.Properties.Date.eq(date), StepDao.Properties.Acceleration.gt(0.0f), StepDao.Properties.Acceleration.le(16.0f), StepDao.Properties.Status.eq("walk"));
        return qb.list();
    }
    public static List<Step> queryRunStepRecords(String user_id, String date) {
        QueryBuilder<Step> qb = getStepDao().queryBuilder();
        qb.where(StepDao.Properties.UserId.eq(user_id), StepDao.Properties.Date.eq(date), StepDao.Properties.Acceleration.gt(16.0f), StepDao.Properties.Acceleration.le(40.0f), StepDao.Properties.Status.eq("run"));
        return qb.list();
    }
    public static List<Step> queryByCarStepRecords(String user_id, String date) {
        QueryBuilder<Step> qb = getStepDao().queryBuilder();
        qb.where(StepDao.Properties.UserId.eq(user_id), StepDao.Properties.Date.eq(date), StepDao.Properties.Acceleration.gt(40.0f), StepDao.Properties.Acceleration.le(80.0f), StepDao.Properties.Status.eq("car"));
        return qb.list();
    }
    public static Step loadStepRecordById(String id) {
        return getStepDao().load(id);
    }

    public static void updateStepRecords(List<Step> steps) {
        if (null != steps && 0 != steps.size()) {
//            getStepDao().updateInTx(UndoEvents);
            getStepDao().insertOrReplaceInTx(steps);
        }
    }

    public static void updateStepRecord(Step step) {
        if (null != step) {
            getStepDao().update(step);
        }
    }

    public static void updateStepRecordByUserIdAndDate(String userId, String date, float stepCount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("STEP_COUNT", stepCount);
        String[] args = {userId, date};
        int affect = MyApplication.getInstance().getSQLiteDatabase().update("STEP", contentValues, "USER_ID=? and DATE=?", args);
    }

    public static void deleteStepRecord(Step step) {
        getStepDao().delete(step);
    }

    public static void deleteStepRecordById(String id) {
        getStepDao().deleteByKey(id);
    }

    public static void deleteStepRecords(ArrayList<String> id_keys) {
        getStepDao().deleteByKeyInTx(id_keys);
    }

    private static StepDao getStepDao() {
        return MyApplication.getInstance().getDaoSession().getStepDao();
    }

}
