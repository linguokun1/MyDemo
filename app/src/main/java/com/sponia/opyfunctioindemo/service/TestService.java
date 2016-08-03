package com.sponia.opyfunctioindemo.service;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.sponia.opyfunctioindemo.Pedometer;
import com.sponia.opyfunctioindemo.R;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.service
 * @description
 * @date 16/7/27
 */
public class TestService extends Service {
    private Pedometer pedometer;
    private static boolean start = false;

    private static final int NOTIFICATION_ID = 1017;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundCompat();
        registerSensor(Sensor.TYPE_STEP_DETECTOR, Sensor.TYPE_STEP_COUNTER);
        System.out.println("服务启动");
        begin();
    }


    private SensorManager mSensorManager;
    private Sensor mSensorDetector;
    private Sensor mSensorCounter;
    private float count;

//    class SensorListener implements SensorEventListener {
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            count += event.values[0];
//            System.out.println(count);
////            if (mTvIsDetector != null) {
////                mTvIsDetector.setText(count + "");
////            }
////            发广播
//            Intent intent = new Intent();
//            intent.setAction("com.sponia.send_broadcast_in_service");
//            intent.putExtra("step_count", pedometer.getmDetector());
//            sendBroadcast(intent);
//        }
//    }

    private void registerSensor(int sensorType, int sensorType2) {
        mSensorManager =
                (SensorManager) this.getSystemService(Activity.SENSOR_SERVICE);
        // sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        mSensorDetector = mSensorManager.getDefaultSensor(sensorType);
        mSensorCounter = mSensorManager.getDefaultSensor(sensorType2);
        if (mSensorDetector != null) {
            // Success! There's a magnetometer.
//            if (mTvIsDetector != null) {
//                mTvIsDetector.setText("本设备支持计步侦测器");
//            }
        } else {
            // Failure! No magnetometer.
//            if (mTvIsDetector != null) {
//                mTvIsDetector.setText("本设备不支持计步侦测器 ");
//            }
        }

        if (mSensorCounter != null) {
            // Success! There's a magnetometer.
//            if (mTvIsCounter != null) {
//                mTvIsCounter.setText("本设备支持计步传感器");
//            }
        } else {
            // Failure! No magnetometer.
//            if (mTvIsCounter != null) {
//                mTvIsCounter.setText("本设备不支持计步传感器 ");
//            }
        }


    }

    private void begin() {
        pedometer = new Pedometer(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pedometer.register();
//        EventBus.getDefault().register(this);
//        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus().register(this);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出前台服务，同时清掉状态栏通知。
        // 在api 18的版本上，这时候状态栏通知没了，但InnerService还在，且仍旧是前台服务状态，目的达到。
        stopForeground(true);
        pedometer.unRegister();
//        EventBus.getDefault().unregister(this);
    }




    public static boolean trigger(Context context) {
        context = context.getApplicationContext();
        if (!start) {
            context.startService(new Intent(context, TestService.class));
        } else {
            if (Build.VERSION.SDK_INT < 18) {
                context.stopService(new Intent(context, TestService.class));
            } else {
                context.stopService(new Intent(context, InnerService.class));
            }
        }
        start = !start;
        return start;
    }

    private void startForegroundCompat() {
        if (Build.VERSION.SDK_INT < 18) {
            // api 18（4.3）以下，随便玩
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            // api 18的时候，google管严了，得绕着玩
            // 先把自己做成一个前台服务，提供合法的参数
            startForeground(NOTIFICATION_ID, fadeNotification(this));

            // 再起一个服务，也是前台的
            startService(new Intent(this, InnerService.class));
        }
    }

    public static class InnerService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();
            // 先把自己也搞成前台的，提供合法参数
            startForeground(NOTIFICATION_ID, fadeNotification(this));

            // 关键步骤来了：自行推掉，或者把AlipayService退掉。
            // duang！系统sb了，说好的人与人的信任呢？
            stopSelf();
        }

        @Override
        public void onDestroy() {
            stopForeground(true);
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


    private static Notification fadeNotification(Context context) {
        Notification notification = new Notification();
        // 随便给一个icon，反正不会显示，只是假装自己是合法的Notification而已
        notification.icon = R.drawable.abc_ab_share_pack_mtrl_alpha;
        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
        return notification;
    }
}
