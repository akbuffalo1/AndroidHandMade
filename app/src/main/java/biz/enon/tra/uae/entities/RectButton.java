package biz.enon.tra.uae.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by ak-buffalo on 29.07.15.
 */
public class RectButton {
    public boolean isSelected;
    public int dX;
    public int dY;
    public String colorThema;
    public Paint paint;
    public RectF rect = new RectF();
    public RectF shadow;
    public Paint shadowpaint;

    public final void setPaintStyle(final Paint.Style _paintStyle) {
        if (paint == null) {
            paint = new Paint();
        }
        paint.setStyle(_paintStyle);
    }
    
    public void drawShadow(Canvas _canvas){
        if (shadowpaint == null) {
            shadowpaint = new Paint();
        }
        shadowpaint = new Paint();
        shadowpaint.setStrokeWidth(10f);
        shadowpaint.setColor(Color.LTGRAY);
        shadowpaint.setStyle(Paint.Style.STROKE);
        shadow = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        _canvas.drawRoundRect(shadow, 2, 2, shadowpaint);
    }
}
