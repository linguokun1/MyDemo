package com.sponia.opyfunctioindemo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.sponia.opyfunctioindemo.activity.StepCounterActivity;
import com.sponia.opyfunctioindemo.dao.Step;
import com.sponia.opyfunctioindemo.dao.StepFactory;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo
 * @description
 * @date 16/7/7
 */
public class Pedometer implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mStepCount;
    private Sensor mStepDetector;
    private Sensor mAccelerometer;
    private float mCount;//步行总数
    private float mDetector;//步行探测器
    private Context context;
    private static final int sensorTypeD=Sensor.TYPE_STEP_DETECTOR;
    private static final int sensorTypeC=Sensor.TYPE_STEP_COUNTER;
    private float aX;
    private float aY;
    private float aZ;
    private float aT;
    private Step mWalkSteps;
    private Step mRunSteps;
    private Step mByCarSteps;

    public Pedometer() {

    }

    public Pedometer(Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mStepDetector = mSensorManager.getDefaultSensor(sensorTypeD);
        mStepCount = mSensorManager.getDefaultSensor(sensorTypeC);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        calendar = Calendar.getInstance();
    }

    public void register(){
        register(mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
        register(mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
        register(mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unRegister(){
        mSensorManager.unregisterListener(this);
    }

    private void register(Sensor sensor, int rateUs) {
        mSensorManager.registerListener(this, sensor, rateUs);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private StepCounterActivity.MyReceiver mReceiver;
    private Calendar calendar;
    private String day;
    private Step step;
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType()==sensorTypeC) {
            setStepCount(event.values[0]);
//            System.out.println("计步器被回调 == "+event.values[0]);

            //eventbus发送步数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            day = sdf.format(calendar.getTime());
            float stepCount = 0;
            if(StepFactory.queryStepRecords("linguokun", day).size() != 0) {
                if(aT>0) {
                    step = StepFactory.queryStepRecords("linguokun", day).get(0);
                    stepCount = step.getStepCount();
                    stepCount++;
                    //TODO 这里环境用对象更新数据库
//            StepFactory.updateStepRecordByUserIdAndDate("linguokun", day, stepCount);
                    step.setStepCount(stepCount);
                    step.setAcceleration(aT);

                    StepFactory.updateStepRecord(step);
                }
            }
            if(StepFactory.queryWalkStepRecords("linguokun", day).size() != 0){
                if(aT>0 && aT<=16){
                    mWalkSteps = StepFactory.queryWalkStepRecords("linguokun", day).get(0);
                    stepCount = mWalkSteps.getStepCount();
                    stepCount++;
                    mWalkSteps.setStepCount(stepCount);
                    mWalkSteps.setAcceleration(aT);
                    mWalkSteps.setStatus("walk");
                    StepFactory.updateStepRecord(mWalkSteps);
                }
            }
            if(StepFactory.queryRunStepRecords("linguokun", day).size() != 0){
                if(aT>16 && aT<=40) {
                    mRunSteps = StepFactory.queryRunStepRecords("linguokun", day).get(0);
                    stepCount = mRunSteps.getStepCount();
                    stepCount++;
                    mRunSteps.setStepCount(stepCount);
                    mRunSteps.setAcceleration(aT);
                    mRunSteps.setStatus("run");
                    StepFactory.updateStepRecord(mRunSteps);
                }
            }
            if(StepFactory.queryByCarStepRecords("linguokun", day).size() != 0){
                if(aT>40 && aT<=80){
                    mByCarSteps = StepFactory.queryByCarStepRecords("linguokun", day).get(0);
                    stepCount = mByCarSteps.getStepCount();
                    stepCount++;
                    mByCarSteps.setStepCount(stepCount);
                    mByCarSteps.setAcceleration(aT);
                    mByCarSteps.setStatus("car");
                    StepFactory.updateStepRecord(mByCarSteps);
                }
            }


            EventBus.getDefault().post(new StepEvent("linguokun", stepCount, day, aT,"walk"));

            Intent intent = new Intent();
            intent.setAction("com.sponia.send_broadcast_in_service");
            intent.putExtra("step_count", getmDetector());
            context.sendBroadcast(intent);
        }

        if (event.sensor.getType()==sensorTypeD) {
            if (event.values[0]==1.0) {
                mDetector++;
            }
//            System.out.println("行走侦测器被回调");
        }

        if (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            float[] values = event.values;
            aX = values[0];
            aY = values[1];
            aZ = values[2];
            aT = (float) Math.sqrt((Math.pow(aX,2)+Math.pow(aY,2)+Math.pow(aZ,2)));
//            System.out.println("x = "+ aX);
//            System.out.println("y = "+ aY);
//            System.out.println("z = "+ aZ);
//            System.out.println("acceleration = "+ aT);
            EventBus.getDefault().post(new AccelerationEvent(aT));
        }

        if(mReceiver == null){
            StepCounterActivity activity = new StepCounterActivity();
            mReceiver = activity.new MyReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.sponia.send_broadcast_in_service");
            context.registerReceiver(mReceiver,filter);
        }


    }

    public float getStepCount() {
        return mCount;
    }

    private void setStepCount(float count) {
        this.mCount = count;
    }

    public float getmDetector() {
        return mDetector;
    }
}
