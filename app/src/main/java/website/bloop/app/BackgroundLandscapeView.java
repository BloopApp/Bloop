package website.bloop.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundLandscapeView extends View {

    private int mBackgroundColor;
    private int mSunColor;
    private Bitmap mLandscapeBitmap;

    public BackgroundLandscapeView(Context context) {
        super(context);
        initialize();
    }

    public BackgroundLandscapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BackgroundLandscapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        mLandscapeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.landscape);

        mBackgroundColor = ContextCompat.getColor(getContext(), R.color.colorSky);
        mSunColor = ContextCompat.getColor(getContext(), R.color.colorSun);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);

        final int scaledHeight = (int) (((float) mLandscapeBitmap.getHeight() / (float) mLandscapeBitmap.getWidth()) * getWidth()) - 1;

        final int top = getHeight() - scaledHeight;

        canvas.drawBitmap(
                mLandscapeBitmap,
                new Rect(0, 0, mLandscapeBitmap.getWidth() - 1, mLandscapeBitmap.getHeight() - 1),
                new Rect(0, top, getWidth() - 1, top + scaledHeight),
                null
        );
    }
}
