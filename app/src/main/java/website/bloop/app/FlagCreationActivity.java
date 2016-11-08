package website.bloop.app;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlagCreationActivity extends AppCompatActivity {
    private int flagColor = Color.WHITE;

    @BindView(R.id.flag_color)
    TextView flagColorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_creation);

        ButterKnife.bind(this);

        flagColorView.setOnClickListener(view -> this.showColorPickerDialog());
    }

    private void setFlagColor(int flagColor) {
        this.flagColor = flagColor;

        if (flagColorView != null) {
            flagColorView.setTextColor(this.flagColor);
        }
    }

    private void showColorPickerDialog() {
        int currentBackgroundColor = flagColor;
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
