package website.bloop.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class SettingsFragment extends Fragment {
    @BindView(R.id.leaderboard_text)
    TextView leaderboardText;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, view);

        leaderboardText.setText("This is just more text to see if everything looks all peachy");
        return view;
    }
}
