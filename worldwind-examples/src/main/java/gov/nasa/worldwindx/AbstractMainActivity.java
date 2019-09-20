package gov.nasa.worldwindx;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

import gov.nasa.worldwind.FrameMetrics;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Camera;

import static java.lang.Math.abs;

public abstract class AbstractMainActivity extends AppCompatActivity {

    protected final static String SESSION_TIMESTAMP = "session_timestamp";
    protected final static String CAMERA_LATITUDE = "latitude";
    protected final static String CAMERA_LONGITUDE = "longitude";
    protected final static String CAMERA_ALTITUDE = "altitude";
    protected final static String CAMERA_ALTITUDE_MODE = "altitude_mode";
   // protected final static String CAMERA_HEADING = "heading";
   // protected final static String CAMERA_TILT = "tilt";
   // protected final static String CAMERA_ROLL = "roll";
    protected static final int PRINT_METRICS = 1;
    protected static final int PRINT_METRICS_DELAY = 3000;
    protected static final Date sessionTimestamp = new Date();
    public AdView mAdView;
    // protected static int selectedItemId = R.id.nav_general_globe_activity;

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == PRINT_METRICS) {
                return printMetrics();
            } else {
                return false;
            }
        }
    });

    /**
     * Returns a reference to the WorldWindow.
     * <p/>
     * Derived classes must implement this method.
     *
     * @return The WorldWindow GLSurfaceView object
     */
    abstract public WorldWindow getWorldWindow();

    /**
     * This method should be called by derived classes in their onCreate method.
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        this.onCreate();
    }

    protected void onCreate() {
        //прокаженная реклама
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //клик на плавающую кнопку
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNavigatorState();

                WorldWindow wwd = getWorldWindow();
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);

                // Read the camera data
                float lat = preferences.getFloat(CAMERA_LATITUDE, Float.MAX_VALUE);
                float lon = preferences.getFloat(CAMERA_LONGITUDE, Float.MAX_VALUE);
                float alt = preferences.getFloat(CAMERA_ALTITUDE, Float.MAX_VALUE);
               /* float heading = 0;
                  float tilt = 0;
                  float roll = 0;*/
                @WorldWind.AltitudeMode int altMode = preferences.getInt(CAMERA_ALTITUDE_MODE, WorldWind.ABSOLUTE);

                if (lat < 0) {
                    lat += abs(lat) * 2;
                } else {
                    lat -= lat * 2;
                }
                if (lon < 0) {
                    lon += 180;
                } else {
                    lon -= 180;
                }

                // Restore the camera state.
                Camera camera = new Camera(lat, lon, alt, altMode, 0, 0, 0);
                wwd.getNavigator().setAsCamera(wwd.getGlobe(), camera);

                Toast.makeText(getApplicationContext(), "Reverse side of the Earth!", Toast.LENGTH_LONG).show();


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the menu by highlighting the last selected menu item
        //   this.navigationView.setCheckedItem(selectedItemId);

        // Use this Activity's Handler to periodically print the FrameMetrics.
        //this.handler.sendEmptyMessageDelayed(PRINT_METRICS, PRINT_METRICS_DELAY);
        // Restore the navigator's camera state from previously saved session data
        this.restoreNavigatorState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop printing frame metrics when this activity is paused.
        //this.handler.removeMessages(PRINT_METRICS);
        // Save the navigator's camera state.
        this.saveNavigatorState();
    }


    /**
     * Saves the Navigator's camera data to a SharedPreferences object.
     */
    protected void saveNavigatorState() {
        WorldWindow wwd = this.getWorldWindow();
        if (wwd != null) {
            SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            // Write an identifier to the preferences for this session;
            editor.putLong(SESSION_TIMESTAMP, getSessionTimestamp());

            // Write the camera data
            Camera camera = wwd.getNavigator().getAsCamera(wwd.getGlobe(), new Camera());
            editor.putFloat(CAMERA_LATITUDE, (float) camera.latitude);
            editor.putFloat(CAMERA_LONGITUDE, (float) camera.longitude);
            editor.putFloat(CAMERA_ALTITUDE, (float) camera.altitude);
           // editor.putFloat(CAMERA_HEADING, (float) camera.heading);
            //editor.putFloat(CAMERA_TILT, (float) camera.tilt);
           // editor.putFloat(CAMERA_ROLL, (float) camera.roll);
            editor.putInt(CAMERA_ALTITUDE_MODE, camera.altitudeMode);

            editor.apply();
        }
    }

    /**
     * Restores the Navigator's camera state from a SharedPreferences object.
     */
    protected void restoreNavigatorState() {
        WorldWindow wwd = this.getWorldWindow();
        if (wwd != null) {
            SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);

            // We only want to restore preferences from the same session.
            if (preferences.getLong(SESSION_TIMESTAMP, -1) != getSessionTimestamp()) {
                return;
            }
            // Read the camera data
            float lat = preferences.getFloat(CAMERA_LATITUDE, Float.MAX_VALUE);
            float lon = preferences.getFloat(CAMERA_LONGITUDE, Float.MAX_VALUE);
            float alt = preferences.getFloat(CAMERA_ALTITUDE, Float.MAX_VALUE);
           // float heading = preferences.getFloat(CAMERA_HEADING, Float.MAX_VALUE);
           // float tilt = preferences.getFloat(CAMERA_TILT, Float.MAX_VALUE);
            //float roll = preferences.getFloat(CAMERA_ROLL, Float.MAX_VALUE);
            @WorldWind.AltitudeMode int altMode = preferences.getInt(CAMERA_ALTITUDE_MODE, WorldWind.ABSOLUTE);

            /*if (lat == Float.MAX_VALUE || lon == Float.MAX_VALUE || alt == Float.MAX_VALUE ||
                    heading == Float.MAX_VALUE || tilt == Float.MAX_VALUE || roll == Float.MAX_VALUE) {
                return;
            }*/

            // Restore the camera state.
            Camera camera = new Camera(lat, lon, alt, altMode, 0, 0, 0);
            wwd.getNavigator().setAsCamera(wwd.getGlobe(), camera);
        }
    }

    protected boolean printMetrics() {
        // Assemble the current system memory info.
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        // Assemble the current WorldWind frame metrics.
        FrameMetrics fm = this.getWorldWindow().getFrameMetrics();

        // Print a log message with the system memory, WorldWind cache usage, and WorldWind average frame time.
       /* Logger.log(Logger.INFO, String.format(Locale.US, "System memory %,.0f KB    Heap memory %,.0f KB    Render cache %,.0f KB    Frame time %.1f ms + %.1f ms",
            (mi.totalMem - mi.availMem) / 1024.0,
            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0,
            fm.getRenderResourceCacheUsedCapacity() / 1024.0,
            fm.getRenderTimeAverage(),
            fm.getDrawTimeAverage()));*/

        // Reset the accumulated WorldWind frame metrics.
        fm.reset();
        // Print the frame metrics again after the configured delay.
        return this.handler.sendEmptyMessageDelayed(PRINT_METRICS, PRINT_METRICS_DELAY);
    }


    protected static long getSessionTimestamp() {
        return sessionTimestamp.getTime();
    }

}
