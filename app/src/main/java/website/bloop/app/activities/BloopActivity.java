package website.bloop.app.activities;

import android.app.Activity;
import android.content.Intent;
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
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import website.bloop.app.BloopApplication;
import website.bloop.app.R;
import website.bloop.app.api.BloopAPIService;
import website.bloop.app.api.NearbyFlag;
import website.bloop.app.api.PlayerLocation;
import website.bloop.app.fragments.BootprintMapFragment;
import website.bloop.app.sound.BloopSoundPlayer;
import website.bloop.app.views.BigButtonView;
import website.bloop.app.views.SonarView;

public class BloopActivity extends AppCompatActivity {
    private static final String TAG = "BloopActivity";
    private static final long LOCATION_UPDATE_MS = 5000;
    private static final int REQUEST_LEADERBOARD = 1000;

    private Location mCurrentLocation;
    private RxLocation mRxLocation;

    private Disposable mLocationDisposable;

    private double mBloopFrequency;
    private Handler mBloopHandler;

    @BindView(R.id.activity_bloop_parent_view)
    RelativeLayout mParentView;

    BootprintMapFragment mBootprintMapFragment;

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
    private BloopAPIService mService;
    private BloopApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloop);

        mBootprintMapFragment = new BootprintMapFragment();
        mBootprintMapFragment.setArguments(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mBootprintMapFragment)
                .commit();

        ButterKnife.bind(this);

        mPlaceFlagButton.setOnClickListener(view -> placeFlag());

        mBigButtonView.setOnClickListener(view -> captureFlag());

        // show hide controls
        mAreControlsVisible = true;
        mParentView.setOnClickListener(view -> showHideControls());

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

        // init sounds
        mBloopSoundPlayer = new BloopSoundPlayer(this);

        // init global references / api stuff
        mApplication = BloopApplication.getInstance();

        mGoogleApiClient = mApplication.getGoogleApiClient();

        mService = mApplication.getService();
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

            String requestedFlagOwner = mNearbyFlagOwner;

            Activity self = this;

            mService.captureFlag(
                    new NearbyFlag(mNearbyFlagId, BloopApplication.getInstance().getPlayerId()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        // flag capture success!
                        mBloopSoundPlayer.bloop();

                        final AlertDialog.Builder builder = new AlertDialog.Builder(self);
                        builder.setTitle(String.format(getString(R.string.you_captured_x_flag_format_string), requestedFlagOwner))
                                .setMessage("Add one more to that collection")
                                .setNeutralButton(
                                        getString(R.string.dismiss_capture_flag_dialog_text),
                                        (dialogInterface, i) -> dialogInterface.dismiss())
                                .show();
                        mBigButtonView.hide();
                    }, throwable -> {
                        mBigButtonView.hide();
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
                .doOnEach(location -> mBootprintMapFragment.updateMapCenter(location.getValue()))
                .doOnEach(location -> mBootprintMapFragment.updatePlayerLocation(location.getValue()))
                .doOnEach(location -> updateBloopFrequency())
                .subscribe();
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
        if (mCurrentLocation != null) {
            mService.getNearestFlag(
                    new PlayerLocation(mApplication.getPlayerId(), mCurrentLocation))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(nearbyFlag -> {
                        mBloopFrequency = nearbyFlag.getBloopFrequency();

                        String playerName = nearbyFlag.getPlayerName();
                        if (playerName != null) {
                            // if player name is present, that means that there is a flag a
                            // capturable distance away
                            // TODO: alert the user that they can capture this flag
                            mNearbyFlagId = nearbyFlag.getFlagId();
                            mNearbyFlagOwner = nearbyFlag.getPlayerName();

                            mBigButtonView.show();
                        } else {
                            mNearbyFlagId = 0; // this is the "null" value of the flag id
                            mNearbyFlagOwner = null;
                            mBigButtonView.hide();
                        }

                        rescheduleBloops();
                    }, throwable -> Log.e(TAG, throwable.getMessage()));
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
