package com.doggiesmile.lecturewatchalarm4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAGAIO = "AllInOneActivity";
    private static final String TAGFT = "FocusTimeActivity";
    private static final String TAGFTR = "FocusChronoActivity";

    private TextView mAllInOneText;
    private TextView mFocusTimeText;
    private TextView mFocusTimerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseTextViews();
        setOnClickListenerToTextViews();

    }

    public void initialiseTextViews() {
        mAllInOneText = (TextView) findViewById(R.id.allInOneText);
        mFocusTimeText = (TextView) findViewById(R.id.focusTimeText);
        mFocusTimerText = (TextView) findViewById(R.id.focusTimerText);
    }

    public void setOnClickListenerToTextViews() {
        mAllInOneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAGAIO, "TextClicked");
                changeToAllInOne();
            }
        });
        mFocusTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAGFT, "TextClicked");
                changeToFocusTime();
            }
        });
        mFocusTimerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAGFTR, "TextClicked");
                changeToFocusTimer();
            }
        });
    }

    public void changeToAllInOne() {
        Intent intent = new Intent(MainActivity.this, AllInOneActivity.class);
        startActivity(intent);
    }

    public void changeToFocusTime() {
        Intent intent = new Intent(MainActivity.this, FocusTimeActivity.class);
        startActivity(intent);
    }

    public void changeToFocusTimer() {
        Intent intent = new Intent(MainActivity.this, FocusChronoActivity.class);
        startActivity(intent);
    }

    protected void onPause() {
        super.onPause();
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
