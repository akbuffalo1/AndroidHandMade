package biz.enon.tra.uae.customviews.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Mikazme on 25/09/2015.
 */
public class HotBarTipView extends TutorialTipView {

    //region PROPERTIES
    private float mTextPadding;
    private TextView mTipTextView;
    private boolean isInitialized = false;
    private float mMultiplePointerLength;
    //endregion

    //region INIT METHODS
    public HotBarTipView(Context _context, AttributeSet _attrs) {
        super(_context, _attrs);
    }

    @Override
    public void setCenterPoints(PointF[] _centerPoints) {
        super.setCenterPoints(_centerPoints);
        isInitialized = true;
        calculateVariables();
        calculateTextLayouts();
        calculateLinePointerPath();
        calculateTextPointerPath();
        requestLayout();
    }

    @Override
    protected void calculateStaticProperties() {
        mTitleTextPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
    }
    //endregion

    //region CALCULATING
    private void calculateVariables() {
        mViewPointerLineLength = getHeight() * 0.15f;
        mTextPointerLineLength = mViewPointerLineLength * 0.4f;
        mMultiplePointerLength = mViewPointerLineLength * 0.4f;
    }

    private void calculateTextLayouts() {
        mTextPadding = getWidth() * 0.05f;

        final int maxTextWidth = (int) (getWidth() - mTextPadding * 2 - mTitleTextPadding * 2);
        final Rect textRect = new Rect();
        mTitleTextPaint.getTextBounds(mTitleText, 0, mTitleText.length(), textRect);
        final int textWidth = textRect.width();

        mTitleTextLayout = new StaticLayout(mTitleText, mTitleTextPaint,
                textWidth < maxTextWidth ? (textWidth + 20) : maxTextWidth,
                Layout.Alignment.ALIGN_CENTER, 1, 0, false);


        mTipTextLayout = new StaticLayout(mTipText, mTipTextPaint,
                (int) (getWidth() - mTextPadding * 2), Layout.Alignment.ALIGN_NORMAL,
                getLineSpacing(), 0, false);
    }

    private void calculateLinePointerPath() {
        mLinePointerPath = new Path();
        PointF firstPoint = mCenterPoints[0];
        PointF lastPoint = mCenterPoints[mCenterPoints.length - 1];

        for (PointF center : mCenterPoints) {
            if (center.x < firstPoint.x) {
                firstPoint = center;
            }
            if(center.x > lastPoint.x) {
                lastPoint = center;
            }

            mLinePointerPath.moveTo(center.x, getPaddingTop() + mPointRadius);
            mLinePointerPath.lineTo(center.x, getPaddingTop() + mPointRadius + mMultiplePointerLength);
        }

        final float startY = getPaddingTop() + mPointRadius + mMultiplePointerLength + mLineWidth / 2;

        mLinePointerPath.moveTo(firstPoint.x, startY);
        mLinePointerPath.lineTo(lastPoint.x, startY);

        final float centerX = getWidth() / 2 - mLineWidth / 2;

        mLinePointerPath.moveTo(centerX, startY);
        mLinePointerPath.lineTo(centerX, startY + mViewPointerLineLength - mMultiplePointerLength);
    }

    private void calculateTextPointerPath() {
        mTextPointerPath = new Path();

        final float startY = getPaddingTop() + mPointRadius + mViewPointerLineLength +
                mTitleBorderWidth + mTitleTextPadding * 2 +
                mTitleBorderWidth + mTitleTextLayout.getHeight() + mTitleBorderWidth;
        final float endY = startY + mTextPointerLineLength;

        mTextPointerPath.moveTo(getWidth() / 2 - mLineWidth / 2, startY);
        mTextPointerPath.lineTo(getWidth() / 2 - mLineWidth / 2, endY);
    }
    //endregion

    //region DRAWING
    @Override
    public final void draw(final Canvas _canvas) {
        super.draw(_canvas);

        if (isInitialized) {
            drawViewPointer(_canvas);
            drawTextPointer(_canvas);
            drawTitleRect(_canvas);
            drawTipText(_canvas);
        }
    }

    private void drawViewPointer(final Canvas _canvas) {
        _canvas.drawPath(mLinePointerPath, mLinePointerPaint);
        for (PointF center : mCenterPoints) {
            _canvas.drawCircle(center.x, getPaddingTop() + mPointRadius, mPointRadius, mViewPointerPaint);
        }
    }

    private void drawTitleRect(final Canvas _canvas) {
        final float topOfRect = getPaddingTop() + mPointRadius + mViewPointerLineLength + mTitleBorderWidth;
        final float bottomOfRect = topOfRect + mTitleTextPadding * 2 + mTitleBorderWidth + mTitleTextLayout.getHeight();
        final float start = getWidth() / 2 - mTitleTextLayout.getWidth() / 2 - mTitleTextPadding - mTitleBorderWidth / 2;
        final float end = getWidth() / 2 + mTitleTextLayout.getWidth() / 2 + mTitleTextPadding + mTitleBorderWidth / 2;

        _canvas.drawRect(start, topOfRect, end, bottomOfRect, mTitleBorderPaint);

        _canvas.save();
        _canvas.translate(start + mTitleTextPadding,
                topOfRect + mTitleTextPadding);
        mTitleTextLayout.draw(_canvas);
        _canvas.restore();
    }

    private void drawTextPointer(final Canvas _canvas) {
        _canvas.drawPath(mTextPointerPath, mLinePointerPaint);
    }

    private void drawTipText(final Canvas _canvas) {
        final float translateY = getPaddingTop() + mPointRadius + mViewPointerLineLength +
                mTitleBorderWidth + mTitleTextPadding * 2 +
                mTitleBorderWidth + mTitleTextLayout.getHeight() +
                mTextPadding / 4 + mTextPointerLineLength;

        _canvas.save();
        _canvas.translate(mTextPadding, translateY);
        mTipTextLayout.draw(_canvas);
        _canvas.restore();
    }
    //endregion
}
