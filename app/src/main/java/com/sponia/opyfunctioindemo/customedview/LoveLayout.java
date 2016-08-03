package com.sponia.opyfunctioindemo.customedview;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.sponia.opyfunctioindemo.R;

import java.util.Random;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.customedview
 * @description
 * @date 16/7/7
 */
public class LoveLayout extends RelativeLayout {
    private final DisplayMetrics dm = getResources().getDisplayMetrics();
    public final float density = dm.density;
    Drawable star;
    private int mWidth;
    private int mHeight;
    private int mIconHeight;
    private int mIconWidth;
    private int mStarHeight;
    private int mStarWidth;
    RelativeLayout.LayoutParams params;
    private Random random = new Random();
    public LoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        star = getResources().getDrawable(R.drawable.ico_competition_performance_star);
        //获取虫虫的宽高
        mIconHeight = dip2px(100);
        mIconWidth = dip2px(60);

        mStarHeight = star.getIntrinsicHeight();
        mStarWidth = star.getIntrinsicWidth();

        //初始化星星图片布局参数
        params = new RelativeLayout.LayoutParams(mStarWidth, mStarHeight);
        params.bottomMargin = mIconHeight;
        params.addRule(CENTER_HORIZONTAL, TRUE);
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE);
    }

    public int dip2px(float dpValue) {
        return (int) (dpValue * density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测绘 得到本Layout的宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();


    }



    public void addStar() {
        final ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(star);
        iv.setLayoutParams(params);
        addView(iv);
        ValueAnimator animator = getBezierValueAnimator(iv);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(iv);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private ValueAnimator getBezierValueAnimator(final ImageView iv) {
        PointF pointF0 = new PointF((mWidth - mStarWidth)/2, mHeight-mIconHeight);//起点
        PointF pointF1 = getPointF(1);//第一次到达点
        PointF pointF2 = getPointF(2);//第二次到达点
        PointF pointF3 = new PointF(mWidth-dip2px(20)-mStarWidth, 0+dip2px(20));//终点 mWidth-dip2px(20)-mStarWidth, 0+dip2px(20)  random.nextInt(mWidth), 1700

        //估值器Evaluator
        BezierEvaluator evaluator = new BezierEvaluator(pointF1, pointF2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, pointF0, pointF3);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                iv.setX(pointF.x);
                iv.setY(pointF.y);
            }
        });
        animator.setDuration(2500);
        animator.setTarget(iv);
//        animator.setInterpolator(new SpringInterpolator());
        return animator;
    }

    private PointF getPointF(int i) {
        PointF pointF = new PointF();
        pointF.x = random.nextInt(mWidth);
        if(i == 1){
            pointF.y = -2000; //random.nextInt(mHeight*1/5)
        }else{
            pointF.y = 4500;//4500
        }
        return pointF;
    }
}
