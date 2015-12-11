package biz.enon.tra.uae.customviews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.util.ImageUtils;

/**
 * Created by mobimaks on 18.08.2015.
 */
public class DragFrameLayout extends FrameLayout implements OnDragListener {

    //region C
    private final float DELETE_ARC_VISIBLE_HEIGHT = 150 * getResources().getDisplayMetrics().density;
    private final float DELETE_ARC_HEIGHT = 400 * getResources().getDisplayMetrics().density;
    private final float SHADOW_SIZE = 3 * getResources().getDisplayMetrics().density;
    private final float SHADOW_SCALE = 0.95F; // [0.0..1.0]
    private final int DELETE_ARC_ALPHA = 64; // [0..255]
    private final int TRASH_BUTTON_SIZE = Math.round(30 * getResources().getDisplayMetrics().density);
    private final int TRASH_BUTTON_TOP_OFFSET = Math.round(30 * getResources().getDisplayMetrics().density);
    private final int OVERLAY_ITEM_COLOR = 0xfff1f1f1;
    private final int DEFAULT_SHADOW_BACKGROUND_COLOR = 0xfffff5ea;
    private final int START_DELETE_ARC_ANGLE = 180, DELETE_ARC_SWEEP_ANGLE = 180;
    private final int ANIMATION_LENGTH = 240, BUTTON_ANIMATION_DELAY = 20;

    private final RectF mBackgroundRect = new RectF();
    private final RectF mItemOverlayRect = new RectF();
    private final RectF mDeleteArcRect = new RectF();

    private final HexagonView mTrashBtn;
    private ValueAnimator mAreaAnimator, mTrashBtnAnimator;
    private Paint mItemOverlayPaint, mBackgroundPaint, mShadowShadowPaint, mShadowPaint;
    private Paint mDeleteArcPaint;
    //endregion

    private View mDragTarget;
    private ColorStateList mDefaultTextColor;
    private PointF mDragPoint;
    private float mDeleteArcTop, mTrashBtnTop;
    private boolean mNeedRedraw, mIsAnimated, mIsDragging, mIsAnimationStarted;
    private OnItemDeleteListener mItemDeleteListener;
    private int mShadowBackgroundColor, mDeleteColor;

    public DragFrameLayout(final Context _context) {
        this(_context, null);
    }

    public DragFrameLayout(final Context _context, final AttributeSet _attrs) {
        super(_context, _attrs);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        initAttributes(_context, _attrs);
        initPaints();
        setOnDragListener(this);

        mTrashBtn = new HexagonView(getContext());
        mTrashBtn.setHexagonSide(TRASH_BUTTON_SIZE);
        mTrashBtn.setBackgroundColor(mDeleteColor);
        mTrashBtn.setHexagonSrcDrawable(R.drawable.ic_action_delete);
//        mTrashBtn.setHexagonShadow(SHADOW_RADIUS * 2, Color.GRAY);

        final ViewGroup invisibleContainer = new FrameLayout(getContext());
        invisibleContainer.setVisibility(INVISIBLE);
        invisibleContainer.addView(mTrashBtn);
        addView(invisibleContainer);
        initAnimations();
    }

    private void initAttributes(final Context _context, final AttributeSet _attrs) {
        TypedArray a = _context.obtainStyledAttributes(_attrs, R.styleable.DragFrameLayout);
        try {
            mShadowBackgroundColor = a.getColor(R.styleable.DragFrameLayout_shadowBackground, DEFAULT_SHADOW_BACKGROUND_COLOR);
            mDeleteColor = a.getColor(R.styleable.DragFrameLayout_deleteColor, Color.RED);
        } finally {
            a.recycle();
        }
    }

    private void initPaints() {
        mItemOverlayPaint = new Paint();
        mItemOverlayPaint.setStyle(Paint.Style.FILL);
        mItemOverlayPaint.setColor(OVERLAY_ITEM_COLOR);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mShadowBackgroundColor);


        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mShadowPaint.setColor(Color.BLUE);

        mShadowShadowPaint = new Paint();
        mShadowShadowPaint.setStyle(Paint.Style.FILL);
        mShadowShadowPaint.setColor(Color.GRAY);

        mDeleteArcPaint = new Paint();
        mDeleteArcPaint.setAntiAlias(true);
        mDeleteArcPaint.setStyle(Paint.Style.FILL);
        mDeleteArcPaint.setColor(mDeleteColor);
        mDeleteArcPaint.setAlpha(DELETE_ARC_ALPHA);
    }

    private void initAnimations() {
        mAreaAnimator = ValueAnimator.ofFloat(0, DELETE_ARC_VISIBLE_HEIGHT);
        mAreaAnimator.setInterpolator(new OvershootInterpolator());
        mAreaAnimator.setDuration(ANIMATION_LENGTH);
        mAreaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDeleteArcTop = (Float) animation.getAnimatedValue();
//                mNeedRedraw = true;
                invalidateDeleteArea(getWidth(), getHeight());
                //invalidate();
            }
        });

        mTrashBtnAnimator = ValueAnimator.ofFloat(0, DELETE_ARC_VISIBLE_HEIGHT);
        mTrashBtnAnimator.setInterpolator(new OvershootInterpolator());
        mTrashBtnAnimator.setDuration(ANIMATION_LENGTH + BUTTON_ANIMATION_DELAY);
        mTrashBtnAnimator.setStartDelay(BUTTON_ANIMATION_DELAY);
        mTrashBtnAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTrashBtnTop = (Float) animation.getAnimatedValue();
                mNeedRedraw = true;
                invalidate();
            }
        });

        mTrashBtnAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                mTrashBtn.setVisibility(VISIBLE);
                mIsAnimated = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimated = false;
//                mTrashBtn.setVisibility(GONE);
                if (!mIsDragging) {
                    mDragTarget.setVisibility(VISIBLE);
                    mDragTarget = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//                mTrashBtn.setVisibility(GONE);
                mIsAnimated = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public final void starDragChild(final View _view, final ClipData _data) throws IllegalArgumentException {
        if (findViewById(_view.getId()) != null) {//check if drag target inside this container
            mDragTarget = _view;

            TextView textView = (TextView) _view.getTag();
            if (textView != null) {
                mDefaultTextColor = textView.getTextColors();
                textView.setTextColor(mDeleteColor);
                if (ImageUtils.isBlackAndWhiteMode(getContext())) {
                    Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_trash_red));
                    DrawableCompat.setTint(drawable.mutate(), mDeleteColor);
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trash_red, 0, 0, 0);
                }
            }

            mDragPoint = new PointF();

            int x = 0, y = 0;
            View view = _view;
            do {
                x += view.getX();
                y += view.getY();
            } while ((view = (View) view.getParent()) != this);

            mItemOverlayRect.set(x, y, x + mDragTarget.getWidth(), y + mDragTarget.getHeight());
            _view.startDrag(_data, new EmptyDragShadowBuilder(_view), null, 0);
        } else {
            throw new IllegalArgumentException("Put view inside DragFrameLayout and/or set id");
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                mIsDragging = true;
                mIsAnimationStarted = true;
                mDragPoint.x = event.getX();
                mDragPoint.y = event.getY();
                invalidateShadowOffset();
                mAreaAnimator.start();
                mTrashBtnAnimator.start();
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                mDragPoint.x = event.getX();
                mDragPoint.y = event.getY();
                invalidateShadowOffset();
                if (!mIsAnimated) {
                    mNeedRedraw = true;
                    invalidate();
                }
                break;
            case DragEvent.ACTION_DROP:
                if (mItemDeleteListener != null && RectF.intersects(mDeleteArcRect, mBackgroundRect)) {
                    mDragTarget.setVisibility(INVISIBLE);
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    mItemDeleteListener.onDeleteItem(Integer.valueOf(item.getText().toString()));
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                TextView textView = (TextView) mDragTarget.getTag();
                if (textView != null) {
                    textView.setTextColor(mDefaultTextColor);
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trash_gray, 0, 0, 0);
                }
                mIsDragging = false;
                if (mIsAnimationStarted) {
                    mIsAnimationStarted = false;
                    mAreaAnimator.reverse();
                    mTrashBtnAnimator.reverse();
                }
                break;
        }

        return true;
    }

    public void setItemDeleteListener(OnItemDeleteListener _itemDeleteListener) {
        mItemDeleteListener = _itemDeleteListener;
    }

    private void invalidateShadowOffset() {
        //region Calc shadow position
        int translateX = Math.round(mDragPoint.x - mDragTarget.getWidth() * SHADOW_SCALE / 2);
        if (translateX < 0) {
            translateX = 0;
        } else if (translateX > (getWidth() - mDragTarget.getWidth() * SHADOW_SCALE)) {
            translateX = Math.round(getWidth() - mDragTarget.getWidth() * SHADOW_SCALE);
        }

        int translateY = Math.round(mDragPoint.y - mDragTarget.getHeight() * SHADOW_SCALE / 2);
        if (translateY < 0) {
            translateY = 0;
        } else if (translateY > getHeight() - mDragTarget.getHeight() * SHADOW_SCALE) {
            translateY = Math.round(getHeight() - mDragTarget.getHeight() * SHADOW_SCALE);
        }
        mBackgroundRect.set(translateX, translateY, translateX + mDragTarget.getWidth() * SHADOW_SCALE, translateY + mDragTarget.getHeight() * SHADOW_SCALE);

        invalidateShadowDrawable();
    }

    private RectShadowDrawable mRectShadowDrawable;

    private void invalidateShadowDrawable() {
        final int[] colors = new int[]{Color.GRAY, Color.TRANSPARENT};
        mRectShadowDrawable = new RectShadowDrawable(mBackgroundRect, Math.round(SHADOW_SIZE), colors);
    }

    private void invalidateDeleteArea(final int _width, final int _height) {
        float canvasOffset = _width * 0.3f;
        mDeleteArcRect.left = -canvasOffset;
        mDeleteArcRect.top = _height - mDeleteArcTop;
        mDeleteArcRect.right = _width + canvasOffset;
        mDeleteArcRect.bottom = _height + DELETE_ARC_HEIGHT - mDeleteArcTop;
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDragTarget != null && mNeedRedraw) {

            if (mIsDragging) {
                canvas.drawRect(mItemOverlayRect, mItemOverlayPaint);//draw overlay for original item background
            }
            drawDeleteArea(canvas);

            if (mIsDragging) {
                mRectShadowDrawable.draw(canvas);//draw shadow around of item
                canvas.drawRect(mBackgroundRect, mBackgroundPaint);//draw shadow background
                canvas.save();
                canvas.scale(SHADOW_SCALE, SHADOW_SCALE);
                canvas.translate(mBackgroundRect.left, mBackgroundRect.top / SHADOW_SCALE);
                mDragTarget.draw(canvas);//draw shadow view
                canvas.restore();
            }
            mNeedRedraw = false;
        }
    }

    private void drawDeleteArea(@NonNull Canvas canvas) {
        canvas.drawArc(mDeleteArcRect, START_DELETE_ARC_ANGLE, DELETE_ARC_SWEEP_ANGLE, true, mDeleteArcPaint);// draw delete area background

        canvas.save();
        float translateX = (canvas.getWidth() - mTrashBtn.getWidth()) / 2; //center horizontal
        float translateY = canvas.getHeight() + TRASH_BUTTON_TOP_OFFSET - mTrashBtnTop;
        canvas.translate(translateX, translateY);
        mTrashBtn.draw(canvas);
        canvas.restore();
    }

    public interface OnItemDeleteListener {
        void onDeleteItem(final int _position);
    }

    private static final class EmptyDragShadowBuilder extends DragShadowBuilder {

        public EmptyDragShadowBuilder(View _view) {
            super(_view);
        }

        @Override
        public final void onProvideShadowMetrics(@NonNull Point _shadowSize, @NonNull Point _shadowTouchPoint) {
            super.onProvideShadowMetrics(_shadowSize, _shadowTouchPoint);
        }

        @Override
        public final void onDrawShadow(@NonNull Canvas canvas) {
        }

    }

}
