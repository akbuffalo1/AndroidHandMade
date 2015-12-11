package biz.enon.tra.uae.interfaces;

import com.octo.android.robospice.SpiceManager;

import biz.enon.tra.uae.global.C;

/**
 * Created by mobimaks on 28.09.2015.
 */
public interface SpiceLoader {
    SpiceManager getSpiceManager(@C.SpiceManager int _spiceManager);
}
