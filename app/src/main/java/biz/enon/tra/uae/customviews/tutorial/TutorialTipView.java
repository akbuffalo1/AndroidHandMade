package biz.enon.tra.uae.customviews.tutorial;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.View;

import com.enon.tra.uae.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mikazme on 24/09/2015.
 */
public abstract class TutorialTipView extends View {

    //region TUTORIAL TYPES
    public static final int TUTORIAL_AVATAR = 0;
    public static final int TUTORIAL_HOT_BAR = 1;
    public static final int TUTORIAL_TAB_BAR = 2;
    //endregion

    //region PROPERTIES
    protected int mTutorialType;
    protected float mPointRadius;
    protected float mLineWidth;
    protected float mTitleBorderWidth;
    protected float mViewPointerLineLength;
    protected float mTextPointerLineLength;
    protected float mTitleTextPadding;
    protected StaticLayout mTipTextLayout;
    protected StaticLayout mTitleTextLayout;
    protected float mTitleRectMargin;
    protected String mTitleText;
    protected String mTipText;
    protected PointF[] mCenterPoints;
    //endregion

    //region COLORS
    protected int mLineColor;
    protected int mTitleBorderColor;
    protected int mTextColor;
    protected int mViewPointerColor;
    //endregion

    //region TEXT SIZES
    protected float mTitleTextSize;
    protected float mTipTextSize;
    //endregion

    //region PAINTS
    protected TextPaint mTitleTextPaint;
    protected TextPaint mTipTextPaint;
    protected Paint mViewPointerPaint;
    protected Paint mLinePointerPaint;
    protected Paint mTitleBorderPaint;
    //endregion

    //region PATHS
    protected Path mLinePointerPath;
    protected Path mTextPointerPath;
    //endregion

    //region INIT METHODS

    public TutorialTipView(final Context _context, final AttributeSet _attrs) {
        super(_context, _attrs);
        initProperties(_attrs);
        initPaint();
        calculateStaticProperties();
    }

    private void initProperties(final AttributeSet _attrs) {
        TypedArray typedArrayData = getContext().getTheme().
                obtainStyledAttributes(_attrs, R.styleable.TutorialTip, 0, 0);
        try {
            mTutorialType = typedArrayData.getInt(R.styleable.TutorialTip_tutorialTipType, 0);
            mPointRadius = typedArrayData.getDimension(R.styleable.TutorialTip_viewPointerRadius, 5);
            mLineWidth = typedArrayData.getDimension(R.styleable.TutorialTip_linePointerWidth, 3);
            mTitleBorderWidth = typedArrayData.getDimension(R.styleable.TutorialTip_titleBorderWidth, 3);
            mLineColor = typedArrayData.getColor(R.styleable.TutorialTip_linePointerColor, Color.WHITE);
            mTitleBorderColor = typedArrayData.getColor(R.styleable.TutorialTip_linePointerColor, Color.WHITE);
            mViewPointerColor = typedArrayData.getColor(R.styleable.TutorialTip_viewPointerColor, Color.WHITE);
            mTextColor = typedArrayData.getColor(R.styleable.TutorialTip_tipTextColor, Color.WHITE);
            mTipTextSize = typedArrayData.getDimension(R.styleable.TutorialTip_tipTextSize, 15);
            mTitleTextSize = typedArrayData.getDimension(R.styleable.TutorialTip_titleTextSize, 15);
            mTipText = typedArrayData.getString(R.styleable.TutorialTip_tipText);
            mTitleText = typedArrayData.getString(R.styleable.TutorialTip_titleText);
        } finally {
            typedArrayData.recycle();
        }
    }

    private void initPaint() {
        mTitleTextPaint = new TextPaint();
        mTitleTextPaint.setAntiAlias(true);
        mTitleTextPaint.setTextSize(mTitleTextSize);
        mTitleTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), CalligraphyConfig.get().getFontPath()));
        mTitleTextPaint.setColor(mTextColor);

        mTipTextPaint = new TextPaint();
        mTipTextPaint.setAntiAlias(true);
        mTipTextPaint.setTextSize(mTipTextSize);
        mTipTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), CalligraphyConfig.get().getFontPath()));
        mTipTextPaint.setColor(mTextColor);

        mViewPointerPaint = new Paint();
        mViewPointerPaint.setAntiAlias(true);
        mViewPointerPaint.setStyle(Paint.Style.FILL);
        mViewPointerPaint.setColor(mViewPointerColor);

        mLinePointerPaint = new Paint();
        mLinePointerPaint.setAntiAlias(true);
        mLinePointerPaint.setStyle(Paint.Style.STROKE);
        mLinePointerPaint.setColor(mLineColor);
        mLinePointerPaint.setStrokeWidth(mLineWidth);
        mLinePointerPaint.setStrokeCap(Paint.Cap.ROUND);

        mTitleBorderPaint = new Paint();
        mTitleBorderPaint.setAntiAlias(true);
        mTitleBorderPaint.setStyle(Paint.Style.STROKE);
        mTitleBorderPaint.setColor(mTitleBorderColor);
        mTitleBorderPaint.setStrokeWidth(mTitleBorderWidth);
        mTitleBorderPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    protected abstract void calculateStaticProperties();

    //endregion

    //region PUBLIC METHODS

    public void setCenterPoints(final PointF[] _centerPoints) {
        mCenterPoints = _centerPoints;
    }

    //endregion

    //region CALCULATING

    @Override
    protected final void onMeasure(final int _widthMeasureSpec, final int _heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(_widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(_heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
    }
    //endregion


    //region LAYOUT DIRECTION METHODS
    protected float getStartPoint() {
        final int direction = getLayoutDirection();
        switch (direction) {
            default:
            case View.LAYOUT_DIRECTION_LTR:
                return 0;
            case View.LAYOUT_DIRECTION_RTL:
                return getWidth();
        }
    }

    protected float getDirectionCoeff() {
        final int direction = getLayoutDirection();
        switch (direction) {
            default:
            case LayoutDirection.LTR:
                return 1;
            case LayoutDirection.RTL:
                return -1;
        }
    }

    protected float calculateWithCoefficient(final float _number) {
        return getDirectionCoeff() * _number;
    }

    protected float calculateDependsOnDirection(final float _number) {
        return getStartPoint() + getDirectionCoeff() * _number;
    }

    protected float getLineSpacing() {
        return getLayoutDirection() == View.LAYOUT_DIRECTION_RTL ? 0.7f : 1;
    }
    //endregion
}
