package website.bloop.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import website.bloop.app.R;

/**
 * Flag which is drawn for FlagCreationActivity and can be animated for when placing.
 */
public class FlagView extends View {
    private static final float GOLDEN_RATIO = 1.61803399f;
    private boolean mShowPole;
    private int mFlagColor;
    private Path mTrianglePath;
    private Rect mPoleRect;
    private Paint mFlagPaint;
    private Paint mPolePaint;

    public FlagView(Context context) {
        super(context);
        initialize(null);
    }

    public FlagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public FlagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FlagView,
                0, 0);

        try {
            mShowPole = a.getBoolean(R.styleable.FlagView_showPole, true);
            mFlagColor = a.getColor(R.styleable.FlagView_flagColor, Color.BLACK);
        } finally {
            a.recycle();
        }

        mFlagPaint = new Paint();
        mFlagPaint.setColor(mFlagColor);
        mFlagPaint.setStyle(Paint.Style.FILL);

        mPolePaint = new Paint();
        mPolePaint.setColor(Color.GRAY);
        mPolePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 25;

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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTrianglePath = createFlagTriangle(w);
        mPoleRect = createPoleRect(w, w);

        setMinimumHeight(w);
    }

    private Path createFlagTriangle(float width) {
        final Path triangle = new Path();
        triangle.moveTo(0, 0);
        triangle.lineTo(width, width / GOLDEN_RATIO / 2f);
        triangle.lineTo(0, width / GOLDEN_RATIO);
        triangle.lineTo(0, 0);

        return triangle;
    }

    private Rect createPoleRect(float flagWidth, float poleHeight) {
        return new Rect(
                0,
                (int) (flagWidth / GOLDEN_RATIO / 2f),
                (int) (flagWidth / GOLDEN_RATIO / 6f),
                (int) poleHeight
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPoleRect != null) {
            canvas.drawRect(mPoleRect, mPolePaint);
        }

        if (mTrianglePath != null) {
            canvas.drawPath(mTrianglePath, mFlagPaint);
        }
    }

    public void setFlagColor(int flagColor) {
        this.mFlagColor = flagColor;

        if (mFlagPaint != null) {
            mFlagPaint.setColor(mFlagColor);
        }

        requestLayout();
    }
}
