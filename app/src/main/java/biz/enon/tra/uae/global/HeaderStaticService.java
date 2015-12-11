package biz.enon.tra.uae.global;

import biz.enon.tra.uae.customviews.HexagonalHeader;

/**
 * Created by ak-buffalo on 9/8/2015.
 */
public enum HeaderStaticService {
    INNOVATIONS(HexagonalHeader.HEXAGON_BUTTON_INNOVATIONS),
    SEARCH(HexagonalHeader.HEXAGON_BUTTON_SEARCH),
    NOTIFICATION(HexagonalHeader.HEXAGON_BUTTON_NOTIFICATION);

    private final int mServiceOrder;
    HeaderStaticService(int _serviceorder){
        mServiceOrder = _serviceorder;
    }
    
    public boolean equals(@HexagonalHeader.HexagonButton final int _hexagonButton){
        return mServiceOrder == _hexagonButton;
    }
}
