package com.example.lisahabermehl.calendar;

/**
 * This class makes it possible to show an intro screen which says "MY CALENDAR".
 */

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

public class IntroActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.intro);

        AlphaAnimation animation = new AlphaAnimation(0.0f , 1.0f ) ;
        animation.setFillAfter(true);
        animation.setDuration(4000);

        // apply the animation (fade In) to the layout
        linearLayout.startAnimation(animation);

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                databaseHelper = new DatabaseHelper(IntroActivity.this);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                db.delete(TableNames.CalendarEntry.TABLE_CALENDAR, null, null);

                Intent intent = new Intent(IntroActivity.this, GoogleCalendar.class);
                Bundle extras = new Bundle();
                extras.putString("zero", "get");
                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        }, secondsDelayed * 1000);
    }
}