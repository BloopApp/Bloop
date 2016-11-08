package website.bloop.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import website.bloop.app.R;

public class FlagView extends View {

    private static final float X_INCREMENT = 20;
    private static final float FLAG_WAVE_HEIGHT = 50;
    private static final String TAG = "FlagView";
    private boolean showPole;
    private Paint flagPaint;
    private Path flagPath;
    private float flagWidth;
    private float flagHeight;
    private float startLeft;
    private float startTop;

    public FlagView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FlagView,
                0, 0
        );

        try {
            showPole = a.getBoolean(R.styleable.FlagView_showPole, false);
        } finally {
            a.recycle();
        }

        init();

        calculatePath();
    }

    private void init() {
        flagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        flagPaint.setStyle(Paint.Style.FILL);
        flagPaint.setColor(Color.RED);

        flagPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int drawableWidth = w - getPaddingLeft() - getPaddingRight();
        int drawableHeight = h - getPaddingTop() - getPaddingBottom();

        float aspectRatio = drawableHeight / drawableWidth;

        if (aspectRatio > 2f / 3f) {
            // width is the restrictive
            flagWidth = drawableWidth;
            flagHeight = drawableWidth / 3f * 2f;
            startLeft = 0f;
            startTop = (drawableWidth - flagHeight) / 2f;
        } else {
            flagHeight = drawableHeight;
            flagWidth = drawableHeight / 2f * 3f;
            startLeft = (drawableWidth - flagWidth) / 2f;
            startTop = 0f;
        }

        calculatePath();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(flagPath, flagPaint);

        Log.d(TAG, flagPath.toString());
    }

    private void calculatePath() {
        flagPath.reset();

        float flagOffset = 0;

        float y;

        flagPath.moveTo(startLeft, startTop);

        if (flagWidth < 1f) {
            flagPath.close();
            return;
        }

        for (float x = startLeft; x <= startLeft + flagWidth; x += flagWidth / 20f) {
            y = (float) (FLAG_WAVE_HEIGHT * - Math.sin((x / flagWidth) * 2 * Math.PI + flagOffset)) + FLAG_WAVE_HEIGHT;
            flagPath.lineTo(x, y);
        }
        for (float x = startLeft + flagWidth; x >= startLeft; x -= flagWidth / 20f) {
            y = (float) (FLAG_WAVE_HEIGHT * - Math.sin((x / flagWidth) * 2 * Math.PI + flagOffset)) + FLAG_WAVE_HEIGHT + flagHeight;
            flagPath.lineTo(x, y);
        }

        flagPath.close();
    }

    public boolean isShowPole() {
        return showPole;
    }

    public void setShowPole(boolean showPole) {
        this.showPole = showPole;
        invalidate();
        requestLayout();
    }

}
