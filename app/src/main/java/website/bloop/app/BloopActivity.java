package website.bloop.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import website.bloop.app.api.CapturedFlag;
import website.bloop.app.api.NearbyFlag;
import website.bloop.app.api.PlayerLocation;

public class BloopActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "BloopActivity";
    private static final long LOCATION_UPDATE_MS = 5000;
    private static final float DEFAULT_ZOOM_LEVEL = 18f;
    private static final double BOOTPRINT_MIN_MERCATOR_DISTANCE = 0.0000895f;
    private static final float BOOTPRINT_SIZE_METERS = 10;
    private static final int MAX_BOOTPRINTS = 50;

    private static final int REQUEST_LEADERBOARD = 1000;

    private BitmapDescriptor mLeftBootprint;
    private BitmapDescriptor mRightBootprint;
    private GoogleMap mMap;
    private int mTotalSteps;
    private List<GroundOverlay> mBootprintLocations;
    private Location mCurrentLocation;
    private RxLocation mRxLocation;
    private boolean mHasSetInitialCameraPosition;
    private Disposable mLocationDisposable;

    private double mBloopFrequency;
    private Handler mBloopHandler;

    @BindView(R.id.activity_bloop_parent_view)
    RelativeLayout mParentView;

    @BindView(R.id.button_place_flag)
    FloatingActionButton mPlaceFlagButton;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.sonar_view)
    SonarView mSonarView;

    @BindView(R.id.big_button_view)
    BigButtonView mBigButtonView;

    private long mLastBloopTime;
    private Runnable mBloopRunnable;

    private long mNearbyFlagId;
    private String mNearbyFlagOwner;

    private GoogleApiClient mGoogleApiClient;
    private boolean mAreControlsVisible;
    private BloopSoundPlayer mBloopSoundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloop);

        ButterKnife.bind(this);

        mPlaceFlagButton.setOnClickListener(view -> placeFlag());

        mBigButtonView.setOnClickListener(view -> captureFlag());

        // show hide controls
        mAreControlsVisible = true;
        mParentView.setOnClickListener(view -> showHideControls());

        // init bootprints
        //TODO better data structure for this
        mBootprintLocations = new ArrayList<>(MAX_BOOTPRINTS);

        mLeftBootprint = BitmapDescriptorFactory.fromResource(R.drawable.bootprint_left);
        mRightBootprint = BitmapDescriptorFactory.fromResource(R.drawable.bootprint_right);

        // init map
        mHasSetInitialCameraPosition = false;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // init location
        mRxLocation = new RxLocation(this);

        requestLocationPermissions().subscribe(granted -> {
            if (granted) {
                startTrackingLocation();
            }
        });

        // init blooping
        mBloopHandler = new Handler();

        setSupportActionBar(mToolbar);

        mGoogleApiClient = BloopApplication.getInstance().getClient();

        // init sounds
        mBloopSoundPlayer = new BloopSoundPlayer(this);
    }

    private void showHideControls() {
        if (mAreControlsVisible) {
            mToolbar
                    .animate()
                    .y(-mToolbar.getHeight())
                    .setDuration(150)
                    .setInterpolator(PathInterpolatorCompat.create(0.4f, 0.0f, 0.6f, 1f))
                    .start();

            mAreControlsVisible = false;
        } else {
            mToolbar
                    .animate()
                    .y(0)
                    .setDuration(150)
                    .setInterpolator(new LinearOutSlowInInterpolator())
                    .start();

            mAreControlsVisible = true;
        }
    }

    private void captureFlag() {
        if (mNearbyFlagId != 0) {
            BloopApplication application = BloopApplication.getInstance();
            final Call<ResponseBody> call = application.getService().captureFlag(
                    new CapturedFlag(mNearbyFlagId, BloopApplication.getInstance().getPlayerId())
            );

            String requestedFlagOwner = mNearbyFlagOwner;

            Activity self = this;

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() >= 200 && response.code() < 400) {
                        // flag capture success!
                        mBloopSoundPlayer.bloop();

                        final AlertDialog.Builder builder = new AlertDialog.Builder(self);
                        builder
                                .setTitle(String.format(getString(R.string.you_captured_x_flag_format_string), requestedFlagOwner))
                                .setMessage("Add one more to that collection")
                                .setNeutralButton(
                                        getString(R.string.dismiss_capture_flag_dialog_text),
                                        (dialogInterface, i) -> dialogInterface.dismiss())
                                .show();
                    } else {
                        Log.e(TAG, response.message());
                    }
                    mBigButtonView.hide();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mBigButtonView.hide();
                }
            });
        } else {
            showHideControls(); // easier than passing it through for some reason
            //TODO: figure out how to actually pass the click event up the chain.
        }
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
        mLocationDisposable = mRxLocation.location().updates(locationRequest)
                //TODO: we might want to clean this data before passing it on
                .doOnEach(location -> mCurrentLocation = location.getValue())
                .doOnEach(location -> updateMapCenter(location.getValue()))
                .doOnEach(location -> updateMyLocation(location.getValue()))
                .doOnEach(location -> updateBloopFrequency())
                .subscribe();
    }

    private void updateMyLocation(Location location) {
        if (mBootprintLocations == null || location == null) {
            return;
        }

        LatLng latLng = new LatLng(
                location.getLatitude(),
                location.getLongitude()
        );

        if (mBootprintLocations.size() == 0) {
            // first time, place left and right prints
            placeBootprint(latLng, 0f);
            placeBootprint(latLng, 0f);

            return;
        }

        if (mBootprintLocations.size() >= 1) {
            // we have a bootprint, only place another if we're far enough away from it
            final LatLng lastLatLng = mBootprintLocations.get(
                    mBootprintLocations.size() - 1
            ).getPosition();

            double deltaLat = latLng.latitude - lastLatLng.latitude;
            double deltaLong = latLng.longitude - lastLatLng.longitude;

            double mercatorDistance = Math.sqrt(
                    Math.pow(deltaLat, 2) + Math.pow(deltaLong, 2)
            );

            if (mercatorDistance > BOOTPRINT_MIN_MERCATOR_DISTANCE) {
                placeBootprint(latLng, (float) Math.atan2(deltaLong, deltaLat));
            }
        }
    }

    private void placeBootprint(LatLng latLng, float direction) {
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
                            BOOTPRINT_SIZE_METERS
                    ).bearing(
                            (float) (direction / Math.PI * 180d)
                    ).image(bootprint)
            );

            mBootprintLocations.add(overlay);
            removeAndUpdateBootprints();
        }
    }

    private void removeAndUpdateBootprints() {
        if (mBootprintLocations.size() >= MAX_BOOTPRINTS) {
            final GroundOverlay removedOverlay = mBootprintLocations.remove(0);
            removedOverlay.remove();
        }

        for (int i = 0; i < mBootprintLocations.size(); i++) {
            GroundOverlay bootprint = mBootprintLocations.get(i);

            int footprintsLeft = MAX_BOOTPRINTS - mBootprintLocations.size();

            float transparency = 1f - ((i + footprintsLeft) / (float) MAX_BOOTPRINTS);

            bootprint.setTransparency((2f * transparency / 3f) + .33f);
        }
    }

    private void updateMapCenter(Location location) {
        if (mMap != null && location != null) {
            LatLng myLocation = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
            );

            if (mHasSetInitialCameraPosition) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM_LEVEL));
            } else {
                // jump right to the first position.
                mHasSetInitialCameraPosition = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM_LEVEL));

                // TODO: reveal map after this?
            }

        }
    }

    private void placeFlag() {
        if (mCurrentLocation != null) {
            // TODO organize bloop and placing flag better

            final Intent placeFlagIntent = new Intent(this, FlagCreationActivity.class);
            placeFlagIntent.putExtra(FlagCreationActivity.FLAG_LOCATION, mCurrentLocation);
            startActivity(placeFlagIntent);
        }
    }

    private void updateBloopFrequency() {
        BloopApplication application = BloopApplication.getInstance();
        if (mCurrentLocation != null) {
            Call<NearbyFlag> call = application.getService().getNearestFlag(
                            new PlayerLocation(application.getPlayerId(), mCurrentLocation)
            );

            call.enqueue(new Callback<NearbyFlag>() {
                @Override
                public void onResponse(Call<NearbyFlag> call, Response<NearbyFlag> response) {
                    if (response.isSuccessful()) {
                        mBloopFrequency = response.body().getBloopFrequency();

                        String playerName = response.body().getPlayerName();
                        if (playerName != null) {
                            // if player name is present, that means that there is a flag a
                            // capturable distance away
                            // TODO: alert the user that they can capture this flag
                            mNearbyFlagId = response.body().getFlagId();
                            mNearbyFlagOwner = response.body().getPlayerName();

                            mBigButtonView.show();
                        } else {
                            mNearbyFlagId = 0; // this is the "null" value of the flag id
                            mNearbyFlagOwner = null;
                            mBigButtonView.hide();
                        }

                        rescheduleBloops();
                    }
                }

                @Override
                public void onFailure(Call<NearbyFlag> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }
    }

    private void rescheduleBloops() {
        double timeSinceLastBloop = (double) (System.currentTimeMillis() - mLastBloopTime);

        if (mBloopFrequency == 0d) {
            // nvm, we done here.
            // cancel the next bloop by removing our reference to it
            mBloopRunnable = null;
            return;
        }

        double newBloopInterval = (1000d / mBloopFrequency); // in millis

        double timeUntilNextBloop = newBloopInterval - timeSinceLastBloop;

        mBloopRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // if this runnable isn't stored on the object, that means that it's been
                    // "canceled", don't bloop
                    if (this.equals(mBloopRunnable)) {
                        bloop();
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    if (this.equals(mBloopRunnable)) {
                        mBloopHandler.postDelayed(mBloopRunnable, (long) newBloopInterval);
                    }
                }
            }
        };

        if (timeUntilNextBloop > 0) {
            // schedule next bloop at difference from last
            mBloopHandler.postDelayed(mBloopRunnable, (long) timeUntilNextBloop);
        } else {
            // immediately bloop if <= 0
            mBloopHandler.post(mBloopRunnable);
        }
    }

    private void bloop() {
        mSonarView.bloop();
        mBloopSoundPlayer.boop();

        mLastBloopTime = System.currentTimeMillis();
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
        mMap.setOnMapClickListener(latLng -> showHideControls());
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bloop_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_leaderboard:
                startActivityForResult(
                        Games.Leaderboards.getLeaderboardIntent(
                                mGoogleApiClient,
                                getString(R.string.leaderboard_bloop_high_scores)
                        ),
                        REQUEST_LEADERBOARD
                );
                return true;
            case R.id.item_about_libs:
                startAboutLibraries();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationDisposable != null && !mLocationDisposable.isDisposed()) {
            mLocationDisposable.dispose();
        }

        mBloopFrequency = 0;
        rescheduleBloops();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mLocationDisposable == null || mLocationDisposable.isDisposed()) {
            startTrackingLocation();
        }
    }
}
