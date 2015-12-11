package biz.enon.tra.uae.global;

/**
 * Created by ak-buffalo on 23.10.15.
 */
public class QueryAdapter {
    public static OffsetQuery pageToOffset(int _page, int _count){
        return new OffsetQuery(--_page * _count, _count);
    }

    public static class OffsetQuery {
        public int offset;
        public int limit;

        public OffsetQuery(int _offset, int _limit) {
            offset = _offset;
            limit = _limit;
        }
    }
}