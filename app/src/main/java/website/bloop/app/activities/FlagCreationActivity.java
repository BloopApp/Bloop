package website.bloop.app.activities;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import website.bloop.app.BloopApplication;
import website.bloop.app.R;
import website.bloop.app.api.PlacedFlag;
import website.bloop.app.views.FlagView;

public class FlagCreationActivity extends AppCompatActivity {
    public static final String FLAG_LOCATION = "ARG_FLAG_LOCATION";
    private static final String TAG = "FlagCreationActivity";

    private int mFlagColor = Color.WHITE;
    private Location mFlagLocation;

    @BindView(R.id.ui_flag_creation)
    RelativeLayout mUiFlagCreation;

    @BindView(R.id.flag_view_row)
    LinearLayout mFlagViewRow;

    @BindView(R.id.flag_view)
    FlagView mFlagView;

    @BindView(R.id.next_button)
    Button mNextButton;

    private BloopApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_creation);

        Intent intent = getIntent();

        mFlagLocation = intent.getParcelableExtra(FLAG_LOCATION);

        ButterKnife.bind(this);

        mFlagView.setOnClickListener(view -> this.showColorPickerDialog());

        mNextButton.setOnClickListener(view -> this.onClickNextButton());

        mApplication = BloopApplication.getInstance();
    }

    private void setFlagColor(int flagColor) {
        this.mFlagColor = flagColor;

        if (mFlagView != null) {
            mFlagView.setFlagColor(this.mFlagColor);
        }
    }

    private void onClickNextButton() {
        Log.i(TAG, "Placing flag at " + mFlagLocation.getLatitude() + ", " + mFlagLocation.getLongitude());

        mUiFlagCreation.setClipChildren(false);
        mFlagViewRow.setClipChildren(false);


        final ViewPropertyAnimator animator = mFlagView.animate().translationY(
                mUiFlagCreation.getHeight() - mFlagView.getHeight()
        );
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);

        animator.start();

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sendPlaceFlagRequest();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void sendPlaceFlagRequest() {
        BloopApplication application = BloopApplication.getInstance();
        PlacedFlag newFlag = new PlacedFlag(application.getPlayerId(), mFlagLocation, mFlagColor);
        Log.d(TAG, newFlag.toString());

        mApplication.getService().placeFlag(newFlag)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> finish(), throwable -> {
                    Log.e(TAG, throwable.getMessage());

                    Toast.makeText(
                            getBaseContext(),
                            R.string.on_flag_placement_fail,
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                });
    }

    private void showColorPickerDialog() {
        int currentBackgroundColor = mFlagColor;
        ColorPickerDialogBuilder
                .with(this)
                .setTitle(getString(R.string.choose_flag_color))
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(this::setFlagColor)
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> setFlagColor(selectedColor))
                .setNegativeButton("cancel", (dialog, which) -> {
                    // nothing
                })
                .noSliders()
                .build()
                .show();
    }
}
