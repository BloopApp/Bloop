package website.bloop.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlagCreationActivity extends AppCompatActivity {
    private int mFlagColor = Color.WHITE;

    @BindView(R.id.flag_button) ImageButton mFlagColorView;

    @BindView(R.id.next_button) Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_creation);

        ButterKnife.bind(this);

        mFlagColorView.setOnClickListener(view -> this.showColorPickerDialog());

        mNextButton.setOnClickListener(view -> this.onClickNextButton());
    }

    private void setFlagColor(int flagColor) {
        this.mFlagColor = flagColor;

        if (mFlagColorView != null) {
            mFlagColorView.setColorFilter(this.mFlagColor);
        }
    }

    private void onClickNextButton() {
        // TODO for now, passing intent to bloop activity
        // TODO actually send data and logic for different flag stuff
        Intent newIntent = new Intent(getBaseContext(), BloopActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
        finish();
    }

    private void showColorPickerDialog() {
        int currentBackgroundColor = mFlagColor;
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose flag color")
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
