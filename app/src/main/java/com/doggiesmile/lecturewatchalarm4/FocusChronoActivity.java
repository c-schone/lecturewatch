package com.doggiesmile.lecturewatchalarm4;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FocusChronoActivity extends WearableActivity implements Chronometer.OnChronometerTickListener {

    private static final String TAG = "FocusChronoActivity";
    private static final int SWIPE_DISTANCE_THRESHOLD = 100;

    private BoxInsetLayout mContainerView;
    private TextView mChronoName;
    private TextView mUpperName;
    private TextView mRightName;
    private TextView mLowerName;
    private Chronometer mChronometer;
    private boolean mChronometerIsActive = false;
    private boolean chronoHours = false;
    private Vibrator mVibrator;
    private boolean firstVibe = false;
    private boolean secondVibe = false;
    private CountDownTimer mChronoCountDownOne;
    private CountDownTimer mChronoCountDownTwo;

    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mDetector;

    private PowerManager pm;
    PowerManager.WakeLock wl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_chrono);
        //Initialise Wakelock for Ambient Displayupdates
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "awake");

        //Initialise Ambient Mode to save energy during use of application
        setAmbientEnabled();

        //View usable for square and round watches
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        //View to quit Activity with long press detection on screen
        mDismissOverlayView = new DismissOverlayView(FocusChronoActivity.this);
        mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        mDetector = new GestureDetector(FocusChronoActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress (MotionEvent e){
                // Detected long press, showing exit widget
                mDismissOverlayView.show();
            }
        });

        setupGestureDetector();
        initialiseTextViews();
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }

    //Initialise TextViews
    private void initialiseTextViews() {
        mChronoName = (TextView) findViewById(R.id.chronoName);
        mChronoName.setText("Vortragsdauer");
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setText("Start");
        mChronometer.setOnChronometerTickListener(this);
        mChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mChronometerText onClickEvent");
                onChronometerClick(view);
            }
        });
        //noinspection ResourceType
        mChronoName.setY(mChronometer.getY()-mChronometer.getTextSize()+20);
        mUpperName = (TextView) findViewById(R.id.upperActivityName1Chrono);
        mUpperName.setText("Uhrzeit");
        mRightName = (TextView) findViewById(R.id.rightActivityName2Chrono);
        mRightName.setText("T\ni\nm\ne\nr\n1");
        mRightName.setLineSpacing(1, 0.75f);
        //mRightName.setText("Timer 1");
        mLowerName = (TextView) findViewById(R.id.lowerActivityName3Chrono);
        mLowerName.setText("Timer 2");
    }

    //Setup GestureDetector for TouchEvents
    public void setupGestureDetector() {
        mDetector = new GestureDetector(FocusChronoActivity.this, new GestureDetector.SimpleOnGestureListener(){
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
        //We do nothing because it would normally close the App
    }

    private void onSwipeLeft() {
        Log.d(TAG, "onSwipeLeft detected!");
        Intent intent = new Intent(this, TimerOneActivity.class);
        intent.putExtra("Activity Key", "FocusChrono1");
        startActivity(intent);
        overridePendingTransition(R.anim.slideleft, R.anim.fadeout);
    }

    private void onSwipeDown() {
        Log.d(TAG, "onSwipeDown detected!");
        Intent intent = new Intent(this, TimeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slidedown, R.anim.fadeout);
    }

    private void onSwipeUp() {
        Log.d(TAG, "onSwipeUp detected!");
        Intent intent = new Intent(this, TimerTwoActivity.class);
        intent.putExtra("Activity Key", "FocusChrono2");
        startActivity(intent);
        overridePendingTransition(R.anim.slideup, R.anim.fadeout);
    }

    public void onChronometerClick(View v) {
        Log.d(TAG, "onChronometerClick");
        if(mChronometerIsActive) {
            //mChronometer.stop();
            //mChronometerIsActive = false;
        } else {
            Log.d(TAG, "ElapsedRealtime: " + SystemClock.elapsedRealtime());
            //mChronometer.setBase(SystemClock.elapsedRealtime()- TimeUnit.MINUTES.toMillis(59));
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            //emChronometer.setFormat("00:%s");
            mChronometerIsActive = true;
            long[] vibrationPattern = {0, 100};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            if(mChronometerIsActive) {
                Log.d(TAG, "Chronometer Activated");
            } else {
                Log.d(TAG, "Chronometer start failed");
            }
            mChronoCountDownOne = new CountDownTimer(180000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long secondsLeft = (millisUntilFinished / 1000) % 60;
                    Log.d(TAG, "ChronoOneSecondsLeft: " + secondsLeft);
                    long minutesLeft = (millisUntilFinished / 60000) % 60;
                    Log.d(TAG, "ChronoOneMinutesLeft: " + minutesLeft);
                }

                public void onFinish() {
                    firstVibe = true;
                    long[] vibrationPattern = {0, 200, 100, 200, 100, 200, 100, 200};
                    //-1 - don't repeat
                    final int indexInPatternToRepeat = -1;
                    mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                }
            }.start();

            mChronoCountDownTwo = new CountDownTimer(300000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long secondsLeft = (millisUntilFinished / 1000) % 60;
                    Log.d(TAG, "ChronoOneSecondsLeft: " + secondsLeft);
                    long minutesLeft = (millisUntilFinished / 60000) % 60;
                    Log.d(TAG, "ChronoOneMinutesLeft: " + minutesLeft);
                }

                public void onFinish() {
                    secondVibe = true;
                    long[] vibrationPattern = {0, 200, 100, 200, 100, 200, 100, 200};
                    //-1 - don't repeat
                    final int indexInPatternToRepeat = -1;
                    mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                }
            }.start();
        }
        Log.d(TAG, mChronometer.getText().toString());
        updateDisplay();
    }

    public void onChronometerTick(Chronometer chronometer) {
        long elapsedChronoTime = (SystemClock.elapsedRealtime()-mChronometer.getBase())/1000;
        Log.d(TAG, "elapsedChronoTime: "+elapsedChronoTime);
       /* if((elapsedChronoTime >= 10) && (firstVibe == false)) {
            //Log.d(TAG, "Chronometer stop");
            //chronometer.stop();
            Log.d(TAG, "First Vibration");
            long[] vibrationPattern = {0, 200, 100, 200};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            firstVibe = true;
        } else if((elapsedChronoTime >= 30) && (secondVibe == false)) {
            Log.d(TAG, "Second Vibration");
            long[] vibrationPattern = {0, 500, 100, 500, 100, 500};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            secondVibe = true;
        }
        if(!chronoHours && elapsedChronoTime > 3600000) {
            chronometer.setFormat("%s");
            chronoHours = true;
        }*/

        updateDisplay();
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
        updateDisplay();
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

            mChronometer.setText(mChronometer.getText().toString());
        } else {
            mContainerView.setBackground(null);
            setTextViewColors(android.R.color.black);
            setTextViewAntiAlias(true);
            setTextViewToFill();

            mChronometer.setText(mChronometer.getText().toString());
        }
    }

    //Fills Characters of TextView
    private void setTextViewToFill() {
        Paint mChronometerPaint = mChronometer.getPaint();
        mChronometerPaint.setStyle(Paint.Style.FILL);
    }

    //Show only lineouts of Characters to prevent burn-in
    private void setTextViewToStroke() {
        Paint mChronometerPaint = mChronometer.getPaint();
        mChronometerPaint.setStyle(Paint.Style.STROKE);
        mChronometerPaint.setStrokeWidth(3);
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
        if(mChronometerIsActive) {
            mChronoCountDownOne.cancel();
            mChronoCountDownTwo.cancel();
        }

    }
}
