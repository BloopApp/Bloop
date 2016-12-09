package website.bloop.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

/**
 * Splash screen, which is useful because we want a login to happen on the first launch as well as
 * a tutorial. Once those are completed, we do not want to use those screens anymore.
 * Splash reference: https://www.bignerdranch.com/blog/splash-screens-the-right-way/
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences activityPref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        boolean previouslyStarted = activityPref.getBoolean("activity_executed", false);

        if (!previouslyStarted) {
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, PlayLoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
