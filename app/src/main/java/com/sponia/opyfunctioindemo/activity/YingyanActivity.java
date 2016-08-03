package com.sponia.opyfunctioindemo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.sponia.opyfunctioindemo.R;
import com.sponia.opyfunctioindemo.fragment.TrackUploadFragment;

import java.util.Map;

@SuppressLint("NewApi")
public class YingyanActivity extends FragmentActivity implements View.OnClickListener {
    /**
     * 轨迹服务
     */
    public static Trace trace = null;

    /**
     * entity标识
     */
    public static String entityName = null;

    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    public static long serviceId = 120518; // serviceId为开发者创建的鹰眼服务ID

    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private int traceType = 2;

    /**
     * 轨迹服务客户端
     */
    public static LBSTraceClient client = null;

    /**
     * Entity监听器
     */
    public static OnEntityListener entityListener = null;
    /**
     * Track监听器
     */
    protected static OnTrackListener trackListener = null;
    private Button btnTrackUpload;
    private Button btnTrackQuery;

    public static MapView bmapView = null;
    public static BaiduMap mBaiduMap = null;

    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;
    private TrackUploadFragment mTrackUploadFragment;
    private TrackQueryFragment mTrackQueryFragment;

    public static Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_yingyan);

        mContext = getApplicationContext();

        // 初始化轨迹服务客户端
        client = new LBSTraceClient(mContext);

        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);

        // 初始化entity标识
        entityName = "myTrace";

        // 初始化轨迹服务
        trace = new Trace(getApplicationContext(), serviceId, entityName,
                traceType);
        // 初始化组件
        initComponent();
        // 初始化OnEntityListener
        initOnEntityListener();
        // 初始化OnTrackListener
        initOnTrackListener();

        client.setOnTrackListener(trackListener);
        // 添加entity
//        addEntity();

        // 设置默认的Fragment
        setDefaultFragment();
    }

    private void initComponent() {
        //轨迹追踪
        btnTrackUpload = (Button) findViewById(R.id.btn_trackUpload);
        btnTrackUpload.setOnClickListener(this);
        //历史轨迹
        btnTrackQuery = (Button) findViewById(R.id.btn_trackQuery);
        btnTrackQuery.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();

        bmapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = bmapView.getMap();
        bmapView.showZoomControls(false);

    }

    private void initOnEntityListener() {
        entityListener = new OnEntityListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Toast.makeText(getApplicationContext(),
                        "entity请求失败回调接口消息 : " + arg0, Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            }

            // 添加entity回调接口
            @Override
            public void onAddEntityCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Toast.makeText(getApplicationContext(),
                        "添加entity回调接口消息 : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // 查询entity列表回调接口 返回每次查询得到的轨迹信息
            @Override
            public void onQueryEntityListCallback(String message) {
                // TODO Auto-generated method stub

            }
            //当收到位置信息,就让fragment开始展示轨迹
            @Override
            public void onReceiveLocation(final TraceLocation location) {
                // TODO Auto-generated method stub
                System.out.println("onReceiveLocation: "+location);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "纬度: " + location.getLatitude()+"  经度:"+location.getLongitude(), Toast.LENGTH_SHORT).show();
                    }
                });

                if (mTrackUploadFragment != null) {
                    System.out.println("有没有调用showRealtimeTrack");
                    mTrackUploadFragment.showRealtimeTrack(location);
                }
            }

        };
    }
    /**
     * 初始化OnTrackListener
     * 回调成功后: 历史轨迹fragment展示历史轨迹
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Toast.makeText(YingyanActivity.this, "track请求失败回调接口消息 : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
                // TODO Auto-generated method stub
                super.onQueryHistoryTrackCallback(arg0);
                mTrackQueryFragment.showHistoryTrack(arg0);
            }
            /**
             * 轨迹中的每个位置点可拥有一系列开发者自定义的描述字段，如汽车的油量、发动机转速等，
             * 用以记录行程中的实时状态信息。开发者须重写OnTrackListener监听器中的onTrackAttrCallback()接口，
             * 在回传轨迹点时回传属性数据。 注：SDK根据位置采集周期回调该接口，获取轨迹属性数据。
             * */
            //轨迹属性回调接口
            @Override
            public Map onTrackAttrCallback() {
                return super.onTrackAttrCallback();
            }
        };
    }
    /**
     * 处理tab点击事件
     *
     * @param id
     */
    private void handlerButtonClick(int id) {
        // 重置button状态
        onResetButton();
        // 开启Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 隐藏Fragment
        hideFragments(transaction);

        switch (id) {

            case R.id.btn_trackQuery:

                TrackUploadFragment.isInUploadFragment = false;

                if (mTrackQueryFragment == null) {
                    mTrackQueryFragment = new TrackQueryFragment();
                    transaction.add(R.id.fragment_content, mTrackQueryFragment);
                } else {
                    transaction.show(mTrackQueryFragment);
                }
                mTrackQueryFragment.addMarker();
                btnTrackQuery.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                btnTrackQuery.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                mBaiduMap.setOnMapClickListener(null);
                break;

            case R.id.btn_trackUpload:

                TrackUploadFragment.isInUploadFragment = true;

                if (mTrackUploadFragment == null) {
                    System.out.println("有没有调用new TrackUploadFragment();");
                    mTrackUploadFragment = new TrackUploadFragment();
                    transaction.add(R.id.fragment_content, mTrackUploadFragment);
                } else {
                    transaction.show(mTrackUploadFragment);
                }

                TrackUploadFragment.addMarker();
                mTrackUploadFragment.startRefreshThread(true);
                btnTrackUpload.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                btnTrackUpload.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                mBaiduMap.setOnMapClickListener(null);
                break;
        }
        // 事务提交
        transaction.commit();

    }
    /**
     * 点击事件
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        handlerButtonClick(v.getId());
    }
    /**
     * 重置button状态
     */
    private void onResetButton() {
        btnTrackQuery.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        btnTrackQuery.setBackgroundColor(Color.rgb(0xFF, 0xFF, 0xFF));
        btnTrackUpload.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        btnTrackUpload.setBackgroundColor(Color.rgb(0xFF, 0xFF, 0xFF));
    }

    /**
     * 隐藏Fragment
     */
    private void hideFragments(FragmentTransaction transaction) {

        if (mTrackQueryFragment != null) {
            transaction.hide(mTrackQueryFragment);
        }
        if (mTrackUploadFragment != null) {
            transaction.hide(mTrackUploadFragment);
        }
        // 清空地图覆盖物
        mBaiduMap.clear();
    }

    private void addEntity() {
        Geofence.addEntity();
    }

    private void setDefaultFragment() {
        handlerButtonClick(R.id.btn_trackUpload);
    }

}
