package website.bloop.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Activity for displaying a tutorial on first run after login to Play Games.
 * Library from: https://github.com/PaoloRotolo/AppIntro
 * First launch logic: http://stackoverflow.com/questions/16419627/making-an-activity-appear-only-once-when-the-app-is-started
 */
public class TutorialActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // launch once logic
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false)) {
            Intent intent = new Intent(this, BloopActivity.class);
            startActivity(intent);
            finish();
        }

        // first content: change flag
        addSlide(AppIntroFragment.newInstance("Create your flag",
                "To get started, customize your flag to your liking. Remember, others will see this!",
                R.drawable.splash_icon,
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        // second content: drop flag
        addSlide(AppIntroFragment.newInstance("Drop your flag",
                "Jump into playing by dropping your flag. Finding a secluded spot is key!",
                R.drawable.splash_icon,
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        // third content: capture flag
        addSlide(AppIntroFragment.newInstance("Find and capture flags",
                "Now go on the offensive! Follow the Bloops to find other players' flags. Happy hunting!",
                R.drawable.splash_icon,
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent newIntent = new Intent(getBaseContext(), PlayLoginActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent newIntent = new Intent(getBaseContext(), PlayLoginActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
        finish();
    }

    // don't do anything special on slide change
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
