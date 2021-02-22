package com.example.paintaula;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

//TODO
// Detect a double tap
// Detect a long press
//TODO
// when double tap is detected the app should enter in "erase mode"
// this mode changes the color of the paint to color of the background
//TODO
// when a long press is detect change the background color with a random one

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "Gestures";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleTouchEventView paintCanvas = new SingleTouchEventView(getApplicationContext(), null);
        setContentView(paintCanvas);// adds the created view to the screen
    }

    class SingleTouchEventView extends View implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, SensorEventListener {
        private Paint paint = new Paint();
        private Path path = new Path();


        private GestureDetectorCompat mDetector;
        private SensorManager mSensorManager;
        private Sensor priximtySensor;
        private float mAccel; // acceleration apart from gravity
        private float mAccelCurrent; // current acceleration including gravity
        private float mAccelLast; // last acceleration including gravity
        private ArrayList<Path> paths = new ArrayList<Path>();
        private float orignX;
        private float orignY;
        private int newcolor;

        public SingleTouchEventView(Context context, AttributeSet attrs) {
            super(context, attrs);

            paint.setAntiAlias(true);
            paint.setStrokeWidth(20f);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);


            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            priximtySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(this, priximtySensor, SensorManager.SENSOR_DELAY_NORMAL);

            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            mAccel = 0.00f;
            mAccelCurrent = SensorManager.GRAVITY_EARTH;
            mAccelLast = SensorManager.GRAVITY_EARTH;


            mDetector = new GestureDetectorCompat(context, this);
            mDetector.setOnDoubleTapListener(this);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.drawPath(path, paint);// draws the path with the paint
            for (Path p : paths) {
                canvas.drawPath(p, paint);
            }
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean performClick(){
            return super.performClick();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //path.moveTo(eventX, eventY);// updates the path initial point
                    path = new Path();
                    path.moveTo(eventX, eventY);
                    this.mDetector.onTouchEvent(event);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //path.lineTo(eventX, eventY);// makes a line to the point each time this event is fired
                    //break;
                    path.lineTo(eventX, eventY);
                    path.moveTo(eventX, eventY);
                    paths.add(path);
                    path = new Path();
                    path.moveTo(eventX, eventY);
                    this.mDetector.onTouchEvent(event);
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:// when you lift your finger
                    //performClick();
                    //break;
                this.mDetector.onTouchEvent(event);
                invalidate();
                return false;
                default:
                    //return false;
                    return super.onTouchEvent(event);
            }

            // Schedules a repaint.
            //invalidate();
            //return true;


        }




        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Random rnd = new Random();
            paint.setColor(newcolor);
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            setBackgroundColor(color);
            newcolor = color;

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta; // perform low-cut filter
                if (mAccel > 10) {
                    paths.clear();
                    invalidate();
                } else if (mAccel > 0.5) {
                    Log.d(DEBUG_TAG, "logou: "+paths.size()  + event.toString());
                    if (paths.size() != 0){
                        paths.remove(paths.size() - 1);
                        invalidate();}

                }
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                float distance = event.values[0];
                //event.sensor.getType()
                if (event.values[0] >= -4 && event.values[0] <= 4) {
                    setBackgroundColor(Color.BLACK);
                } else {
                    setBackgroundColor(Color.WHITE);
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}