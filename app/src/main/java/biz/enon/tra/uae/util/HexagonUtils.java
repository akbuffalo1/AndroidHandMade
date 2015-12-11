package biz.enon.tra.uae.util;

import android.graphics.PointF;

/**
 * Created by Mikazme on 15/08/2015.
 */
public abstract class HexagonUtils {

    private HexagonUtils() {}

    public static boolean pointInPolygon(final PointF p, final PointF[] poly ) {
        PointF p1, p2;
        boolean inside = false;

        PointF oldPoint = new PointF(poly[poly.length - 1].x, poly[poly.length - 1].y);

        for (PointF point : poly) {
            PointF newPoint = new PointF(point.x, point.y);

            if (newPoint.x > oldPoint.x) {
                p1 = oldPoint;
                p2 = newPoint;
            } else {
                p1 = newPoint;
                p2 = oldPoint;
            }

            if ((newPoint.x < p.x) == (p.x <= oldPoint.x) && (p.y - p1.y) * (p2.x - p1.x)
                    < (p2.y - p1.y) * (p.x - p1.x)) {
                inside = !inside;
            }

            oldPoint = newPoint;
        }

        return inside;
    }
}
