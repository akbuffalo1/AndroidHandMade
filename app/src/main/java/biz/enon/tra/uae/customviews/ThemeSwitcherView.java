package biz.enon.tra.uae.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.enon.tra.uae.R;

import java.util.ArrayList;
import java.util.Map;

import biz.enon.tra.uae.baseentities.BaseCustomSwitcher;
import biz.enon.tra.uae.entities.RectButton;
import biz.enon.tra.uae.entities.Separator;
import biz.enon.tra.uae.entities.SeparatorFactory;
import biz.enon.tra.uae.global.H;

/**
 * Created by ak-buffalo on 27.07.15.
 */
public class ThemeSwitcherView extends BaseCustomSwitcher implements View.OnTouchListener {

    private RectButton mSelectedCircle;
    private final SeparatorFactory separatorFactory;
    public ThemeSwitcherView(Context context) {
        super(context);
        separatorFactory = new SeparatorFactory(getContext());
    }

    public ThemeSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        separatorFactory = new SeparatorFactory(getContext());
    }

    private String mCurrentTheme;

    @Override
    public <T> void initPreferences(T prefs) {
        mCurrentTheme = prefs.toString();
    }

    private Map<String, String> mColorsMap;

    @Override
    protected void initData(Context context, AttributeSet attrs) {
        mColorsMap = H.parseXmlToMap(getContext(), R.xml.themes);
    }

    private RectButton getRectButton(int circleDX, int circleDY, int color, String colorThema) {
        RectButton rectButton = new RectButton();
        rectButton.dX = circleDX;
        rectButton.dY = circleDY;
        int rectSide = getHeight() / 5;
        rectButton.rect.set(circleDX - rectSide, circleDY - rectSide, circleDX + rectSide, circleDY + rectSide);
        rectButton.colorThema = colorThema;
        rectButton.paint = new Paint();
        rectButton.paint.setStrokeWidth(5f);
        rectButton.paint.setColor(color);
        return rectButton;
    }

    @Override
    protected void initListeners() {
        setOnTouchListener(this);
    }

    private float getCircleDX(int width, int count, int position) {
        float first = position / (float) count;
        float second = (position + 1) / (float) count;
        float dX = width * (first + second) / 2f;
        return dX;
    }

    @Override
    public Type getType() {
        return Type.THEME;
    }

    @Override
    protected void bindView(View view) {/*Unimplemented method*/}

    @Override
    protected void unBindView(View view) {/*Unimplemented method*/}

    private int mContainerWidth;
    private ArrayList<RectButton> points;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContainerWidth = w;
        points = new ArrayList<>();
        int iter = 0;
        for (Map.Entry<String, String> entry : mColorsMap.entrySet()) {
            RectButton circlePoint = getRectButton(
                    Math.round(getCircleDX(mContainerWidth, mColorsMap.size(), iter)),
                    Math.round(h / 2),
                    Color.parseColor(entry.getKey()),
                    entry.getValue()
            );
            if (entry.getValue().equals(mCurrentTheme)) {
                circlePoint.isSelected = true;
                mSelectedCircle = circlePoint;
            } else {
                circlePoint.isSelected = false;
            }
            points.add(circlePoint);
            iter++;
        }
    }

    protected void onDraw(Canvas canvas) {
        int counter = 1;
        int themeCount = points.size();
        int sectorOffset = canvas.getWidth() / themeCount;
        int separatorPadding = getHeight() / 3;
        for (RectButton p : points) {
            canvas.drawRoundRect(p.rect, 1, 1, p.paint);
            if (p.isSelected){
                p.drawShadow(canvas);
            }
            Separator separator = new Separator(
                        getContext(),
                        getResources().getDimensionPixelSize(R.dimen.dp_authorization_fields_separator_height),
                        getHeight(),
                        R.color.hex_auth_fields_separator_color,
                        sectorOffset * counter
                );
            if (counter != themeCount){
                View separatorView = separatorFactory.createView(separator);
                LinearLayout layout = new LinearLayout(getContext());
                layout.setPadding(0, separatorPadding, 0, separatorPadding);
                layout.addView(separatorView);
                layout.measure(canvas.getWidth(), canvas.getHeight());
                layout.layout(0, 0, canvas.getWidth(), canvas.getHeight());
                layout.draw(canvas);
            }
            counter++;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
                handled = true;
                break;
            case MotionEvent.ACTION_UP:
                return handleClick(event.getX());
        }
        return handled;
    }

    private boolean handleClick(float dX) {
        boolean somethingInArea = false;
        for (RectButton point : points) {
            if (isInArea(point, dX)) {
                somethingInArea = true;
                if (point == mSelectedCircle) {
                    return false;
                }
                mSelectedCircle = point;
                point.isSelected = true;
                point.setPaintStyle(Paint.Style.STROKE);
                mSettingsChangeListener.onSettingsChanged(this, point.colorThema);
            }
        }

        if (somethingInArea) {
            for (RectButton point : points) {
                if (!isInArea(point, dX)) {
                    point.isSelected = false;
                }
            }
        }

        invalidate();
        return true;
    }

    private boolean isInArea(RectButton point, float x) {
        float halfSector = Float.valueOf(getWidth()) / points.size() / 2;
        return x >= point.dX - halfSector && x <= point.dX + halfSector;
    }

    @Override
    protected int getLayoutId() {
        return -1;
    }
}