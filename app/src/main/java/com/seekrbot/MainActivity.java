package com.seekrbot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {
    private static final String FLURRY_APIKEY = "JQVT87W7TGN5W7SWY2FH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MyService.class));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);

                    gotoHomeActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Init Flurry
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogLevel(Log.INFO);

        FlurryAgent.setVersionName("1.0");
        FlurryAgent.init(this, FLURRY_APIKEY);


    }

    public void gotoHomeActivity()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
