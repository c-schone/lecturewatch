package com.doggiesmile.lecturewatchalarm4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerTwoActivity extends WearableActivity {

    private static final String TAG = "TimerTwoActivity";
    private static final int SWIPE_DISTANCE_THRESHOLD = 100;

    private BoxInsetLayout mContainerView;
    private TextView mTimerTwoName;
    private TextView mTimerTwoTimer;
    private TextView mPreviousAcitivty;
    private CountDownTimer mTimerTwoCountDown;
    private boolean mTimerTwoActive = false;
    private Vibrator mVibrator;
    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mDetector;
    private Intent mParentIntent;

    private PowerManager pm;
    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentIntent = getIntent();
        setContentView(R.layout.activity_timer_two);

        //Initialise Wakelock for Ambient Displayupdates
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "awake");

        //View usable for square and round watches
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        //View to quit Activity with long press detection on screen
        mDismissOverlayView = new DismissOverlayView(TimerTwoActivity.this);
        mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        setupGestureDetector();
        initialiseTextViews();
        updateDisplay();

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    //Initialise TextViews
    private void initialiseTextViews() {
        mTimerTwoName = (TextView) findViewById(R.id.timerName);
        mTimerTwoName.setText("Timer 2");
        mTimerTwoTimer = (TextView) findViewById(R.id.timerTimer);
        mTimerTwoTimer.setText("00:30");
        mTimerTwoTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mTaskTimer onClickEvent");
                onTaskTimerClick(view);
            }
        });
        mTimerTwoName.setY(mTimerTwoTimer.getY()-mTimerTwoTimer.getTextSize()+20);
        mPreviousAcitivty = (TextView) findViewById(R.id.previousActivityName);
        mPreviousAcitivty.setLineSpacing(1, 0.75f);
        if (mParentIntent.getStringExtra("Activity Key").matches("FocusTime2")) {
            mPreviousAcitivty.setText("Uhrzeit");
        } else if (mParentIntent.getStringExtra("Activity Key").matches("FocusChrono2")){
            mPreviousAcitivty.setText("Vortragsdauer");
        }
    }

    public void onTaskTimerClick(View v) {
        Log.d(TAG, "onTimer1Click");
        if(!mTimerTwoActive) {
            mTimerTwoActive = true;
            long[] vibrationPattern = {0, 100};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            mTimerTwoCountDown = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long secondsLeft = (millisUntilFinished / 1000) % 60;
                    Log.d(TAG, "SecondsLeft: "+secondsLeft);
                    long minutesLeft = (millisUntilFinished / 60000) % 60;
                    Log.d(TAG, "MinutesLeft: "+ minutesLeft);
                    if(secondsLeft < 10 && minutesLeft < 10) {
                        mTimerTwoTimer.setText("0" + minutesLeft + ":0" + secondsLeft);
                    } else if (secondsLeft < 10) {
                        mTimerTwoTimer.setText("" + minutesLeft + ":0" + secondsLeft);
                    } else if (minutesLeft < 10) {
                        mTimerTwoTimer.setText("0" + minutesLeft + ":" + secondsLeft);
                    } else {
                        mTimerTwoTimer.setText("" + minutesLeft + ":" + secondsLeft);
                    }
                    updateDisplay();
                }

                public void onFinish() {
                    mTimerTwoTimer.setText("00:30");
                    mTimerTwoActive = false;
                    long[] vibrationPattern = {0, 200, 100, 200, 100, 200, 100, 200};
                    //-1 - don't repeat
                    final int indexInPatternToRepeat = -1;
                    mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                }
            }.start();
        }
    }

    //Setup GestureDetector for TouchEvents
    public void setupGestureDetector() {
        mDetector = new GestureDetector(TimerTwoActivity.this, new GestureDetector.SimpleOnGestureListener(){
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
    }

    private void onSwipeLeft() {
        Log.d(TAG, "onSwipeLeft detected!");
    }

    private void onSwipeDown() {
        Log.d(TAG, "onSwipeDown detected!");
        Intent parentIntent = getIntent();
        String activityKey = parentIntent.getStringExtra("Activity Key");
        if(activityKey.matches("FocusTime2")) {
            Intent intent = new Intent(this, FocusTimeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slidedown, R.anim.fadeout);
        } else if(activityKey.matches("FocusChrono2")) {
            Intent intent = new Intent(this, FocusChronoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slidedown, R.anim.fadeout);
        } else {
            Log.d(TAG, "Error matching Key: " + activityKey);
        }
    }

    private void onSwipeUp() {
        Log.d(TAG, "onSwipeUp detected!");
    }

    //Determine if Touchevent is Viewbased or not
    @Override
    public boolean dispatchTouchEvent (MotionEvent e) {
        return mDetector.onTouchEvent(e) || super.dispatchTouchEvent(e);
    }

    //Enter Ambient Mode: Activate Wakelock for Displayupdates on every Tick
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
        wl.acquire();
        Log.d(TAG, "Enter Ambient");
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    //Exit Ambient Mode: Release Wakelock
    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        updateDisplay();
        wl.release();
    }

    //Update Displayinformation depending on Mode (Ambient or Active)
    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            setTextViewColors(android.R.color.white);
            setTextViewAntiAlias(false);
            setTextViewToStroke();
        } else {
            mContainerView.setBackground(null);
            setTextViewColors(android.R.color.black);
            setTextViewAntiAlias(true);
            setTextViewToFill();
        }
    }

    //Fills Characters of TextView
    private void setTextViewToFill() {
        Paint mTaskTimerPaint = mTimerTwoTimer.getPaint();
        mTaskTimerPaint.setStyle(Paint.Style.FILL);
    }

    //Show only lineouts of Characters to prevent burn-in
    private void setTextViewToStroke() {
        Paint mTaskTimerPaint = mTimerTwoTimer.getPaint();
        mTaskTimerPaint.setStyle(Paint.Style.STROKE);
        mTaskTimerPaint.setStrokeWidth(2);
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
        if(wl.isHeld()) {
            wl.release();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimerTwoActive) {
            mTimerTwoCountDown.cancel();
        }
    }
}
