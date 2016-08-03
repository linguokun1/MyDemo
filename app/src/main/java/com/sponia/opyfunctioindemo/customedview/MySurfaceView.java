package com.sponia.opyfunctioindemo.customedview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sponia.opyfunctioindemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.customedview
 * @description
 * @date 16/7/8
 */
//1.自定义SurfaceView类
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawingThread drawingThread;

    public MySurfaceView(Context context) {
        super(context);
        initParams();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    //2.监听SurfaceView的生命周期
    private void initParams() {
        this.getHolder().addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //创建过程
        //6. 通过SurfaceView的生命周期来执行我们的线程,绘制我们的图片
        drawingThread = new DrawingThread(getHolder(), BitmapFactory.decodeResource(getResources(), R.drawable.ico_competition_performance_star));
        drawingThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //改变过程  例如: SurfaceView的宽度, 高度发生了改变.
        this.drawingThread.updateSize(width, height);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //销毁过程 释放线程
        this.drawingThread.quit();
        this.drawingThread = null;
    }

    //7.添加触摸事件,创建DrawingItem对象


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //监听down事件
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //添加图片对象
            this.drawingThread.addItem(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }
    //清空图片
    public void clear(){
        this.drawingThread.clear();
    }

    //3.自定义线程,继承HandlerThread
    class DrawingThread extends HandlerThread implements Handler.Callback {

        //4.应以线程处理相关的参数
        //4.1 定义消息类型
        //创建消息
        private static final int MSG_ADD = 101;
        //点击消息(消息移动)
        private static final int MSG_MOVE = 102;
        //清理消息
        private static final int MSG_CLEAR = 103;

        //定义SurfaceView的宽高
        private int drawingWidth, drawingHeight;
        //缓存视图
        private SurfaceHolder drawingHolder;
        //画笔
        private Paint paint;
        //我们需要绘制的图片
        private Bitmap iconBitmap;
        //图像对象数组
        private List<DrawingItem> locations;
        //定义Handler, 更新UI线程
        private Handler receiver;
        //线程是否在运行
        private boolean isRunning;

        public DrawingThread(SurfaceHolder drawingHolder, Bitmap bitmap) {
            super("DrawingThread");
            this.drawingHolder = drawingHolder;
            this.iconBitmap = bitmap;
            this.locations = new ArrayList<>();
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        }

        public void updateSize(int width, int height){
            this.drawingHeight = height;
            this.drawingWidth = width;
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case MSG_ADD:
                    Random random =new Random();
                    DrawingItem drawingItem = new DrawingItem(msg.arg1, msg.arg2, random.nextBoolean(), random.nextBoolean());
                    locations.add(drawingItem);
                    break;
                case MSG_CLEAR:
                    //清空图片
                    locations.clear();
                    break;
                case MSG_MOVE:
                    //绘图
                    if(!isRunning){
                        return true;
                    }
                    //获取画布
                    //获取加锁画布  为什么是加锁? 因为很可能在绘制过程中其他线程也在操作, 所以防止线程问题加锁
                    Canvas lockCanvas = this.drawingHolder.lockCanvas();
                    if(lockCanvas == null){
                        break;
                    }
                    //清空画布
                    lockCanvas.drawColor(Color.BLACK);
                    //循环绘图
                    for(DrawingItem item : locations){
                        //思考:我们如何让我们的图片运动?
                        item.x += item.isHorizontal ? 5 : -5;
                        if(item.x >= this.drawingWidth - iconBitmap.getWidth()){//其实就是图片横向运动范围
                            item.isHorizontal = false;
                        }else{
                            item.isHorizontal = true;
                        }

                        item.y += item.isVertical ? 5 : -5;
                        if(item.y >= this.drawingHeight - iconBitmap.getHeight()){
                            item.isVertical = false;
                        }else{
                            item.isVertical = true;
                        }

                        //绘图
                        lockCanvas.drawBitmap(iconBitmap, item.x, item.y, paint);

                    }
                    this.drawingHolder.unlockCanvasAndPost(lockCanvas);//解锁, 加了锁后一定要解锁,否则会出巨大问题,例如阻塞,崩溃.
                    break;
                default:
                    break;

            }

            //如果我们的线程正在运行,那么我们需要循环不断的,持续的发消息;
            if(isRunning){
                this.receiver.sendEmptyMessage(MSG_MOVE);
            }
            return false;
        }


        //5.监听线程的生命周期
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            //提供给我们初始化基本参数
            this.receiver = new Handler(getLooper(), this);//把当前线程的轮询器与当前线程对象(实现了CallBack,一旦实现CallBack,就要覆写handleMessage方法)放入Handler
            this.isRunning = true;

            //发一个默认消息
            this.receiver.sendEmptyMessage(MSG_ADD);

        }

        @Override
        public boolean quit() {
            this.isRunning = false;
            this.receiver.removeCallbacksAndMessages(null);
            return super.quit();
        }


        public void addItem(float x, float y){
            Message msg = Message.obtain(receiver,MSG_ADD, (int)x, (int)y);
            this.receiver.sendMessage(msg);
        }

        //清空图片
        public void clear(){
            this.receiver.sendEmptyMessage(MSG_CLEAR);
        }
        class DrawingItem{
            int x, y;
            //运动的方向
            boolean isVertical, isHorizontal;

            public DrawingItem(int x, int y, boolean isVertical, boolean isHorizontal) {
                this.x = x;
                this.y = y;
                this.isVertical = isVertical;
                this.isHorizontal = isHorizontal;
            }
        }
    }
}
