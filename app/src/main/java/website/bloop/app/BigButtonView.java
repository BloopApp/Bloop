package website.bloop.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;

public class BigButtonView extends View {
    private Paint mButtonPrimary;
    private float mRadiusCoef;

    public BigButtonView(Context context) {
        super(context);
        initialize();
    }

    public BigButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);


        initialize();
    }

    public BigButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void initialize() {
        mRadiusCoef = 0f;

        mButtonPrimary = new Paint();
        mButtonPrimary.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mButtonPrimary.setStyle(Paint.Style.FILL);
        mButtonPrimary.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 50;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(width, heightSize); // want square
        } else {
            //Be whatever you want
            height = width; // square
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRadiusCoef > 0) {
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 4f * mRadiusCoef, mButtonPrimary);
        }
    }

    public void setRadiusCoef(float radiusCoef) {
        this.mRadiusCoef = radiusCoef;
    }

    public void show() {
        final BigButtonAnimation animation = new BigButtonAnimation(this, true);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(250);

        setAnimation(animation);
    }

    public void hide() {
        final BigButtonAnimation animation = new BigButtonAnimation(this, false);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(250);

        setAnimation(animation);
    }

    private class BigButtonAnimation extends Animation {
        private BigButtonView mBigButtonView;

        private float mStartPoint;
        private float mEndPoint;

        /**
         * Creates an animation for the button
         * @param bigButtonView
         * @param grow whether the animation makes it bigger or smaller
         */
        BigButtonAnimation(BigButtonView bigButtonView, boolean grow) {
            mBigButtonView = bigButtonView;

            mStartPoint = bigButtonView.mRadiusCoef;
            mEndPoint = grow ? 1.0f : 0.0f;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mBigButtonView.setRadiusCoef(
                    (float) ((1.0 - interpolatedTime) * mStartPoint + interpolatedTime * mEndPoint)
            );

            mBigButtonView.requestLayout();
        }
    }
}
