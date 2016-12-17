package website.bloop.app.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import website.bloop.app.api.CapturedFlag;
import website.bloop.app.api.NearbyFlag;
import website.bloop.app.api.PlayerLocation;
import website.bloop.app.dialogs.FlagCapturedDialogFragment;
import website.bloop.app.fragments.BootprintMapFragment;
import website.bloop.app.sound.BloopSoundPlayer;
import website.bloop.app.views.BigButtonView;
import website.bloop.app.views.FlagView;
import website.bloop.app.views.SonarView;

/**
 * Main game activity, which shows bloop animations and sounds, as well as controls
 * game interaction such as capturing bloops or checking the leaderboards.
 */
public class BloopActivity extends AppCompatActivity {
    public static final String ARG_OPPONENT = "ArgOpponentName";

    private static final String PREF_SOUND = "MutePREF";
    private static final String PREF_SOUND_VAL = "muted";
    private static final String TAG = "BloopActivity";
    private static final long LOCATION_UPDATE_MS = 5000;
    private static final int REQUEST_LEADERBOARD = 1000;
    private static final String PREF_ACHIEVEMENT_TRACKER = "AchievementTrackerPREF";
    private static final int PLACE_FLAG_ACTIVITY_RESULT = 123;

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
    private float mPlaceFlagButtonMarginBottom; // for animation

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.sonar_view)
    SonarView mSonarView;

    @BindView(R.id.big_button_view)
    BigButtonView mBigButtonView;

    private long mLastBloopTime;
    private Runnable mBloopRunnable;

    private NearbyFlag mNearbyFlag;

    private GoogleApiClient mGoogleApiClient;
    private BloopAPIService mService;
    private BloopApplication mApplication;

    private BloopSoundPlayer mBloopSoundPlayer;
    private SharedPreferences mutePref;
    private boolean mute;
    private boolean mFlagButtonIsShown;
    private boolean mHasPlacedFlag;

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

        // init global references / api stuff
        mApplication = BloopApplication.getInstance();

        mGoogleApiClient = mApplication.getGoogleApiClient();

        // makes achievement dialogs visible
        Games.setViewForPopups(
                mGoogleApiClient,
                getWindow().getDecorView().findViewById(android.R.id.content)
        );

        mService = mApplication.getService();

        // hide flag by default
        mPlaceFlagButtonMarginBottom = getResources().getDimension(R.dimen.fab_margin);
        // TODO: this doesn't actually animate the fab far enough
        mPlaceFlagButton.setVisibility(View.INVISIBLE);

        checkHasPlacedFlag();

        mPlaceFlagButton.setOnClickListener(view -> placeFlag());

        mBigButtonView.setOnClickListener(view -> captureFlag());

        // init location
        mRxLocation = new RxLocation(this);

        startTrackingLocation();

        // init blooping
        mBloopHandler = new Handler();

        setSupportActionBar(mToolbar);

        // init sounds
        mBloopSoundPlayer = new BloopSoundPlayer(this);

        // mute logic
        mutePref = getSharedPreferences(PREF_SOUND, Context.MODE_PRIVATE);
        mute = mutePref.getBoolean(PREF_SOUND_VAL, false);
    }

    private void checkHasPlacedFlag() {
        mService.checkHasPlacedFlag(mApplication.getPlayerId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ownFlag -> {
                    if (!ownFlag.getDoesExist() && !mFlagButtonIsShown) {
                        showPlaceFlag();
                        mHasPlacedFlag = false;
                    } else if (ownFlag.getDoesExist() && mFlagButtonIsShown) {
                        hidePlaceFlag();
                        mHasPlacedFlag = true;
                    }
                }, throwable -> Log.e(TAG, throwable.getMessage()));
    }

    /**
     * Hide the fab and blocking the ability to place a flag.
     */
    private void hidePlaceFlag() {
        mPlaceFlagButton
                .animate()
                .translationY(mPlaceFlagButton.getHeight() + mPlaceFlagButtonMarginBottom)
                .setDuration(150)
                .setInterpolator(PathInterpolatorCompat.create(0.4f, 0.0f, 0.6f, 1f))
                .start();

        mFlagButtonIsShown = false;
    }

    /**
     * Show the fab and add the ability to place a flag.
     */
    private void showPlaceFlag() {
        // if we have set the visibility to invisible (as we do in onCreate), we should put this
        // below the screen
        if (mPlaceFlagButton.getVisibility() == View.INVISIBLE) {
            mPlaceFlagButton.setTranslationY(
                    mPlaceFlagButton.getHeight() + mPlaceFlagButtonMarginBottom
            );

            mPlaceFlagButton.setVisibility(View.VISIBLE);
        }

        mPlaceFlagButton
                .animate()
                .translationY(0)
                .setDuration(150)
                .setInterpolator(new LinearOutSlowInInterpolator())
                .start();

        mFlagButtonIsShown = true;
    }

    /**
     * Draw a different bloop over the main bloops to prompt the user to click and capture a flag.
     */
    private void captureFlag() {
        if (mNearbyFlag != null) {
            CapturedFlag flag = new CapturedFlag(
                    mNearbyFlag.getFlagId(),
                    mApplication.getPlayerId()
            );

            // TODO: slowly mute the boop sounds so the bloop is better

            mService.captureFlag(flag)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        // flag capture success!
                        if (!mute) {
                            mBloopSoundPlayer.bloop();
                        }

                        final FlagView flagView = new FlagView(getBaseContext());
                        flagView.setFlagColor(mNearbyFlag.getColor());

                        final FlagCapturedDialogFragment flagCapturedDialog = new FlagCapturedDialogFragment();
                        final Bundle flagCapturedDialogBundle = new Bundle();
                        flagCapturedDialogBundle.putString(
                                FlagCapturedDialogFragment.ARG_TITLE,
                                String.format(getString(R.string.you_captured_x_flag_format_string), mNearbyFlag.getPlayerName())
                        );

                        flagCapturedDialogBundle.putInt(
                                FlagCapturedDialogFragment.ARG_FLAG_COLOR,
                                mNearbyFlag.getColor()
                        );

                        flagCapturedDialogBundle.putString(
                                FlagCapturedDialogFragment.ARG_POINTS_TEXT,
                                "Flag capture: +1 point"
                        );

                        final int playerScore = 1; // TODO: from API

                        flagCapturedDialogBundle.putString(
                                FlagCapturedDialogFragment.ARG_TOTAL_SCORE,
                                "Score: " + playerScore
                        );

                        Games.Leaderboards.submitScore(
                                mGoogleApiClient,
                                getString(R.string.leaderboard_bloop_high_scores),
                                playerScore
                        );

                        // trigger breaking ground if we haven't gotten it yet
                        triggerAchievement(getString(R.string.achievement_breaking_ground));

                        flagCapturedDialog.setArguments(flagCapturedDialogBundle);
                        flagCapturedDialog.show(getSupportFragmentManager(), "FlagDialog");

                        mBigButtonView.hide();
                    }, throwable -> {
                        Log.e(TAG, throwable.getMessage());
                        mBigButtonView.hide();
                    });
        }
    }

    /**
     * Triggers an achievement if the achievement hasn't been gotten yet (checks local prefs)
     * @param achievementId the google play achievement id
     */
    private void triggerAchievement(String achievementId) {
        SharedPreferences pref = getSharedPreferences(PREF_ACHIEVEMENT_TRACKER, Context.MODE_PRIVATE);
        boolean haveAchievement = pref.getBoolean(achievementId, false);
        // bail if we have it already
        if (haveAchievement) {
            return;
        }

        // if we haven't gotten it yet, get it now
        Games.Achievements.unlock(mGoogleApiClient, achievementId);

        // store this so we don't waste API calls the next time
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean(achievementId, true);
        ed.apply();
    }

    private void deleteFlag() {
        mService.deleteFlag(mApplication.getPlayerId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ownFlag -> {
                    Toast.makeText(this, "Flag deleted", Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage());
                });

        showPlaceFlag();
    }

    /**
     * Shows an activity that describes the open source libraries used in this project.
     */
    private void startAboutLibraries() {
        new LibsBuilder()
                //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                //start the activity
                .start(this);
    }

    /**
     * Request location permissions so we can track location.
     * @return
     */
    private Observable<Boolean> requestLocationPermissions() {
        return RxPermissions.getInstance(this)
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .doOnEach(granted -> {
                    Log.i(TAG, String.format("Location permission granted: %s", granted));
                });
    }

    /**
     * Main location tracking logic.
     */
    private void startTrackingLocation() {
        requestLocationPermissions().subscribe(granted -> {
            if (!granted) {
                // TODO: launch dialog telling them why
                Log.d(TAG, "Location permissions denied");
            } else {
                Log.d(TAG, "Location tracking started");

                // Request location now that we know we have permission to do so
                LocationRequest locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(LOCATION_UPDATE_MS);

                //noinspection MissingPermission
                mLocationDisposable = mRxLocation.location().updates(locationRequest)
                        .doOnEach(location -> mCurrentLocation = location.getValue())
                        .doOnEach(location -> mBootprintMapFragment.updateMapCenter(location.getValue()))
                        .doOnEach(location -> mBootprintMapFragment.updatePlayerLocation(location.getValue()))
                        .doOnEach(location -> updateBloopFrequency())
                        .subscribe();
            }
        });
    }

    /**
     * Lets a user customize and place a flag at their current location.
     * Passes intent to FlagCreationActivity.
     */
    private void placeFlag() {
        if (mCurrentLocation != null) {

            final Intent placeFlagIntent = new Intent(this, FlagCreationActivity.class);
            placeFlagIntent.putExtra(FlagCreationActivity.FLAG_LOCATION, mCurrentLocation);
            startActivityForResult(placeFlagIntent, PLACE_FLAG_ACTIVITY_RESULT);
        }
        hidePlaceFlag();
        mHasPlacedFlag = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_FLAG_ACTIVITY_RESULT) {
            if(resultCode == Activity.RESULT_OK){
                hidePlaceFlag();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // just in case
                showPlaceFlag();
            }
        }
    }

    private void opponentCapturedPlayerFlag(String opponentNameAndText) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // TODO: put this in strings file.
        builder.setTitle(opponentNameAndText) // TODO: when Sam sends me the actual name, change this
                .setNeutralButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .setMessage("You have lost a point, dang.")
                .show();
    }

    /**
     * Check with server to compute closest flag and update how often bloops are animated on-screen.
     */
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
                            mNearbyFlag = nearbyFlag;

                            if (mHasPlacedFlag) {
                                mBigButtonView.show();
                            }
                        } else {
                            mNearbyFlag = null;
                            mBigButtonView.hide();
                        }

                        rescheduleBloops();
                    }, throwable -> Log.e(TAG, throwable.getMessage()));
        }
    }

    /**
     * Actually reschedule the "timer" to redraw a bloop on screen.
     */
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

    /**
     * BLOOP!!!
     */
    private void bloop() {
        mSonarView.bloop();
        if (!mute) {
            mBloopSoundPlayer.boop();
        }

        mLastBloopTime = System.currentTimeMillis();
    }

    /**
     * Inflate options dropdown, as well as set mute checkbox to preexisting value, if one available.
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bloop_activity_menu, menu);

        // set default value of checkbox
        MenuItem item = menu.findItem(R.id.item_mute);
        item.setChecked(mute);

        return true;
    }

    /**
     * Show leaderboards, mute audio, show library information.
     * @param item
     * @return
     */
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
            case R.id.item_delete:
                deleteFlag();
                return true;
            case R.id.item_mute:
                SharedPreferences.Editor ed = mutePref.edit();
                if (!mute) {
                    mute = true;
                    item.setChecked(true);
                    ed.putBoolean(PREF_SOUND_VAL, true);
                } else {
                    mute = false;
                    item.setChecked(false);
                    ed.putBoolean(PREF_SOUND_VAL, false);
                }
                ed.apply();
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

        checkHasPlacedFlag();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras.containsKey(ARG_OPPONENT)) {
            final String opponentName = extras.getString(ARG_OPPONENT);

            opponentCapturedPlayerFlag(opponentName);
        }
    }
}
