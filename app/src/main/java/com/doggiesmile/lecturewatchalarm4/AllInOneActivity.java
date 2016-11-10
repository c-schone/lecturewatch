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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AllInOneActivity extends WearableActivity implements Chronometer.OnChronometerTickListener {

    private static final String TAG = "AllInOneActivity";

    private static final SimpleDateFormat THIS_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.GERMANY);

    private BoxInsetLayout mContainerView;
    private TextView mClockView;
    private TextView mClockName;
    private TextView mChronoName;
    private TextView mQuestionName;
    private TextView mQuestionTimer;
    private CountDownTimer mQuestionCountDown;
    private boolean mQuestionActive = false;
    private TextView mTaskName;
    private TextView mTaskTimer;
    private CountDownTimer mTaskCountDown;
    private boolean mTaskActive = false;
    private Chronometer mChronometer;
    private boolean mChronometerIsActive = false;
    private boolean chronoHours = false;
    private Vibrator mVibrator;

    private boolean firstVibe = false;
    private boolean secondVibe = false;

    private DismissOverlayView mDismissOverlayView;
    private GestureDetector mDetector;

    private PowerManager pm;
    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allinone);

        //Initialise Wakelock for Ambient Displayupdates
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "awake");

        //Initialise Ambient Mode to save energy during use of application
        setAmbientEnabled();

        //View usable for square and round watches
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        //View to quit Activity with long press detection on screen
        mDismissOverlayView = new DismissOverlayView(AllInOneActivity.this);
        mContainerView.addView(mDismissOverlayView,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        mDetector = new GestureDetector(AllInOneActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress (MotionEvent e){
                // Detected long press, showing exit widget
                mDismissOverlayView.show();
            }
        });

        initialiseTextViews();

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        updateDisplay();
    }

    //Initialise TextViews
    private void initialiseTextViews() {
        mClockName = (TextView) findViewById(R.id.clockName);
        mClockName.setText(" Uhr");
        mClockView = (TextView) findViewById(R.id.clock);
        mClockView.setX(-5);
        mClockView.setY(-10);
        mChronoName = (TextView) findViewById(R.id.chronoName);
        mChronoName.setY(-50);
        mChronoName.setText(" Vortrag");
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setX(-5);
        mChronometer.setText("Start");
        mChronometer.setOnChronometerTickListener(this);
        mChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mChronometerText onClickEvent");
                onChronometerClick(view);
            }
        });

        mQuestionName = (TextView) findViewById(R.id.questionName);
        mQuestionName.setY(-55);
        mQuestionName.setText(" Frage");
        mQuestionTimer = (TextView) findViewById(R.id.questionTimer);
        mQuestionTimer.setX(5);
        mQuestionTimer.setText("02:00");
        mQuestionTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mQuestionTimer onClickEvent");
                onQuestionTimerClick(view);
            }
        });
        mTaskName= (TextView) findViewById(R.id.taskName);
        mTaskName.setY(-55);
        mTaskName.setText("Aufgabe ");
        mTaskTimer = (TextView) findViewById(R.id.taskTimer);
        mTaskTimer.setX(-5);
        mTaskTimer.setText("05:00");
        mTaskTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mTaskTimer onClickEvent");
                onTaskTimerClick(view);
            }
        });
    }

    public void onChronometerClick(View v) {
        Log.d(TAG, "onChronometerClick");
        if(mChronometerIsActive) {
            //mChronometer.stop();
            //mChronometerIsActive = false;
        } else {
            Log.d(TAG, "ElapsedRealtime: "+SystemClock.elapsedRealtime());
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
        }

        Log.d(TAG, mChronometer.getText().toString());
        updateDisplay();
    }

    public void onQuestionTimerClick(View v) {
        Log.d(TAG, "onQuestionTimerClick");
        if(!mQuestionActive) {
            mQuestionActive = true;
            long[] vibrationPattern = {0, 100};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            mQuestionCountDown = new CountDownTimer(120000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long secondsLeft = (millisUntilFinished / 1000) % 60;
                    Log.d(TAG, "SecondsLeft: "+secondsLeft);
                    long minutesLeft = (millisUntilFinished / 60000) % 60;
                    Log.d(TAG, "MinutesLeft: "+ minutesLeft);
                    if(secondsLeft < 10 && minutesLeft < 10) {
                        mQuestionTimer.setText("0" + minutesLeft + ":0" + secondsLeft);
                    } else if (secondsLeft < 10) {
                        mQuestionTimer.setText("" + minutesLeft + ":0" + secondsLeft);
                    } else if (minutesLeft < 10) {
                        mQuestionTimer.setText("0" + minutesLeft + ":" + secondsLeft);
                    } else {
                        mQuestionTimer.setText("" + minutesLeft + ":" + secondsLeft);
                    }
                    updateDisplay();
                }

                public void onFinish() {
                    mQuestionTimer.setText("02:00");
                    mQuestionActive = false;
                    long[] vibrationPattern = {0, 200, 100, 200, 100, 200};
                    //-1 - don't repeat
                    final int indexInPatternToRepeat = -1;
                    mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                }
            }.start();
        }
    }

    public void onTaskTimerClick(View v) {
        Log.d(TAG, "onTaskTimerClick");
        if(!mTaskActive) {
            mTaskActive = true;
            long[] vibrationPattern = {0, 100};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            mTaskCountDown = new CountDownTimer(300000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long secondsLeft = (millisUntilFinished / 1000) % 60;
                    Log.d(TAG, "SecondsLeft: "+secondsLeft);
                    long minutesLeft = (millisUntilFinished / 60000) % 60;
                    Log.d(TAG, "MinutesLeft: "+ minutesLeft);
                    if(secondsLeft < 10 && minutesLeft < 10) {
                        mTaskTimer.setText("0" + minutesLeft + ":0" + secondsLeft);
                    } else if (secondsLeft < 10) {
                        mTaskTimer.setText("" + minutesLeft + ":0" + secondsLeft);
                    } else if (minutesLeft < 10) {
                        mTaskTimer.setText("0" + minutesLeft + ":" + secondsLeft);
                    } else {
                        mTaskTimer.setText("" + minutesLeft + ":" + secondsLeft);
                    }
                    updateDisplay();
                }

                public void onFinish() {
                    mTaskTimer.setText("05:00");
                    mTaskActive = false;
                    long[] vibrationPattern = {0, 200, 100, 200, 100, 200, 100, 200};
                    //-1 - don't repeat
                    final int indexInPatternToRepeat = -1;
                    mVibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                }
            }.start();
        }
    }

    public void onChronometerTick(Chronometer chronometer) {
        long elapsedChronoTime = (SystemClock.elapsedRealtime()-mChronometer.getBase())/1000;
        Log.d(TAG, "elapsedChronoTime: "+elapsedChronoTime);
        if((elapsedChronoTime >= 10) && (firstVibe == false)) {
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
        }
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

            mClockView.setText(THIS_DATE_FORMAT.format(new Date()));
            mChronometer.setText(mChronometer.getText().toString());
        } else {
            mContainerView.setBackground(null);
            setTextViewColors(android.R.color.black);
            setTextViewAntiAlias(true);
            setTextViewToFill();

            mClockView.setText(THIS_DATE_FORMAT.format(new Date()));
            mChronometer.setText(mChronometer.getText().toString());
        }
    }

    //Fills Characters of TextView
    private void setTextViewToFill() {
        Paint mChronometerPaint = mChronometer.getPaint();
        mChronometerPaint.setStyle(Paint.Style.FILL);
        Paint mQuestionTimerPaint = mQuestionTimer.getPaint();
        mQuestionTimerPaint.setStyle(Paint.Style.FILL);
        Paint mTaskTimerPaint = mTaskTimer.getPaint();
        mTaskTimerPaint.setStyle(Paint.Style.FILL);
    }

    //Show only lineouts of Characters to prevent burn-in
    private void setTextViewToStroke() {
        Paint mChronometerPaint = mChronometer.getPaint();
        mChronometerPaint.setStyle(Paint.Style.STROKE);
        mChronometerPaint.setStrokeWidth(3);
        Paint mQuestionTimerPaint = mQuestionTimer.getPaint();
        mQuestionTimerPaint.setStyle(Paint.Style.STROKE);
        mQuestionTimerPaint.setStrokeWidth(2);
        Paint mTaskTimerPaint = mTaskTimer.getPaint();
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
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mQuestionActive) {
            mQuestionCountDown.cancel();
        }
        if(mTaskActive) {
            mTaskCountDown.cancel();
        }

    }
}
