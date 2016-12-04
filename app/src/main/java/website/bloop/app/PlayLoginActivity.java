package website.bloop.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Login authentication through Google Play Games
 * Reference: https://developers.google.com/games/services/training/signin
 */

public class PlayLoginActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final String TAG = "PlayLogin";
    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = false;
    private boolean mSignInClicked = false;

    private GoogleApiClient mGoogleApiClient;

    @BindView(R.id.signInName) TextView loginText;
    @BindView(R.id.signInButton) Button loginButton;
    @BindView(R.id.sonarView) SonarView bloopView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_login);

        ButterKnife.bind(this);

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        loginButton.setOnClickListener(view -> signInClicked());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String displayName;
        if (p == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
        loginText.setText(displayName);
        Intent newIntent = new Intent(getBaseContext(), BloopActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
        finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, String.valueOf(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = true;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    // Call when the sign-in button is clicked
    public void signInClicked() {
        bloopView.bloop();
        if (!mSignInClicked) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        } else {
            mSignInClicked = false;
            Games.signOut(mGoogleApiClient);
            loginText.setText(R.string.signin_out);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}

