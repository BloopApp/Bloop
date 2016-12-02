package website.bloop.app;

import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.Observable;

public class BloopActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "BloopActivity";
    private static final long LOCATION_UPDATE_MS = 5000;
    private static final float DEFAULT_ZOOM_LEVEL = 18f;
    private RxLocation mRxLocation;

    private GoogleMap mMap;

    private List<GroundOverlay> mBootprintLocations;
    private int mTotalSteps;
    private BitmapDescriptor mLeftBootprint;
    private BitmapDescriptor mRightBootprint;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloop);

        // we're going to be removing the first element a lot, LinkedList will be good
        mBootprintLocations = new LinkedList<>();

        mLeftBootprint = BitmapDescriptorFactory.fromResource(R.drawable.bootprint_left);
        mRightBootprint = BitmapDescriptorFactory.fromResource(R.drawable.bootprint_right);

        ButterKnife.bind(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRxLocation = new RxLocation(this);

        requestLocationPermissions().subscribe(granted -> {
            if (granted) {
                startTrackingLocation();
            }
        });
    }

    /**
     * Shows an activity that describes the open source libraries used in this project
     */
    private void startAboutLibraries() {
        new LibsBuilder()
                //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                //start the activity
                .start(this);
    }

    private Observable<Boolean> requestLocationPermissions() {
        return RxPermissions.getInstance(this)
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .doOnEach(granted -> {
                    Log.i(TAG, String.format("Location permission granted: %s", granted));
                });
    }

    private void startTrackingLocation() {
        Log.d(TAG, "Location tracking started");

        // Request location now that we know we have permission to do so
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_MS);

        // this should never be called if the permission hasn't been granted.
        //noinspection MissingPermission
        mRxLocation.location().updates(locationRequest)
                //TODO: we might want to clean this data before passing it on
                .doOnEach(location -> mCurrentLocation = location.getValue())
                .doOnEach(location -> updateMapCenter(location.getValue()))
                .doOnEach(location -> updateMyLocation(location.getValue()))
                .subscribe();
    }

    private void updateMyLocation(Location location) {
        if (mBootprintLocations == null) {
            return;
        }

        LatLng latLng = new LatLng(
                location.getLatitude(),
                location.getLongitude()
        );

        if (mBootprintLocations.size() == 0) {
            // first time, place left and right prints
            placeBootprint(latLng, 0);
            placeBootprint(latLng, 0);

            return;
        }

        if (mBootprintLocations.size() >= 1) {
            // we have a bootprint, only place another if we're far enough away from it
            final LatLng lastLatLng = mBootprintLocations.get(
                    mBootprintLocations.size() - 1
            ).getPosition();

            double deltaLat = lastLatLng.latitude - latLng.latitude;
            double deltaLong = lastLatLng.longitude - latLng.longitude;

            double mercatorDistance = Math.sqrt(
                    Math.pow(deltaLat, 2) + Math.pow(deltaLong, 2)
            );

            if (mercatorDistance > 0.0000895f) {
                placeBootprint(latLng, (float) Math.atan2(deltaLong, deltaLat));
            }
        }
    }

    private void placeBootprint(LatLng latLng, float bearing) {
        if (mMap != null) {
            final BitmapDescriptor bootprint;
            // pick left/right print
            if (mTotalSteps++ % 2 == 0) {
                bootprint = mLeftBootprint;
            } else {
                bootprint = mRightBootprint;
            }


            final GroundOverlay overlay = mMap.addGroundOverlay(
                    new GroundOverlayOptions().position(
                            latLng,
                            10
                    ).bearing(
                            (float) (bearing / Math.PI * 180d) + 180f
                    ).image(bootprint)
            );

            mBootprintLocations.add(overlay);
        }
    }

    private void updateMapCenter(Location location) {
        if (mMap != null) {
            LatLng myLocation = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
            );

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM_LEVEL));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
            );

            if (!success) {
                Log.e(TAG, "Set map style failed!");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style file.", e);
        }

        // we don't want this to be fixed.
        mMap.getUiSettings().setAllGesturesEnabled(false);
    }
}
