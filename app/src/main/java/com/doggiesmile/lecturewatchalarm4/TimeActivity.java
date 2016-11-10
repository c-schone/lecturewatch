package com.doggiesmile.lecturewatchalarm4;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeActivity extends WearableActivity {

    private static final String TAG = "TimeActivity";
    private static final int SWIPE_DISTANCE_THRESHOLD = 170;

    private static final SimpleDateFormat THIS_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.GERMANY);

    private BoxInsetLayout mContainerView;
    private TextView mClockView;
    private TextView mClockName;
    private TextView mPreviousActivity;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        //Initialise Ambient Mode to save energy during use of application
        setAmbientEnabled();

        //View usable for square and round watches
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        //View to quit Activity with long press detection on screen
        mDismissOverlayView = new DismissOverlayView(TimeActivity.this);
        mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        setupGestureDetector();
        initialiseTextViews();
        updateDisplay();
    }

    //Initialise TextViews
    private void initialiseTextViews() {
        mClockName = (TextView) findViewById(R.id.clockName);
        mClockName.setText("Uhrzeit");
        mClockView = (TextView) findViewById(R.id.clock);
        //noinspection ResourceType
        mClockName.setY(mClockView.getY()-mClockView.getTextSize()+20);
        mPreviousActivity = (TextView) findViewById(R.id.previousActivityName);
        mPreviousActivity.setText("Vortragsdauer");
    }

    //Determine if Touchevent is Viewbased
    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }

    //Setup GestureDetector for TouchEvents
    public void setupGestureDetector() {
        mDetector = new GestureDetector(TimeActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress (MotionEvent e){
                // Detected long press, showing exit widget
                mDismissOverlayView.show();
            }

            //Determine Swipe Direction
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2,
                                   float velocityX, float velocityY) {
                float distanceX = event2.getX() - event1.getX();
                float distanceY = event2.getY() - event1.getY();
                Log.d(TAG, "onFling: distanceX = " + distanceX + "  distanceY = "+ distanceY);
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                } else if (Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD) {
                    if (distanceY > 0)
                        onSwipeDown();
                    else
                        onSwipeUp();
                    return true;
                }
                return false;
            }
        });
    }

    //Actions based on SwipeDirection
    private void onSwipeRight() {
        Log.d(TAG, "onSwipeRight detected!");
        //Do nothing because it would normally close the App
    }

    private void onSwipeLeft() {
        Log.d(TAG, "onSwipeLeft detected!");
    }

    private void onSwipeDown() {
        Log.d(TAG, "onSwipeDown detected!");
    }

    private void onSwipeUp() {
        Log.d(TAG, "onSwipeUp detected!");
        Intent intent = new Intent(this, FocusChronoActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slideup, R.anim.fadeout);
    }

    //Enter Ambient Mode: Activate Wakelock for Displayupdates on every Tick
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
        Log.d(TAG, "Enter Ambient");
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    //Exit Ambient Mode: Release Wakelock
    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        updateDisplay();
    }

    //Update Displayinformation depending on Mode (Ambient or Active)
    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            setTextViewColors(android.R.color.white);
            setTextViewAntiAlias(false);
            setTextViewToStroke();

            mClockView.setText(THIS_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            setTextViewColors(android.R.color.black);
            setTextViewAntiAlias(true);
            setTextViewToFill();

            mClockView.setText(THIS_DATE_FORMAT.format(new Date()));
        }
    }

    //Fills Characters of TextView
    private void setTextViewToFill() {
        Paint mClockPaint = mClockView.getPaint();
        mClockPaint.setStyle(Paint.Style.FILL);
    }

    //Show only lineouts of Characters to prevent burn-in
    private void setTextViewToStroke() {
        Paint mClockPaint = mClockView.getPaint();
        mClockPaint.setStyle(Paint.Style.STROKE);
        mClockPaint.setStrokeWidth(3);
    }

    //Turn Anti Aliasing on or off depending on Mode
    private void setTextViewAntiAlias(boolean b) {
        for(int i=0; i < mContainerView.getChildCount(); i++) {
            View v = mContainerView.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).getPaint().setAntiAlias(b);
            }
        }
    }

    //Change TextView color
    private void setTextViewColors(int color) {
        for(int i=0; i < mContainerView.getChildCount(); i++) {
            View v = mContainerView.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTextColor(getResources().getColor(color,this.getTheme()));
            }
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        updateDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
