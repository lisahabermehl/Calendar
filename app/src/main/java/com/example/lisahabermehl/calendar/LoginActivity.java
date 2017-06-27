package com.example.lisahabermehl.calendar;

/**
 * Everything that has to do with/is need to log a user in.
 */

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.intro);

        AlphaAnimation animation = new AlphaAnimation(0.0f , 1.0f ) ;
        animation.setFillAfter(true);
        animation.setDuration(4000);

        //apply the animation ( fade In ) to your LAyout
        linearLayout.startAnimation(animation);

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                databaseHelper = new DatabaseHelper(LoginActivity.this);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                db.delete(TableNames.CalendarEntry.TABLE_CALENDAR, null, null);

                Intent intent = new Intent(LoginActivity.this, GoogleCalendar.class);
                Bundle extras = new Bundle();
                extras.putString("zero", "get");
                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        }, secondsDelayed * 1000);
    }
}