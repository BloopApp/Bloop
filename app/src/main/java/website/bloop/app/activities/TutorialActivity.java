package website.bloop.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import website.bloop.app.R;

import static website.bloop.app.BloopApplication.BLOOP_PREFERENCE_FILE;

/**
 * Activity for displaying a tutorial on first run after login to Play Games.
 * Library from: https://github.com/PaoloRotolo/AppIntro
 * First launch logic: http://stackoverflow.com/questions/16419627/making-an-activity-appear-only-once-when-the-app-is-started
 */
public class TutorialActivity extends AppIntro {
    private static final String PREF_TUTORIAL_RUN = "tutorial_run";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // second content: drop flag
        addSlide(AppIntroFragment.newInstance("Place your first flag",
                "Jump into playing by dropping your flag. Finding a secluded spot is key!",
                R.drawable.ic_bloop_flag_placed_icon,
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        // first content: change flag
        addSlide(AppIntroFragment.newInstance("Create your flag",
                "When you place your flag, you get a chance to customize it. Remember, others will see this!",
                R.drawable.landscape,
                ContextCompat.getColor(getApplicationContext(), R.color.colorSky)));

        // third content: capture flag
        addSlide(AppIntroFragment.newInstance("Find and capture flags",
                "Now go on the offensive! Follow the \"bloop\" sounds to find other players' flags. The more often you see the bloops, the closer you are. Happy hunting!",
                R.drawable.landscape,
                ContextCompat.getColor(getApplicationContext(), R.color.colorSky)));
    }

    /**
     * Skip past the tutorial activity if pressed.
     * @param currentFragment
     */
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        saveTutorialDone();

        leaveTutorial();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        saveTutorialDone();

        leaveTutorial();
    }

    /**
     * Do not do anything special on slide change.
     * @param oldFragment
     * @param newFragment
     */
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    /**
     * Update the pref to now skip the tutorial activity on subsequent runs.
     */
    private void saveTutorialDone() {
        // set the pref to skip this activity now
        SharedPreferences pref = getSharedPreferences(BLOOP_PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean(PREF_TUTORIAL_RUN, true);
        ed.apply();
    }

    /**
     * Pass intent to login activity.
     */
    private void leaveTutorial() {
        Intent newIntent = new Intent(getBaseContext(), PlayLoginActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
        finish();
    }

    /**
     * Check if not first run, skip tutorial activity if so.
     * @param context
     * @return
     */
    public static boolean hasTutorialRun(Context context) {
        SharedPreferences activityPref = context.getSharedPreferences(BLOOP_PREFERENCE_FILE, Context.MODE_PRIVATE);
        return activityPref.getBoolean(PREF_TUTORIAL_RUN, false);
    }
}
