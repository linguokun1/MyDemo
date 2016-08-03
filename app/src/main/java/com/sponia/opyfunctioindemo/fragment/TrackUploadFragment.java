package com.sponia.opyfunctioindemo.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.TraceLocation;
import com.sponia.opyfunctioindemo.R;
import com.sponia.opyfunctioindemo.activity.Geofence;
import com.sponia.opyfunctioindemo.activity.YingyanActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * 轨迹追踪
 */
@SuppressLint("NewApi")
public class TrackUploadFragment extends Fragment {

    private Button btnStartTrace = null;

    private Button btnStopTrace = null;

    private Button btnOperator = null;

    protected TextView tvEntityName = null;

    private Geofence geoFence = null;

    /**
     * 开启轨迹服务监听器
     */
    protected static OnStartTraceListener startTraceListener = null;

    /**
     * 停止轨迹服务监听器
     */
    protected static OnStopTraceListener stopTraceListener = null;

    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 10;//2 * 60

    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 15;//4 * 60

    /**
     * 图标
     */
    private static BitmapDescriptor realtimeBitmap;

    // 覆盖物
    protected static OverlayOptions overlay;
    // 路线覆盖物
    private static PolylineOptions polyline = null;

    private static List<LatLng> pointList = new ArrayList<LatLng>();

    protected boolean isTraceStart = false;

    /**
     * 刷新地图线程(获取实时点)
     */
    protected RefreshThread refreshThread = null;

    public static MapStatusUpdate msUpdate = null;

    private View view = null;

    private LayoutInflater mInflater = null;

    public static boolean isInUploadFragment = true;

    private static boolean isRegister = false;

    protected static PowerManager pm = null;

    protected static WakeLock wakeLock = null;

    private PowerReceiver powerReceiver = new PowerReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_trackupload, container, false);

        mInflater = inflater;

        return view;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        // 初始化
        init();

        // 初始化监听器
        initListener();

        // 设置采集周期
        setInterval();

        // 设置http请求协议类型
        setRequestType();
        queryEntityList();
    }

    /**
     * 初始化
     */
    private void init() {

        btnStartTrace = (Button) view.findViewById(R.id.btn_startTrace);

        btnStopTrace = (Button) view.findViewById(R.id.btn_stopTrace);

        btnOperator = (Button) view.findViewById(R.id.btn_operator);

        tvEntityName = (TextView) view.findViewById(R.id.tv_entityName);

        tvEntityName.setText(" entityName : " + YingyanActivity.entityName + " ");

        btnStartTrace.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "正在开启轨迹服务，请稍候", Toast.LENGTH_LONG).show();
                startTrace();

                if (!isRegister) {
                    if (null == pm) {
                        pm = (PowerManager) YingyanActivity.mContext.getSystemService(Context.POWER_SERVICE);
                    }
                    if (null == wakeLock) {
                        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TrackUpload");
                    }
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    filter.addAction(Intent.ACTION_SCREEN_ON);
                    YingyanActivity.mContext.registerReceiver(powerReceiver, filter);//注册电量锁广播接收器
                    isRegister = true;
                }
            }
        });

        btnStopTrace.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "正在停止轨迹服务，请稍候", Toast.LENGTH_SHORT).show();
                stopTrace();
                if (isRegister) {
                    try {
                        YingyanActivity.mContext.unregisterReceiver(powerReceiver);
                        isRegister = false;
                    } catch (Exception e) {
                        // TODO: handle
                    }

                }
            }
        });

        btnOperator.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                geoFence = new Geofence(getActivity(), mInflater);
                if (geoFence.popupwindow != null && geoFence.popupwindow.isShowing()) {
                    geoFence.popupwindow.dismiss();
                    return;
                } else {
                    geoFence.initPopupWindowView();
                    geoFence.popupwindow.showAsDropDown(v, 0, 5);
                }
            }
        });

    }
    /**
     * 开启轨迹服务
     */
    private void startTrace() {
        // 通过轨迹服务客户端client开启轨迹服务
        YingyanActivity.client.startTrace(YingyanActivity.trace, startTraceListener);
    }

    /**
     * 停止轨迹服务
     */
    private void stopTrace() {
        // 通过轨迹服务客户端client停止轨迹服务
        YingyanActivity.client.stopTrace(YingyanActivity.trace, stopTraceListener);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 初始化开启轨迹服务监听器
        initOnStartTraceListener();

        // 初始化停止轨迹服务监听器
        initOnStopTraceListener();
    }
    /**
     * 初始化OnStartTraceListener
     */
    private void initOnStartTraceListener() {
        // 初始化startTraceListener
        startTraceListener = new OnStartTraceListener() {

            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            public void onTraceCallback(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showMessage("开启轨迹服务回调接口消息 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]", Integer.valueOf(arg0));
                if (0 == arg0 || 10006 == arg0 || 10008 == arg0) {
                    isTraceStart = true;
                    startRefreshThread(true);//启动刷新线程
                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            public void onTracePushCallback(byte arg0, String arg1) {
                // TODO Auto-generated method stub
                showMessage("轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
            }

        };
    }

    /**
     * 初始化OnStopTraceListener
     */
    private void initOnStopTraceListener() {
        // 初始化stopTraceListener
        stopTraceListener = new OnStopTraceListener() {

            // 轨迹服务停止成功
            public void onStopTraceSuccess() {
                // TODO Auto-generated method stub
                showMessage("停止轨迹服务成功", Integer.valueOf(1));
                isTraceStart = false;
                startRefreshThread(false);
            }

            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showMessage("停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
            }
        };
    }

    /**
     * 设置采集周期和打包周期
     */
    private void setInterval() {
        YingyanActivity.client.setInterval(gatherInterval, packInterval);
    }

    /**
     * 设置请求协议
     */
    private void setRequestType() {
        int type = 0;
        YingyanActivity.client.setProtocolType(type);
    }

    /**
     * 查询entityList
     */
    @SuppressWarnings("unused")
    private void queryEntityList() {
        // entity标识列表（多个entityName，以英文逗号"," 分割）
        String entityNames = YingyanActivity.entityName;
        // 属性名称（格式为 : "key1=value1,key2=value2,....."）
        String columnKey = "";
        // 返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
        int returnType = 2;
        // 活跃时间（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
        int activeTime = (int) (System.currentTimeMillis() / 1000 - 30);
        // 分页大小
        int pageSize = 10;
        // 分页索引
        int pageIndex = 1;

        YingyanActivity.client.queryEntityList(YingyanActivity.serviceId, entityNames, columnKey, returnType, activeTime,
                pageSize,
                pageIndex, YingyanActivity.entityListener);
    }

    /**
     * 刷新地图线程:
     * 用于不断地 查询实时轨迹 显示轨迹
     * */
    protected class RefreshThread extends Thread {

        protected boolean refresh = true;

        @Override
        public void run() {
            // TODO Auto-generated method stub

            while (refresh) {
                // 查询实时轨迹
                queryRealtimeTrack();
                try {
                    Thread.sleep(gatherInterval * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    System.out.println("线程休眠失败");
                }
            }

        }
    }

    /**
     * 查询实时轨迹
     * 获取到服务器返回的轨迹后,会在YingyanActivity的entityListener的实现方法中回调showRealtimeTrack()显示轨迹
     */
    private void queryRealtimeTrack() {
        YingyanActivity.client.queryRealtimeLoc(YingyanActivity.serviceId, YingyanActivity.entityListener);
    }

    /**
     * 显示实时轨迹
     */
    public void showRealtimeTrack(TraceLocation location) {

        if (null == refreshThread || !refreshThread.refresh) {//如果没点击开始按钮,就在这里终止,不会画地图
            return;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            showMessage("当前查询无轨迹点", null);

        } else {
            System.out.println("显示轨迹");
            LatLng latLng = new LatLng(latitude, longitude);

            pointList.add(latLng);

            if (isInUploadFragment) {
                // 绘制实时点
                drawRealtimePoint(latLng);
            }

        }

    }



    /**
     * 绘制实时点
     */
    private void drawRealtimePoint(LatLng point) {
        System.out.println("绘制实时点");
        YingyanActivity.mBaiduMap.clear();

        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18).build();

        msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        realtimeBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);

        overlay = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);

        if (pointList.size() >= 2 && pointList.size() <= 10000) {
            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(pointList);
        }

        addMarker();

    }

    /**
     * 添加地图覆盖物
     */
    public static void addMarker() {
        System.out.println("画覆盖物");
        if (null != msUpdate) {
            YingyanActivity.mBaiduMap.setMapStatus(msUpdate);
        }

        // 路线覆盖物
        if (null != polyline) {
            YingyanActivity.mBaiduMap.addOverlay(polyline);
        }

        // 围栏覆盖物
        if (null != Geofence.fenceOverlay) {
            YingyanActivity.mBaiduMap.addOverlay(Geofence.fenceOverlay);
        }

        // 实时点覆盖物
        if (null != overlay) {
            System.out.println("实时点覆盖物");
            YingyanActivity.mBaiduMap.addOverlay(overlay);
        }
    }

    public void startRefreshThread(boolean isStart) {
        if (null == refreshThread) {
            refreshThread = new RefreshThread();
        }
        refreshThread.refresh = isStart;
        if (isStart) {
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
        }
    }

    private void showMessage(final String message, final Integer errorNo) {

        new Handler(YingyanActivity.mContext.getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(YingyanActivity.mContext, message, Toast.LENGTH_LONG).show();

                if (null != errorNo) {
                    if (0 == errorNo.intValue() || 10006 == errorNo.intValue() || 10008 == errorNo.intValue()
                            || 10009 == errorNo.intValue()) {
                        btnStartTrace.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                        btnStartTrace.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                    } else if (1 == errorNo.intValue() || 10004 == errorNo.intValue()) {
                        btnStartTrace.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
                        btnStartTrace.setTextColor(Color.rgb(0x00, 0x00, 0x00));
                    }
                }
            }
        });

    }
}
