package com.seekrbot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by silver on 11/11/16.
 */
public class HomeActivity extends FragmentActivity implements GpsStatus.Listener, BackgroundLocationService.ConnectionCallbacks, BackgroundLocationService.LocationCallbacks{
    private static final String TAG = HomeActivity.class.getName();

    private static final int NUM_PAGES = 3;

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;
    public ImageView[] dots = new ImageView[3];

    public ArrayList<CircleImageView> arrayDotView = new ArrayList<CircleImageView>();
    public ScreenSlidePageFragment m_fragment1;
    public ScreenSlidePageFragment m_fragment2;
    public ScreenSlidePageFragment m_fragment3;


    public int pageNumber;
    int screenWidth;
    int screenHeight;


    // Google play services stuff..
    private boolean mIsInResolution;
    private boolean mShouldRetryConnecting;

    // Request code for auto Google Play Services error resolution.
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    // Use the location manger to track if location is enabled or not.
    private LocationManager mLocationManager;
    private boolean mLocationEnabled;


    private boolean mLocationRunning;
    private boolean bBackgroundMode;

    private BackgroundLocationService mBackgroundLocationService;
    private Location myLocation;
    private Location oldLocation;

    private ServiceConnection mLocationManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Background location services connected");
            mBackgroundLocationService = ((BackgroundLocationService.LocalBinder)service).getBackgroundLocationService();
            mBackgroundLocationService.addConnectionCallbacks(HomeActivity.this);
            mBackgroundLocationService.addLocationCallbacks(HomeActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBackgroundLocationService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/Existence-Light.ttf");
        TextView title = (TextView)this.findViewById(R.id.tv_title);
        title.setTypeface(font);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < NUM_PAGES; i++)
                {
                    CircleImageView dot = (CircleImageView)arrayDotView.get(i);
                    dot.setImageBitmap(circleBitmap(Color.rgb(111, 113, 121)));
                    if (i == position)
                        dot.setImageBitmap(circleBitmap(Color.rgb(255, 255, 255)));
                }

//                mPagerAdapter.notifyDataSetChanged();
                if (position == 1)
                    m_fragment2.setSacredName();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mPager.setOnTouchListener(new OnSwipeTouchListener());

//        DisplayMetrics mec = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(mec);


//        Display display = getWindowManager().getDefaultDisplay();
//        final Point size = new Point();
//        display.getSize(size);
//        screenWidth = size.x;
//        screenHeight = size.y;

        DisplayMetrics mec = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mec);
        screenWidth = mec.widthPixels;
        screenHeight = mec.heightPixels;



        RelativeLayout dot_layout = (RelativeLayout) findViewById(R.id.dot_layout);
        for (int i = 0; i < NUM_PAGES; i++)
        {
            CircleImageView dot = new CircleImageView(this);
            dot.setBorderWidth(0);
            dot.setImageBitmap(circleBitmap(Color.rgb(111, 113, 121)));
            dot_layout.addView(dot);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

//            params.setMargins(0, 0, 20, 0);
// OR
            params.topMargin= 20;

            dot.setLayoutParams(params);

//            dot.setY(20);
            int x = mec.widthPixels / (NUM_PAGES +1 ) * (i + 1);

                if (x < mec.widthPixels/2)
                {
                    params.leftMargin = (int)(mec.widthPixels/2 - ((NUM_PAGES/2-i)*30)*screenWidth/320.0f);
//                    dot.setX(mec.widthPixels/2 - ((NUM_PAGES/2-i)*30)*screenWidth/320.0f);
                }
                if (x > mec.widthPixels/2)
                {
                    params.leftMargin = (int)(mec.widthPixels/2 + (i-NUM_PAGES/2)*30*screenWidth/320.0f);

//                    dot.setX(mec.widthPixels/2 + (i-NUM_PAGES/2)*30*screenWidth/320.0f);
                }
                if (x == mec.widthPixels/2)
                    params.leftMargin = x;
//                    dot.setX(x);

                dot.setLayoutParams(params);


            if (i == 0)
                dot.setImageBitmap(circleBitmap(Color.rgb(255, 255, 255)));
            arrayDotView.add(dot);

        }

        bNewLocation = true;

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        checkLocationEnabled();

        if (!mLocationEnabled) {
            mLocationRunning = false;
            Toast.makeText(this, "Location is disabled :(", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }


    }

    public Bitmap circleBitmap(int color)
    {

        Bitmap circleBitmap = Bitmap.createBitmap((int) (10 * screenWidth / 320.0f), (int) (10 * screenWidth / 320.0f), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle((int)(10*screenWidth/320.0f) / 2, (int)(10*screenWidth/320.0f) / 2, (int)(10*screenWidth/320.0f) / 2, paint);

        return circleBitmap;
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (m_fragment1 == null)
                    m_fragment1 = new ScreenSlidePageFragment().create(position);
                return m_fragment1;
            }
            else if (position == 1) {
                if (m_fragment2 == null)
                    m_fragment2 = new ScreenSlidePageFragment().create(position);
                return m_fragment2;
            }
            else if (position == 2) {
                if (m_fragment3 == null)
                    m_fragment3 = new ScreenSlidePageFragment().create(position);
                return m_fragment3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
//        checkLocationEnabled();
//
//        if (!mLocationEnabled){
//            mLocationRunning = false;
//            Toast.makeText(this, "Location is disabled :(", Toast.LENGTH_SHORT).show();
////            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//        }
//        FlurryAgent.onStartSession(this, "Y8XCM94B45XGV4FFSCJB");


    }

    public void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
        // your code
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationEnabled();

        if( mLocationEnabled ){
            if (!mLocationRunning){
                bindService(new Intent(this, BackgroundLocationService.class), mLocationManagerConnection, Context.BIND_AUTO_CREATE);
                mLocationRunning = true;
                timerHandler.postDelayed(timerRunnable, 10000);
            }
        } else {
            mLocationRunning = false;
//            Toast.makeText(this, "Location is disabled :(", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }
        bBackgroundMode = false;
        ShortcutBadger.removeCount(this);
        SharedPreferences.Editor editor = getSharedPreferences("Seekr", MODE_PRIVATE).edit();
        editor.putInt("badge_count", 0);
        editor.commit();

        FlurryAgent.logEvent("Seekr is running");
    }

    private void checkLocationEnabled() {
        mLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(this, "New location! " + location.toString(), Toast.LENGTH_SHORT).show();
        myLocation = location;
    }

    @Override
    protected void onPause() {
        // When you're done with locations, be sure you remember to remove the service!
//        if (mBackgroundLocationService != null) {
//            mBackgroundLocationService.removeLocationUpdates();
//            mBackgroundLocationService.removeConnectionCallbacks(this);
//            mBackgroundLocationService.removeLocationCallbacks(this);
//            unbindService(mLocationManagerConnection);
//            mBackgroundLocationService = null;
//        }

        bBackgroundMode = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.removeGpsStatusListener(this);
        if (mBackgroundLocationService != null) {
            mBackgroundLocationService.removeLocationUpdates();
            mBackgroundLocationService.removeConnectionCallbacks(this);
            mBackgroundLocationService.removeLocationCallbacks(this);
            unbindService(mLocationManagerConnection);
            mBackgroundLocationService = null;
        }
    }

    @Override
    public void onBackPressed()
    {
//        super.onBackPressed();
    }

    @Override
    public final void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection to Google Play Services suspended!");
    }

    @Override
    public void onLocationServicesConnectionSuccessful() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(5000); // Five seconds

        mBackgroundLocationService.requestUpdates(request);
    }

    @Override
    public void onLocationServicesConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            mShouldRetryConnecting = true;
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mShouldRetryConnecting) {
            retryConnecting();
        } else {
            switch (requestCode) {
                case REQUEST_CODE_RESOLUTION:
                    retryConnecting();
                    break;
            }
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        mShouldRetryConnecting = false;
        if (mBackgroundLocationService != null) {
            mBackgroundLocationService.onConnectionResolved();
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                Log.d(TAG, "GPS has started.");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d(TAG, "GPS has stopped.");
                checkLocationEnabled();
                if (mBackgroundLocationService.getGoogleApiClient().isConnected() && !mLocationEnabled) {
                    Log.d(TAG, "Disconnecting location client");
                    Toast.makeText(this, "Location disabled.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            startTimer();
            timerHandler.postDelayed(this, 10000);
        }
    };

    int timeCount = 0;
    int interval = 30; // 30;
    boolean bNewLocation;

    public void startTimer()
    {
        timeCount++;
        if (myLocation == null)
            return;

        if (oldLocation == null) {
            oldLocation = myLocation;
        }
        if (isSameLocation(oldLocation, myLocation)) {
            if (timeCount % interval != 0) {
                return;
            }
            timeCount = 0;
            interval = 360; // 360 = 1 hours

            String message = null;


            if (bNewLocation == false) {
                if (Global.sacredName.equalsIgnoreCase("God") || Global.sacredName.equalsIgnoreCase("Allah")) {
                    message = String.format("You're still here. %s's %s is still here.", Global.sacredName, Global.attributeName);
                }
                else if(Global.sacredName.equalsIgnoreCase("Jesus"))
                    message = String.format("You're still here. %s' %s is still here.", Global.sacredName, Global.attributeName);
                else if(Global.sacredName.equalsIgnoreCase("Self"))
                {
                    message = String.format("You're still here. The %s residing within you is too.", Global.attributeName);
                }
                else if (Global.sacredName.equalsIgnoreCase("Universe") || Global.sacredName.equalsIgnoreCase("Ancestors")) {
                    message = String.format("You're still here. the %s of the %s is still here.",  Global.attributeName, Global.sacredName);
                }
            }
            else
            {
                if (Global.sacredName.equalsIgnoreCase("God") || Global.sacredName.equalsIgnoreCase("Allah")) {
                    message = String.format("You're in a new space. Take 5 deep breaths and know that %s's %s is with you.", Global.sacredName, Global.attributeName);
                }
                else if(Global.sacredName.equalsIgnoreCase("Jesus"))
                    message = String.format("You're in a new space. Take 5 deep breaths and know that %s' %s is with you.", Global.sacredName, Global.attributeName);

                else if(Global.sacredName.equalsIgnoreCase("Self"))
                {
                    message = String.format("You're in a new space. Take 5 deep breaths and know that %s already residing within you is here", Global.attributeName);
                    message = String.format("You're in a new space. Take 5 deep breaths and know that %s already residing within you is here", Global.attributeName);

                }
                else if (Global.sacredName.equalsIgnoreCase("Universe") || Global.sacredName.equalsIgnoreCase("Ancestors")) {
                    message = String.format("You're in a new space. Take 5 deep breaths and know that the %s of the %s is with you.", Global.attributeName, Global.sacredName);
                }

            }



            bNewLocation = false;

            final String finalMessage = message;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAlertAndNotification(finalMessage);

                }
            });
        }
        else{
            interval = 30;
            timeCount = 0;
            bNewLocation = true;
        }

        oldLocation = myLocation;

//        timerHandler.postDelayed(timerRunnable, 10000);
    }

    public void showAlertAndNotification(String message)
    {
        if (bBackgroundMode == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Seekrbot");
            builder.setMessage(message);

            //Here pass the listener object.
            builder.setPositiveButton("OK", null);
            builder.show();


        }
        else{
            SharedPreferences prefs = getSharedPreferences("Seekr", MODE_PRIVATE);
            int badges = prefs.getInt("badge_count", 0);
            badges++;

            SharedPreferences.Editor editor = getSharedPreferences("Seekr", MODE_PRIVATE).edit();
            editor.putInt("badge_count", badges);
            editor.commit();


            ShortcutBadger.applyCount(HomeActivity.this, badges);

            String GROUP_KEY_EMAILS = "group_key_emails";
            int NOTIFICATION_ID = 1;

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT );

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setColor(0x3c57a7)
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle("Seekrbot")
                    .setContentText(message)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY_EMAILS)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());


        }
    }

    private boolean isSameLocation(Location l1, Location l2)
    {
        double d = distance(l1.getLatitude(), l1.getLongitude(), l2.getLatitude(), l2.getLongitude());
        int delta = (int) (d / 1600.0f);
        int threshold = 100;  // threshold distance in meters

        // note: userLocation and otherLocation are CLLocation objects
        if (delta <= threshold) {
            // same location
            return true;
        }
        return false;

    }
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }
}
