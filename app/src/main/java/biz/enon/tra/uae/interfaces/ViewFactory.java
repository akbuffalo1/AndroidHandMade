package biz.enon.tra.uae.interfaces;

import android.view.View;

/**
 * Created by ak-buffalo on 7/26/15.
 */
public interface ViewFactory<T extends View,K>{
	T createView(K data);
	void prepareData(T view, K data);
}