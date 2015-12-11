package biz.enon.tra.uae.entities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ak-buffalo on 7/26/15.
 */
public class SeparatorFactory extends AbstractViewFactory<View, Separator> {
	public SeparatorFactory(Context context) {
		super(context);
	}

	@Override
	public View createView(Separator entity) {
		View separatorView = new View(mContext);
		prepareData(separatorView, entity);
		return separatorView;
	}

	@Override
	public void prepareData(View separator, Separator entity) {
		ViewGroup.LayoutParams param1 = new ViewGroup.LayoutParams(entity.getWidth(), entity.getHeight());
		separator.setBackgroundColor(entity.getColor());
		separator.setLayoutParams(param1);
		if(entity.dX != -1){
			separator.setX(entity.dX);
		}
	}
}
