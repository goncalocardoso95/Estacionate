package com.example.paint;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {

    private GestureDetectorCompat mDetector;
    private static final String DEBUG_TAG = "Gestures";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = new SingleTouchEventView(this, null);
        setContentView(v);
//        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        this.mDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
//    }
//
//
//
//    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
//        private static final String DEBUG_TAG = "Gestures";
//
//        @Override
//        public boolean onDoubleTap(MotionEvent event) {
//
//            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
//            return true;
//        }
//    }
}