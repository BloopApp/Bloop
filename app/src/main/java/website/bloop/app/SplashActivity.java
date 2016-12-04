package website.bloop.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Splash screen, which is useful because we want a login to happen on the first launch as well as
 * a tutorial. Once those are completed, we do not want to use those screens anymore.
 * Splash reference: https://www.bignerdranch.com/blog/splash-screens-the-right-way/
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }
}
