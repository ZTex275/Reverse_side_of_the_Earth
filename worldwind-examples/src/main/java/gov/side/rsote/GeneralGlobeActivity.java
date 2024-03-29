package gov.side.rsote;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import gov.side.worldwind.NavigatorEvent;
import gov.side.worldwind.NavigatorListener;
import gov.side.worldwind.WorldWind;
import gov.side.worldwind.WorldWindow;
import gov.side.worldwind.geom.Camera;
import gov.side.worldwind.geom.LookAt;

/**
 * Creates a general purpose globe view with touch navigation, a few layers, and a coordinates overlay.
 */
public class GeneralGlobeActivity extends BasicGlobeActivity {

    // UI elements
    protected TextView latView;
    protected TextView lonView;
    protected TextView altView;
    protected ImageView crosshairs;
    protected ViewGroup overlay;
    protected EditText latlon;

    // Use pre-allocated navigator state objects to avoid per-event memory allocations
    private LookAt lookAt = new LookAt();
    private Camera camera = new Camera();
    // Track the navigation event time so the overlay refresh rate can be throttled
    private long lastEventTime;
    // Animation object used to fade the overlays
    private AnimatorSet animatorSet;
    private boolean crosshairsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Убираем навигационную панель снизу
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        // Initialize the UI elements that we'll update upon the navigation events
        this.crosshairs = (ImageView) findViewById(R.id.globe_crosshairs);
        this.overlay = (ViewGroup) findViewById(R.id.globe_status);
        //this.latlon = (EditText) findViewById(R.id.input_latlon);

        this.crosshairs.setVisibility(View.VISIBLE);
        this.overlay.setVisibility(View.VISIBLE);
        this.latView = (TextView) findViewById(R.id.lat_value);
        this.lonView = (TextView) findViewById(R.id.lon_value);
        this.altView = (TextView) findViewById(R.id.alt_value);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this.crosshairs, "alpha", 0f).setDuration(1500);
        fadeOut.setStartDelay((long) 500);
        this.animatorSet = new AnimatorSet();
        this.animatorSet.play(fadeOut);


        // Create a simple Navigator Listener that logs navigator events emitted by the WorldWindow.
        NavigatorListener listener = new NavigatorListener() {
            @Override
            public void onNavigatorEvent(WorldWindow wwd, NavigatorEvent event) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - lastEventTime;
                int eventAction = event.getAction();
                boolean receivedUserInput = (eventAction == WorldWind.NAVIGATOR_MOVED && event.getLastInputEvent() != null);

                // Update the status overlay views whenever the navigator stops moving,
                // and also it is moving but at an (arbitrary) maximum refresh rate of 20 Hz.
                if (eventAction == WorldWind.NAVIGATOR_STOPPED || elapsedTime > 50) {

                    // Get the current navigator state to apply to the overlays
                    event.getNavigator().getAsLookAt(wwd.getGlobe(), lookAt);
                    event.getNavigator().getAsCamera(wwd.getGlobe(), camera);

                    // Update the overlays
                    updateOverlayContents(lookAt, camera);
                    updateOverlayColor(eventAction);

                    lastEventTime = currentTime;
                }

                // Show the crosshairs while the user is gesturing and fade them out after the user stops
                if (receivedUserInput) {
                    showCrosshairs();
                } else {
                    fadeCrosshairs();
                }
            }
        };

        // Register the Navigator Listener with the activity's WorldWindow.
        this.getWorldWindow().addNavigatorListener(listener);
    }

    /**
     * Makes the crosshairs visible.
     */
    protected void showCrosshairs() {
        if (this.animatorSet.isStarted()) {
            this.animatorSet.cancel();
        }
        this.crosshairs.setAlpha(1.0f);
        this.crosshairsActive = true;
    }

    /**
     * Fades the crosshairs using animation.
     */
    protected void fadeCrosshairs() {
        if (this.crosshairsActive) {
            this.crosshairsActive = false;
            if (!this.animatorSet.isStarted()) {
                this.animatorSet.start();
            }
        }
    }

    /**
     * Displays navigator state information in the status overlay views.
     *
     * @param lookAt Where the navigator is looking
     * @param camera Where the camera is positioned
     */
    protected void updateOverlayContents(LookAt lookAt, Camera camera) {
        latView.setText(formatLatitude(lookAt.latitude));
        lonView.setText(formatLongitude(lookAt.longitude));
        altView.setText(formatAltitude(camera.altitude));
    }

    /**
     * Brightens the colors of the overlay views when when user input occurs.
     *
     * @param eventAction The action associated with this navigator event
     */
    protected void updateOverlayColor(@WorldWind.NavigatorAction int eventAction) {
        int color = (eventAction == WorldWind.NAVIGATOR_STOPPED) ? 0xA0FFFF00 /*semi-transparent yellow*/ : Color.YELLOW;
        latView.setTextColor(color);
        lonView.setTextColor(color);
        altView.setTextColor(color);
    }

    protected String formatLatitude(double latitude) {
        int sign = (int) Math.signum(latitude);
        return String.format("%6.3f°%s", (latitude * sign), (sign >= 0.0 ? "N" : "S"));
    }

    protected String formatLongitude(double longitude) {
        int sign = (int) Math.signum(longitude);
        return String.format("%7.3f°%s", (longitude * sign), (sign >= 0.0 ? "E" : "W"));
    }

    protected String formatAltitude(double altitude) {
        return String.format("Eye: %,.0f %s",
            (altitude < 100000 ? altitude : altitude / 1000),
            (altitude < 100000 ? "m" : "km"));
    }


    //Убираем навигационную панель снизу
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }


}
