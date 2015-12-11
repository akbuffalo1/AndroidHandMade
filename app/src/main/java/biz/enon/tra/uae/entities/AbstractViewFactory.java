package biz.enon.tra.uae.entities;

import android.content.Context;
import android.view.View;

import biz.enon.tra.uae.interfaces.ViewFactory;

/**
 * Created by ak-buffalo on 7/26/15.
 */
public abstract class AbstractViewFactory<T extends View,K> implements ViewFactory<T,K> {
	protected Context mContext;

	protected AbstractViewFactory(Context _context){
		mContext = _context;
	}
}