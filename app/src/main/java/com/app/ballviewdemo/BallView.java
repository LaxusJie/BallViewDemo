package com.app.ballviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by HJ on 2016/4/19 0019.
 */
public class BallView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
    private Context context;
    private SurfaceHolder surfaceHolder;
    private Thread thread;
    private boolean isrun;
    private Bitmap bgBitmap;
    private Bitmap waveBitmap;
    private Bitmap transparentBitmap;
    private Bitmap[] ballsBitmap = new Bitmap[3];
    private int[] ballsBg = {R.mipmap.point1, R.mipmap.point2, R.mipmap.point3};
    private int mRepeatCount;
    //水波速度
    private int speed = 5;
    //水波开始位置
    private int begin = 0;
    //睡眠时间
    private static final int SLEEPTIME = 30;
    //百分比
    private int percentNum = 0;
    //计数器
    private int count = 0;
    //文字
    private String percentText;
    //文字画笔
    Paint textPaint = new Paint();
    //小球画笔
    Paint[] ballsPaint = new Paint[3];
    //目标
    private int target = 99;

    public BallView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }


    private void init(Context context) {
        if (bgBitmap == null) {
            Bitmap mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.circle);
            bgBitmap = Bitmap.createScaledBitmap(mBitmap, getWidth(), getHeight(), false);
            Bitmap mWave = BitmapFactory.decodeResource(context.getResources(), R.mipmap.wave4);
            waveBitmap = Bitmap.createScaledBitmap(mWave, getWidth()/2, getHeight(), false);
            Bitmap mTrans = BitmapFactory.decodeResource(context.getResources(), R.mipmap.transparent);
            transparentBitmap = Bitmap.createScaledBitmap(mTrans, getWidth(), getHeight(), false);
            for(int i = 0; i < ballsBitmap.length; i++) {
                ballsPaint[i] = new Paint();
                ballsPaint[i].setAlpha(0);
                Bitmap mBall = BitmapFactory.decodeResource(context.getResources(), ballsBg[i]);
                ballsBitmap[i] = Bitmap.createScaledBitmap(mBall, getWidth(), mBall.getHeight(), false);
                mBall.recycle();
            }
            mWave.recycle();
            mBitmap.recycle();
            mTrans.recycle();
        }
    }

    private void drawView(Canvas canvas) {
        drawBg(canvas);
        drawWave(canvas);
        drawBall(canvas);
        drawText(canvas);
    }


    private void drawBg(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.colorDarkBlue));
        canvas.drawBitmap(bgBitmap, 0, 0, null);
    }

    private void drawWave(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        canvas.clipPath(path);
        path.addCircle(getWidth()/2, getHeight()/2, getWidth()/2, Path.Direction.CCW);
        canvas.clipPath(path, Region.Op.REPLACE);
        if(percentNum >= 100) {
            canvas.drawBitmap(transparentBitmap, 0, 0, null);
        } else {
            for(int i = 0; i < 4; i++) {
                Paint paint = new Paint();
                paint.setAlpha(100);
                canvas.drawBitmap(waveBitmap, begin + (i-2)*waveBitmap.getWidth(), getHeight() * (100- percentNum) * 0.01f, paint);
            }
        }
        canvas.restore();
        path.reset();
    }

    private void drawBall(Canvas canvas) {
        if(ballsPaint[0].getAlpha() < 200) {
            ballsPaint[0].setAlpha((count) * 4);
        }
        canvas.drawBitmap(ballsBitmap[0], 0, getHeight()*3/4, ballsPaint[0]);
        if(count > 10) {
            if(ballsPaint[1].getAlpha() < 200) {
                ballsPaint[1].setAlpha((count-10) * 4);
            }
            canvas.drawBitmap(ballsBitmap[1], 0, getHeight()*3/4, ballsPaint[1]);
        }
        if(count > 20) {
            if(ballsPaint[2].getAlpha() < 200) {
                ballsPaint[2].setAlpha((count-20) * 4);
            }
            canvas.drawBitmap(ballsBitmap[2], 0, getHeight()*3/4, ballsPaint[2]);
        }
    }

    private void drawText(Canvas canvas) {
        textPaint.setColor(Color.WHITE);
        if(percentNum == 100) {
            textPaint.setTextSize((3.0f/11)*getWidth());
            percentText = String.valueOf(percentNum);
            canvas.drawText(percentText, bgBitmap.getWidth() / 4.5f, bgBitmap.getHeight() * 2 / 3, textPaint);
            float textWidth = textPaint.measureText(percentText);
            textPaint.setTextSize((1.0f/5)*getWidth());
            canvas.drawText("%", bgBitmap.getWidth() / 4.5f + textWidth, bgBitmap.getHeight() * 2 / 3, textPaint);
        } else {
            textPaint.setTextSize((5.0f/11)*getWidth());
            if (percentNum < 10) {
                percentText = "0" + percentNum;
            } else {
                percentText = String.valueOf(percentNum);
            }
            canvas.drawText(percentText, bgBitmap.getWidth() / 6, bgBitmap.getHeight() * 2 / 3, textPaint);
            float textWidth = textPaint.measureText(percentText);
            textPaint.setTextSize((1.0f/5)*getWidth());
            canvas.drawText("%", bgBitmap.getWidth() / 6 + textWidth, bgBitmap.getHeight() * 2 / 3, textPaint);
        }
    }

    private void update() {
        begin +=speed;
        if(begin > getWidth()) {
            begin = 0;
        }
        if(percentNum < target) {
            percentNum += 1;
        } else {
            percentNum = target;
        }
        count ++;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isrun = false;
        thread = null;
    }

    @Override
    public void run() {
        while (isrun) {
            Canvas c = null;
            init(context);
            synchronized (surfaceHolder) {
                c = surfaceHolder.lockCanvas();
                drawView(c);
                update();
            }
            if (c != null) {
                surfaceHolder.unlockCanvasAndPost(c);
            }
            try {
                Thread.sleep(SLEEPTIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void start() {
        isrun = true;
    }

    public void stop() {
        isrun = false;
    }

}
