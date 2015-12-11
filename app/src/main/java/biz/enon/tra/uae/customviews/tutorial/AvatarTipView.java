package biz.enon.tra.uae.customviews.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by Mikazme on 24/09/2015.
 */
public class AvatarTipView extends TutorialTipView {

    //region STATIC PROPERTIES
    private float mSideOffset;
    //endregion

    private boolean initialized = false;

    //region INIT METHODS
    public AvatarTipView(Context _context, AttributeSet _attrs) {
        super(_context, _attrs);
    }

    @Override
    protected void calculateStaticProperties() {
        mViewPointerLineLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        mTextPointerLineLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        mTitleTextPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        mTitleRectMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    }
    //endregion

    public final void setSideOffset(final float _sideOffset) {
        mSideOffset = _sideOffset;
        initialized = true;
        calculateViewPointerPath();
        calculateTextLayouts();
        calculateTextPointerPath();
        requestLayout();
        invalidate();
    }

    private void calculateTextLayouts() {
        mTipTextLayout = new StaticLayout(mTipText, mTipTextPaint,
                (int) (getWidth() - mSideOffset * 2), Layout.Alignment.ALIGN_NORMAL,
                getLineSpacing(), 0, false);

        final int maxTextWidth = (int) (getWidth() - mSideOffset * 2 - mTitleTextPadding * 2);
        final Rect textRect = new Rect();
        mTitleTextPaint.getTextBounds(mTitleText, 0, mTitleText.length(), textRect);
        final int textWidth = textRect.width();

        mTitleTextLayout = new StaticLayout(mTitleText, mTitleTextPaint,
                textWidth < maxTextWidth ? textWidth + 10 : maxTextWidth,
                Layout.Alignment.ALIGN_CENTER, 1, 0, false);
    }

    //region CALCULATE PATHS

    private void calculateViewPointerPath() {
        mLinePointerPath = new Path();

        final float startY = getPaddingTop();
        final float startX = mCenterPoints[0].x;

        final float endY = startY + mViewPointerLineLength;
        final float endX = startX;

        mLinePointerPath.moveTo(startX, startY);
        mLinePointerPath.lineTo(endX, endY);
    }

    private void calculateTextPointerPath() {
        mTextPointerPath = new Path();

        final float startY = getPaddingTop() + mViewPointerLineLength + mTitleRectMargin * 2 +
                mTitleTextPadding * 2 + mTitleTextLayout.getHeight() + mTitleBorderWidth;
        final float startX = mCenterPoints[0].x;


        final float endY = startY + mTextPointerLineLength;
        final float endX = startX;


        mTextPointerPath.moveTo(startX, startY);
        mTextPointerPath.lineTo(endX, endY);
    }

    //endregion


    //region DRAWING
    @Override
    public final void draw(@NonNull final Canvas _canvas) {
        super.draw(_canvas);

        if (initialized) {
            drawViewPointer(_canvas);
            drawTitleRect(_canvas);
            drawTextPointer(_canvas);
            drawTipText(_canvas);
        }
    }

    private void drawViewPointer(final Canvas _canvas) {
        _canvas.drawCircle(mCenterPoints[0].x, getPaddingTop() + mPointRadius, mPointRadius, mViewPointerPaint);
        _canvas.drawPath(mLinePointerPath, mLinePointerPaint);
    }

    private void drawTitleRect(final Canvas _canvas) {
        final float topOfRect = getPaddingTop() + mViewPointerLineLength + mTitleRectMargin;
        final float start = calculateDependsOnDirection(mSideOffset);
        final float end = start + calculateWithCoefficient(mTitleTextLayout.getWidth() + mTitleTextPadding * 2);

        _canvas.drawRect(start < end ? start : end,
                topOfRect,
                start < end ? end : start,
                topOfRect + mTitleTextPadding * 2 + mTitleTextLayout.getHeight(),
                mTitleBorderPaint);

        _canvas.save();
        _canvas.translate(start < end ? (start + calculateWithCoefficient(mTitleTextPadding)): (end - calculateWithCoefficient(mTitleTextPadding)),
                topOfRect + mTitleTextPadding);
        mTitleTextLayout.draw(_canvas);
        _canvas.restore();
    }

    private void drawTextPointer(final Canvas _canvas) {
        _canvas.drawPath(mTextPointerPath, mLinePointerPaint);
    }

    private void drawTipText(final Canvas _canvas) {
        final float translateY = getPaddingTop() + mViewPointerLineLength + mTitleRectMargin * 2 +
                mTitleTextPadding * 2 + mTitleTextLayout.getHeight() + mTitleBorderWidth +
                mTextPointerLineLength + mTitleRectMargin;

        _canvas.save();
        _canvas.translate(mSideOffset, translateY);
        mTipTextLayout.draw(_canvas);
        _canvas.restore();
    }
    //endregion


}
