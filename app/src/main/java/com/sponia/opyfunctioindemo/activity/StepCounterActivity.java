package com.sponia.opyfunctioindemo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import com.sponia.opyfunctioindemo.AccelerationEvent;
import com.sponia.opyfunctioindemo.Pedometer;
import com.sponia.opyfunctioindemo.R;
import com.sponia.opyfunctioindemo.StepEvent;
import com.sponia.opyfunctioindemo.dao.Step;
import com.sponia.opyfunctioindemo.dao.StepFactory;
import com.sponia.opyfunctioindemo.service.TestService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StepCounterActivity extends AppCompatActivity {

    @Bind(R.id.tv_isCounter)
    TextView mTvIsCounter;
    @Bind(R.id.tv_isDetector)
    TextView mTvIsDetector;

    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.textView1)
    TextView mTextView1;
    @Bind(R.id.textView2)
    TextView mTextView2;
    @Bind(R.id.TextViewDetected)
    TextView tvD;
    @Bind(R.id.tv_last2day)
    TextView mTvLast2day;
    @Bind(R.id.tv_lastday)
    TextView mTvLastday;
    @Bind(R.id.tv_today)
    TextView mTvToday;

    @Bind(R.id.tv_sport_status)
    TextView mTvSportStatus;

    @Bind(R.id.tv_walk_value)
    TextView mTvWalkValue;
    @Bind(R.id.tv_run_value)
    TextView mTvRunValue;
    @Bind(R.id.tv_car_value)
    TextView mTvCarValue;

    private SensorListener listener;
    private Calendar calendar;
    private List<Step> mWalkSteps;
    private List<Step> mRunSteps;
    private List<Step> mCarSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);
        ButterKnife.bind(this);
//        EventBus.getDefault().register(this);

        EventBus.getDefault().register(this);
        calendar = Calendar.getInstance();
        registerSensor(Sensor.TYPE_STEP_DETECTOR, Sensor.TYPE_STEP_COUNTER);
//        begin();
        addStepRecord2Db();
        setViewsText();
        //开启服务进行计步
//        Intent service = new Intent(StepCounterActivity.this, TestService.class);
//        this.startService(service);
        TestService.trigger(this);
    }

    private SensorManager mSensorManager;
    private Sensor mSensorDetector;
    private Sensor mSensorCounter;
    private float count;

    private void registerSensor(int sensorType, int sensorType2) {
        mSensorManager =
                (SensorManager) this.getSystemService(Activity.SENSOR_SERVICE);
        // sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        mSensorDetector = mSensorManager.getDefaultSensor(sensorType);
        mSensorCounter = mSensorManager.getDefaultSensor(sensorType2);
        if (mSensorDetector != null) {
            // Success! There's a magnetometer.
            if (mTvIsDetector != null) {
                mTvIsDetector.setText("本设备支持计步侦测器");
            }
        } else {
            // Failure! No magnetometer.
            if (mTvIsDetector != null) {
                mTvIsDetector.setText("本设备不支持计步侦测器 ");
            }
        }

        if (mSensorCounter != null) {
            // Success! There's a magnetometer.
            if (mTvIsCounter != null) {
                mTvIsCounter.setText("本设备支持计步传感器");
            }
        } else {
            // Failure! No magnetometer.
            if (mTvIsCounter != null) {
                mTvIsCounter.setText("本设备不支持计步传感器 ");
            }
        }


//        mSensorManager.registerListener(listener, mSensorDetector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    class SensorListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            count += event.values[0];
            if (mTvIsDetector != null) {
                mTvIsDetector.setText(count + "");
            }
        }

    }


    private Pedometer pedometer;
    private ViewPropertyAnimator mStepEventAnim;

    private void begin() {
        pedometer = new Pedometer(this);
        tv = (TextView) findViewById(R.id.tv);
        tvD = (TextView) findViewById(R.id.TextViewDetected);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                tv.post(new Runnable() {

                    @Override
                    public void run() {
                        tv.setText("" + pedometer.getStepCount());
                        tv.postInvalidate();
                    }
                });

                tvD.post(new Runnable() {

                    @Override
                    public void run() {

                        if (mStepEventAnim != null) {
                            mStepEventAnim.cancel();
                        }
                        tvD.setText("" + pedometer.getmDetector());
                        tvD.postInvalidate();
                        tvD.setAlpha(1f);
                        mStepEventAnim = tvD.animate().setDuration(500).alpha(0f);
                    }
                });

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 100, 1000);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        pedometer.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        pedometer.unRegister();
    }

    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接收到MyReceiver广播");
            if (intent.getAction().equals("com.sponia.send_broadcast_in_service")) {
                if (mTvIsDetector != null) {
                    mTvIsDetector.setText(intent.getExtras().getFloat("step_count") + "");
                }
                //TODO 以日期为主键 更新数据库该日期对应的当天步数

            }
        }
    }

    /**
     * 接收计步器发送过来的步数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStepEventBus(StepEvent stepEvent) {
        //TODO 以日期为主键 更新数据库该日期对应的当天步数
//        System.out.println("接收到事件");

//        StepFactory.updateStepRecordByUserIdAndDate(stepEvent.user_id, stepEvent.date, stepEvent.stepCount);
//        if (mTvIsDetector != null) {
//            System.out.println("mTvIsDetector != null");
//            System.out.println(stepEvent.getStepCount() + "");
//            mTvIsDetector.setText(stepEvent.getStepCount() + "");
//        }

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        day = sdf.format(calendar.getTime());
//        System.out.println("day onStepEventBus = " + day);

        step = StepFactory.queryStepRecords(stepEvent.getUser_id(), stepEvent.getDate()).get(0);
        if (step != null) {
            //TODO 这里要setText
            System.out.println(step.getStepCount() + "");
            mTvToday.setText(step.getStepCount() + "");
        }
        mWalkSteps = StepFactory.queryWalkStepRecords(stepEvent.getUser_id(), stepEvent.getDate());

        if(mWalkSteps != null && mTvWalkValue != null){
            mTvWalkValue.setText(mWalkSteps.get(0).getStepCount()+"");
        }
        mRunSteps = StepFactory.queryRunStepRecords(stepEvent.getUser_id(), stepEvent.getDate());
        if(mRunSteps != null && mTvRunValue != null){
            mTvRunValue.setText(mRunSteps.get(0).getStepCount()+"");
        }
        mCarSteps = StepFactory.queryByCarStepRecords(stepEvent.getUser_id(), stepEvent.getDate());
        if(mCarSteps != null && mTvCarValue != null){
            mTvCarValue.setText(mCarSteps.get(0).getStepCount()+"");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccelerationEventBus(AccelerationEvent accelerationEvent) {
        int acceleration = Math.round(accelerationEvent.acceleration);
        if (acceleration <= 3) {
            mTvSportStatus.setText("站立 加速度 = " + acceleration);
        } else if (acceleration > 3 && acceleration <= 16) {
            mTvSportStatus.setText("步行 加速度 = " + acceleration);
        } else if (acceleration > 16 && acceleration <= 40) {
            mTvSportStatus.setText("跑步 加速度 = " + acceleration);
        } else if (acceleration > 40 && acceleration <= 80) {
            mTvSportStatus.setText("坐车 加速度 = " + acceleration);
        }
    }


    private String day;
    private Step step;

    private void setViewsText() {
        int i = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        day = sdf.format(calendar.getTime());
        System.out.println("day = " + day);
        // Toast.makeText(getActivity(), day + "", Toast.LENGTH_LONG).show();
//        List<Step> steps = StepFactory.queryStepRecords("userId", day);
        if (StepFactory.queryStepRecords("linguokun", day).size() != 0) {
            step = StepFactory.queryStepRecords("linguokun", day).get(0);
            if (step != null) {
                //TODO 这里要setText
                mTvToday.setText(step.getStepCount() + "");
            } else {
            }
        }
        mWalkSteps = StepFactory.queryWalkStepRecords("linguokun", day);

        if(mWalkSteps != null && mTvWalkValue != null){
            mTvWalkValue.setText(mWalkSteps.get(0).getStepCount()+"");
        }
        mRunSteps = StepFactory.queryRunStepRecords("linguokun", day);
        if(mRunSteps != null && mTvRunValue != null){
            mTvRunValue.setText(mRunSteps.get(0).getStepCount()+"");
        }
        mCarSteps = StepFactory.queryByCarStepRecords("linguokun", day);
        if(mCarSteps != null && mTvCarValue != null){
            mTvCarValue.setText(mCarSteps.get(0).getStepCount()+"");
        }



        calendar.add(Calendar.DAY_OF_MONTH, -1);
        day = sdf.format(calendar.getTime());
        System.out.println("day = " + day);
//        List<Step> steps = StepFactory.queryStepRecords("userId", day);
        if (StepFactory.queryStepRecords("linguokun", day).size() != 0) {
            step = StepFactory.queryStepRecords("linguokun", day).get(0);
            if (step != null) {
                mTvLastday.setText(step.getStepCount() + "");
            } else {
            }
        }

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        day = sdf.format(calendar.getTime());
        System.out.println("day = " + day);
//        List<Step> steps = StepFactory.queryStepRecords("userId", day);
        if (StepFactory.queryStepRecords("linguokun", day).size() != 0) {
            step = StepFactory.queryStepRecords("linguokun", day).get(0);
            if (step != null) {
                mTvLast2day.setText(step.getStepCount() + "");
            }

        }


    }

    private void addStepRecord2Db() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        day = sdf.format(calendar.getTime());
        if (StepFactory.queryStepRecords("linguokun", day).size() == 0) {
            Step step = new Step();
            step.setUserId("linguokun");
            step.setId(UUID.randomUUID().toString());
            step.setStepCount(0.0f);
            step.setDate(day);

            StepFactory.insertRecord(step);
        }
        if (StepFactory.queryWalkStepRecords("linguokun", day).size() == 0) {
            Step step = new Step();
            step.setUserId("linguokun");
            step.setId(UUID.randomUUID().toString());
            step.setStepCount(0.0f);
            step.setDate(day);
            step.setStatus("walk");
            step.setAcceleration(4.0f);
            StepFactory.insertRecord(step);
        }
        if (StepFactory.queryRunStepRecords("linguokun", day).size() == 0) {
            Step step = new Step();
            step.setUserId("linguokun");
            step.setId(UUID.randomUUID().toString());
            step.setStepCount(0.0f);
            step.setDate(day);
            step.setStatus("run");
            step.setAcceleration(20.0f);
            StepFactory.insertRecord(step);
        }
        if (StepFactory.queryByCarStepRecords("linguokun", day).size() == 0) {
            Step step = new Step();
            step.setUserId("linguokun");
            step.setId(UUID.randomUUID().toString());
            step.setStepCount(0.0f);
            step.setDate(day);
            step.setStatus("car");
            step.setAcceleration(50.0f);
            StepFactory.insertRecord(step);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
