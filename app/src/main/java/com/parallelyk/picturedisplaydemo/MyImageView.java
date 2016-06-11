package com.parallelyk.picturedisplaydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by YK on 2016/6/11.
 */
public class MyImageView extends View {

    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private double mDistanceFinger;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(){

    }
    private void initBitmap(){

    }



    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        invalidate();
    }

    private void move(){

    }


    private void zoom(){

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed){
             mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_POINTER_DOWN:
                if(event.getPointerCount() ==2){
                    mDistanceFinger = getDistanceFinger(event);
                }

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_POINTER_UP:

            case MotionEvent.ACTION_UP:

        }


        return super.onTouchEvent(event);
    }



    private double getDistanceFinger(MotionEvent event){
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }
}
