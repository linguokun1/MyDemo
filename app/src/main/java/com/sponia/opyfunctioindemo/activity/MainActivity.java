package com.sponia.opyfunctioindemo.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.sponia.opyfunctioindemo.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btn_star)
    Button mBtnStar;
    @Bind(R.id.btn_step_counter)
    Button mBtnStepCounter;
    @Bind(R.id.btn_face_cute)
    Button mBtnFaceCute;
    @Bind(R.id.btn_surface)
    Button mBtnSurface;
    @Bind(R.id.btn_trace)
    Button mBtnTrace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        mBtnStar = (Button) findViewById(R.id.btn_star);
//        mBtnStepCounter = (Button) findViewById(R.id.btn_step_counter);
        initListener();
        checkSensor();
    }

    private void initListener() {
        mBtnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StarActivity.class));
            }
        });
//        mBtnStepCounter.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StepCounterActivity.class)));
        mBtnStepCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepCounterActivity.class));
            }
        });
        mBtnFaceCute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FaceCuteActivity.class));
            }
        });
        mBtnSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SurfaceActivity.class));
            }
        });
        mBtnTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, YingyanActivity.class));
            }
        });
//        mBtnFaceCute.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FaceCuteActivity.class)));
//        mBtnSurface.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SurfaceActivity.class)));
//        mBtnTrace.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, YingyanActivity.class)));

//        mBtnCocos2d.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, Cocos2dActivity.class));
//            }
//        });


    }


    private SensorManager sm;
    private StringBuffer str;
    private List<Sensor> allSensors;
    private Sensor sensor;

    private void checkSensor() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        allSensors = sm.getSensorList(Sensor.TYPE_ALL);


        str = new StringBuffer();
        str.append("该手机有" + allSensors.size() + "个传感器,分别是:\n");
        for (int i = 0; i < allSensors.size(); i++) {
            sensor = allSensors.get(i);
//            str.append("设备名称:" + sensor.getName() + "\n");
//            str.append("设备版本:" + sensor.getVersion() + "\n");
//            str.append("通用类型号:" + sensor.getType() + "\n");
//            str.append("设备商名称:" + sensor.getVendor() + "\n");
//            str.append("传感器功耗:" + sensor.getPower() + "\n");
//            str.append("传感器分辨率:" + sensor.getResolution() + "\n");
//            str.append("传感器最大量程:" + sensor.getMaximumRange() + "\n");
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    str.append(i + "加速度传感器" + "\n");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    str.append(i + "陀螺仪传感器" + "\n");
                    break;
                case Sensor.TYPE_LIGHT:
                    str.append(i + "环境光线传感器" + "\n");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    str.append(i + "电磁场传感器" + "\n");
                    break;
                case Sensor.TYPE_ORIENTATION:
                    str.append(i + "方向传感器" + "\n");
                    break;
                case Sensor.TYPE_PRESSURE:
                    str.append(i + "压力传感器" + "\n");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    str.append(i + "距离传感器" + "\n");
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    str.append(i + "温度传感器" + "\n");
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    str.append(i + "计步传感器" + "\n");
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    str.append(i + "步行检测传感器" + "\n");
                    break;
                case Sensor.TYPE_GRAVITY:
                    str.append(i + "重力传感器" + "\n");
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    str.append(i + "直线加速度传感器" + "\n");
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    str.append(i + "旋转矢量传感器" + "\n");
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    str.append(i + "游戏旋转矢量传感器" + "\n");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    str.append(i + "未校准的磁性传感器" + "\n");
                    break;
                case 22:
                    str.append(i + "倾斜侦测器" + "\n");
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    str.append(i + "特殊动作触发传感器" + "\n");
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    str.append(i + "未校准的陀螺仪传感器" + "\n");
                    break;
                default:
                    str.append(i + "未知传感器 type:" + sensor.getType() + "名称:" + sensor.getName() + "\n");
                    break;
            }
        }
        System.out.println(str);
    }
}
