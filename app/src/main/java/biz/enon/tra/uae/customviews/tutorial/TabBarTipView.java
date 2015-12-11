package biz.enon.tra.uae.customviews.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mikazme on 25/09/2015.
 */
public class TabBarTipView extends TutorialTipView {

    //region PROPERTIES
    private float mTextPadding;
    private TextView mTipTextView;
    private LinearLayout mTextLayout;
    //endregion

    //region INIT METHODS
    public TabBarTipView(Context _context, AttributeSet _attrs) {
        super(_context, _attrs);
    }

    @Override
    protected void calculateStaticProperties() {
        mTitleTextPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        mTitleRectMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
    }
    //endregion

    //region CALCULATING
    @Override
    protected final void onSizeChanged(final int _w, final int _h, final int _oldw, final int _oldh) {
        super.onSizeChanged(_w, _h, _oldw, _oldh);

        calculateLineLengths(_h);
        calculateTextLayouts();
        calculateViewPointerPath();
        calculateTextPointer();
    }

    private void calculateLineLengths(final int _height) {
        mViewPointerLineLength = _height * 0.25f;
        mTextPointerLineLength = mViewPointerLineLength * 0.25f;
    }

    private void calculateTextLayouts() {
        mTextPadding = getWidth() * 0.1f;
        mTipTextLayout = new StaticLayout(mTipText, mTipTextPaint,
                (int) (getWidth() - mTextPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        final int maxTextWidth = (int) (getWidth() - mTextPadding * 2 - mTitleTextPadding * 2);
        final Rect textRect = new Rect();
        mTitleTextPaint.getTextBounds(mTitleText, 0, mTitleText.length(), textRect);
        final int textWidth = textRect.width();

        mTitleTextLayout = new StaticLayout(mTitleText, mTitleTextPaint,
                textWidth < maxTextWidth ? (textWidth + 20) : maxTextWidth,
                Layout.Alignment.ALIGN_CENTER, 1, 0.f, false);

        mTipTextView = new TextView(getContext());
        mTipTextView.setGravity(Gravity.BOTTOM);
        mTipTextView.setTextSize(16);
        mTipTextView.setText(mTipText);
        mTipTextView.setEllipsize(TextUtils.TruncateAt.END);
        mTipTextView.setTextColor(mTextColor);
        mTipTextView.setLineSpacing(0, getLineSpacing());
        mTipTextView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), CalligraphyConfig.get().getFontPath()));
        mTipTextView.measure(MeasureSpec.makeMeasureSpec((int) (getWidth() - mTextPadding * 2), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) (getHeight() - getPaddingBottom() - mViewPointerLineLength -
                        mTitleTextPadding * 2 - mTitleTextLayout.getHeight() - mTitleBorderWidth - mTextPointerLineLength - mTextPadding - mTextPadding / 4), MeasureSpec.EXACTLY));
        mTipTextView.layout(0, 0, (int) (getWidth() - mTextPadding * 2), (int) (getHeight() - getPaddingBottom() - mViewPointerLineLength -
                mTitleTextPadding * 2 - mTitleTextLayout.getHeight() - mTitleBorderWidth - mTextPointerLineLength - mTextPadding - mTextPadding / 4));
    }

    private void calculateViewPointerPath() {
        mLinePointerPath = new Path();

        final float centerX = getWidth() / 2 - mLineWidth / 2;

        mLinePointerPath.moveTo(centerX, getHeight() - getPaddingBottom() - mPointRadius);
        mLinePointerPath.lineTo(centerX, getHeight() - getPaddingBottom() - mViewPointerLineLength);
    }

    private void calculateTextPointer() {
        mTextPointerPath = new Path();

        final float centerX = getWidth() / 2 - mLineWidth / 2;
        final float startY = getHeight() - getPaddingBottom() - mViewPointerLineLength -
                mTitleTextPadding * 2 - mTitleTextLayout.getHeight() - mTitleBorderWidth;

        mLinePointerPath.moveTo(centerX, startY);
        mLinePointerPath.lineTo(centerX, startY - mTextPointerLineLength);
    }
    //endregion

    //region DRAWING

    @Override
    public final void draw(final Canvas _canvas) {
        super.draw(_canvas);

        drawPointers(_canvas);
        drawTitleRect(_canvas);
        drawTipText(_canvas);
    }

    private void drawPointers(final Canvas _canvas) {
        _canvas.drawPath(mLinePointerPath, mLinePointerPaint);
        _canvas.drawPath(mTextPointerPath, mLinePointerPaint);


        final float centerX = getWidth() / 2 - mLineWidth / 2;
        final float centerY = getHeight() - getPaddingBottom() - mPointRadius;

        _canvas.drawCircle(centerX, centerY, mPointRadius, mViewPointerPaint);
    }

    private void drawTitleRect(final Canvas _canvas) {
        final float bottomOfRect = getHeight() - getPaddingBottom() - mViewPointerLineLength - mTitleBorderWidth / 2;
        final float topOfRect = bottomOfRect - mTitleTextPadding * 2 - mTitleTextLayout.getHeight() - mTitleBorderWidth / 2;
        final float start = getWidth() / 2 - mTitleTextLayout.getWidth() / 2 - mTitleTextPadding - mTitleBorderWidth / 2;
        final float end = getWidth() / 2 + mTitleTextLayout.getWidth() / 2 + mTitleTextPadding + mTitleBorderWidth / 2;

        _canvas.drawRect(start, topOfRect, end, bottomOfRect, mTitleBorderPaint);

        _canvas.save();
        _canvas.translate(start + mTitleTextPadding,
                topOfRect + mTitleTextPadding);
        mTitleTextLayout.draw(_canvas);
        _canvas.restore();
    }

    private void drawTipText(final Canvas _canvas) {
        _canvas.save();
        _canvas.translate(mTextPadding, mTextPadding);
        mTipTextView.draw(_canvas);
        _canvas.restore();
    }

    //endregion
}
