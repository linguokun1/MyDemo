package com.sponia.opyfunctioindemo.customedview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.customedview
 * @description
 * @date 16/7/8
 */
public class FaceCuteView  extends SurfaceView implements SurfaceHolder.Callback {

    private DrawingThread drawingThread;

    public FaceCuteView(Context context) {
        super(context);
        initParams();
    }

    public FaceCuteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    //2.监听SurfaceView的生命周期
    private void initParams() {
        this.getHolder().addCallback(this);
        this.setZOrderOnTop(true);//把最新绘制的至于顶部
//        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //创建过程
        //6. 通过SurfaceView的生命周期来执行我们的线程,绘制我们的图片
        drawingThread = new DrawingThread(getHolder());
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

    //3.自定义线程,继承HandlerThread                   监听线程生命周期
    class DrawingThread extends HandlerThread implements Handler.Callback {

        //4.应以线程处理相关的参数
        //4.1 定义消息类型
        //创建消息
        private static final int MSG_ADD = 101;
        //点击消息(消息移动)
        private static final int MSG_MOVE = 102;
        //清理消息
        private static final int MSG_CLEAR = 103;
        //画发型
        private static final int MSG_DRAW_HAIR = 104;
        //画脸型
        private static final int MSG_DRAW_FACE = 105;
        //画眼镜
        private static final int MSG_DRAW_GLASSES = 106;

        //定义SurfaceView的宽高
        private int drawingWidth, drawingHeight;
        //缓存视图
        private SurfaceHolder drawingHolder;
        //画笔
        private Paint paint;
        //定义Handler, 更新UI线程
        private Handler receiver;
        //线程是否在运行
        private boolean isRunning;

        public DrawingThread(SurfaceHolder drawingHolder) {
            super("DrawingThread");
            this.drawingHolder = drawingHolder;
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
                    break;
                case MSG_CLEAR:
                    //清空图片
                    if(!isRunning){
                        return true;
                    }
                    drawAvartar();
                    break;
                case MSG_MOVE:
//                    //绘图
//                    if(!isRunning){
//                        return true;
//                    }
//                    //获取画布
//                    //获取加锁画布  为什么是加锁? 因为很可能在绘制过程中其他线程也在操作, 所以防止线程问题加锁
//                    Canvas lockCanvas = this.drawingHolder.lockCanvas();
//                    if(lockCanvas == null){
//                        break;
//                    }
//                    //清空画布
//                    lockCanvas.drawColor(Color.BLACK);
//                    //循环绘图
//                    for(DrawingItem item : locations){
//                        //思考:我们如何让我们的图片运动?
//                        item.x += item.isHorizontal ? 5 : -5;
//                        if(item.x >= this.drawingWidth - iconBitmap.getWidth()){//其实就是图片横向运动范围
//                            item.isHorizontal = false;
//                        }else{
//                            item.isHorizontal = true;
//                        }
//
//                        item.y += item.isVertical ? 5 : -5;
//                        if(item.y >= this.drawingHeight - iconBitmap.getHeight()){
//                            item.isVertical = false;
//                        }else{
//                            item.isVertical = true;
//                        }
//
//                        //绘图
//                        lockCanvas.drawBitmap(iconBitmap, item.x, item.y, paint);
//
//                    }
//                    this.drawingHolder.unlockCanvasAndPost(lockCanvas);//解锁, 加了锁后一定要解锁,否则会出巨大问题,例如阻塞,崩溃.
                    break;
                case MSG_DRAW_FACE:
                    //绘图
                    if(!isRunning){
                        return true;
                    }
                    drawAvartar();


                    break;
                case MSG_DRAW_HAIR:
                    //绘图
                    if(!isRunning){
                        return true;
                    }
                    drawAvartar();
                    break;
                default:
                    break;

            }

            //如果我们的线程正在运行,那么我们需要循环不断的,持续的发消息;
            if(isRunning){
//                this.receiver.sendEmptyMessage(MSG_MOVE);
            }
            return false;
        }

        private void drawAvartar() {
            //获取画布
            //获取加锁画布  为什么是加锁? 因为很可能在绘制过程中其他线程也在操作, 所以防止线程问题加锁
            Canvas mMlockCanvas = this.drawingHolder.lockCanvas();

            if(mMlockCanvas != null){

//                mMlockCanvas.drawColor(Color.WHITE);
                Bitmap bitmap = Bitmap.createBitmap(261, 276, Bitmap.Config.ARGB_8888);

                Paint paint = new Paint();

                paint.setAntiAlias(true);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mMlockCanvas.drawPaint(paint);//清空之前的内容

                /**这里绘制的是显示到SurfaceView上*/
                if(faceList != null && faceList.size()>0){
                    faceList.get(0).draw(mMlockCanvas);
                }

                if(hairList != null && hairList.size()>0){
                    hairList.get(0).draw(mMlockCanvas);
                }

                /**下面这里是把drawable放入bitmap里*/
                mMlockCanvas.setBitmap(bitmap);//此图//这句话之后的drawable无法显示在SurfaceView上
                mMlockCanvas.drawBitmap(bitmap, 0, 0, paint);//把bitmap放入画布

                if(faceList != null && faceList.size()>0){
                    faceList.get(0).draw(mMlockCanvas);
                }

                if(hairList != null && hairList.size()>0){
                    hairList.get(0).draw(mMlockCanvas);
                }

                mMlockCanvas.save(Canvas.ALL_SAVE_FLAG);// 存储新合成的图片
                mMlockCanvas.restore();

//                Matrix m = new Matrix();


                File file = new File(Environment.getExternalStorageDirectory().getPath().toString());
                if(!file.exists())
                    file.mkdirs();
                try {
                    FileOutputStream fos = new FileOutputStream(file.getPath() + "/2.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    System.out.println("saveBmp is here");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.drawingHolder.unlockCanvasAndPost(mMlockCanvas);//解锁, 加了锁后一定要解锁,否则会出巨大问题,例如阻塞,崩溃.
            }

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
            faceList.clear();
            hairList.clear();
            this.receiver.sendEmptyMessage(MSG_CLEAR);
        }

        public void drawFace() {
            this.receiver.sendEmptyMessage(MSG_DRAW_FACE);
        }

        public void drawHair() {
            this.receiver.sendEmptyMessage(MSG_DRAW_HAIR);
        }


    }
    //清空图片
    public void clear(){
        this.drawingThread.clear();
    }
    //绘制脸型
    public void drawFace(Drawable drawable){
        faceList.clear();
        faceList.add(drawable);
        this.drawingThread.drawFace();
    }
    //绘制脸型
    public void drawHair(Drawable drawable){
        hairList.clear();
        hairList.add(drawable);
        this.drawingThread.drawHair();
    }
    ArrayList<Drawable> hairList = new ArrayList<>();
    ArrayList<Drawable> faceList = new ArrayList<>();
}
