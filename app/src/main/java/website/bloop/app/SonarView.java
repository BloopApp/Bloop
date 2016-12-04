package website.bloop.app;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class SonarView extends RelativeLayout {
    private static final String TAG = "SonarView";
    private int mSonarColor;
    private Paint mPaint;
    private LayoutParams mSonarBloopParams;
    private AnimatorSet mAnimatorSet;
    private ArrayList<Animator> mAnimatorList;
    private ArrayList<SonarBloopView> mSonarBloopViewList;

    public SonarView(Context context) {
        super(context);
    }

    public SonarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public SonarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(final Context context, final AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SonarOverlay);

        mSonarColor = typedArray.getColor(R.styleable.SonarOverlay_sonar_color, ContextCompat.getColor(getContext(), R.color.colorSonar));

        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSonarColor);

        mSonarBloopParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSonarBloopParams.addRule(CENTER_IN_PARENT, TRUE);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());

        mAnimatorList = new ArrayList<>();

        mSonarBloopViewList = new ArrayList<>();
    }

    public void bloop() {
        SonarBloopView sonarBloopView = new SonarBloopView(
                getContext(), Math.max(getWidth(), getHeight())
        );
        addView(sonarBloopView, mSonarBloopParams);
        mSonarBloopViewList.add(sonarBloopView);

        sonarBloopView.setVisibility(VISIBLE);

        final SonarBloopAnimation animation = new SonarBloopAnimation(sonarBloopView);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removeView(sonarBloopView);
                mSonarBloopViewList.remove(sonarBloopView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        sonarBloopView.setAnimation(animation);
    }

    private class SonarBloopView extends View {
        private float mBloopRadius;
        private float mRadiusCoef;

        public SonarBloopView(Context context, float bloopRadius) {
            super(context);
            mBloopRadius = bloopRadius;

            setVisibility(INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            mPaint.setAlpha(255 - (int) (mRadiusCoef * 255));

            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadiusCoef * mBloopRadius, mPaint);
        }

        public void setRadiusCoef(float radiusCoef) {
            mRadiusCoef = radiusCoef;
        }
    }

    private class SonarBloopAnimation extends Animation {
        private SonarBloopView mSonarBloopView;

        SonarBloopAnimation(SonarBloopView sonarBloopView) {
            mSonarBloopView = sonarBloopView;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mSonarBloopView.setRadiusCoef(interpolatedTime);
            mSonarBloopView.requestLayout();
        }
    }
}
