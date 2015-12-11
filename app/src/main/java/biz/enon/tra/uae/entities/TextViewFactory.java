package biz.enon.tra.uae.entities;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import biz.enon.tra.uae.R;

/**
 * Created by ak-buffalo on 7/26/15.
 */
public class TextViewFactory extends AbstractViewFactory<TextView, LanguageSelector>{

	public TextViewFactory(Context context) {
		super(context);
	}

	@Override
	public TextView createView(LanguageSelector entity) {
		TextView textView = new TextView(mContext);
		prepareData(textView, entity);
		return textView;
	}

	@Override
	public void prepareData(TextView textView, LanguageSelector entity) {
		textView.setText(entity.getText());
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTag(entity.getTag());
		int paddingSide = mContext.getResources().getDimensionPixelSize(R.dimen.dp_divider_padding_side);
		textView.setPadding(paddingSide, textView.getPaddingTop(), paddingSide, textView.getPaddingBottom());
		textView.setTextAppearance(mContext, entity.getStyle());
		textView.setTextColor(mContext.getResources().getColor(entity.getTextColor()));
		textView.setOnClickListener(entity.getHadler());
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
		);
		textView.setLayoutParams(params);
	}
}