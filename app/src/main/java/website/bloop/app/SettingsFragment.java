package website.bloop.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class SettingsFragment extends Fragment {
    @BindView(R.id.leaderboard_text)
    TextView leaderboardText;

    @BindView(R.id.signOutButton)
    Button signOutButton;

    private GoogleApiClient mGoogleApiClient;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mGoogleApiClient = BloopApplication.getInstance().getClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        signOutButton.setOnClickListener(frag -> signOut());

        leaderboardText.setText("This is just more text to see if everything looks all peachy");
        return view;
    }

    private void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Games.signOut(mGoogleApiClient).setResultCallback(status -> {
                        Toast.makeText(getContext(), "Signed out of Play Games", Toast.LENGTH_SHORT).show();

                        Intent newIntent = new Intent(getContext(), PlayLoginActivity.class);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(newIntent);
                        getActivity().finish();
                    });
        }

        SharedPreferences pref = getActivity().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("activity_executed", false);
        ed.apply();
    }
}
