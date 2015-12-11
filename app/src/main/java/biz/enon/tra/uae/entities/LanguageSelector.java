package biz.enon.tra.uae.entities;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by ak-buffalo on 7/26/15.
 */

public class LanguageSelector{
	private String text;
	private String tag;
	private @IdRes int style;
	private @IdRes int textColor;
	private View.OnClickListener hadler;

	public LanguageSelector(String text, String tag, int style, int textColor, View.OnClickListener hadler) {
		this.text = text;
		this.tag = tag;
		this.style = style;
		this.textColor = textColor;
		this.hadler = hadler;
	}

	public String getText() {
		return text;
	}

	public String getTag() {
		return tag;
	}

	public int getStyle() {
		return style;
	}

	public int getTextColor() {
		return textColor;
	}

	public View.OnClickListener getHadler() {
		return hadler;
	}
}