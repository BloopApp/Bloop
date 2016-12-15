package website.bloop.app.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import website.bloop.app.R;
import website.bloop.app.views.FlagView;

public class FlagCapturedDialogFragment extends DialogFragment {
    public static final String ARG_TITLE = "DialogTitle";
    public static final String ARG_FLAG_COLOR = "DialogFlagColor";
    public static final String ARG_POINTS_TEXT = "DialogPointsText";

    @BindView(R.id.captured_title_view)
    TextView titleView;

    @BindView(R.id.captured_flag_view)
    FlagView flagView;

    @BindView(R.id.captured_points_view)
    TextView pointsView;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View inflatedDialog = inflater.inflate(R.layout.dialog_captured_flag, null);
        ButterKnife.bind(this, inflatedDialog);

        builder.setView(inflatedDialog)
                .setNeutralButton(
                        getString(R.string.dismiss_capture_flag_dialog_text),
                        (dialogInterface, i) -> dialogInterface.dismiss()
                );

        final Bundle arguments = getArguments();

        final String title = arguments.getString(ARG_TITLE);
        titleView.setText(title);

        final int flagColor = arguments.getInt(ARG_FLAG_COLOR);
        flagView.setFlagColor(flagColor);

        final String pointsText = arguments.getString(ARG_POINTS_TEXT);
        pointsView.setText(pointsText);

        return builder.create();
    }
}
