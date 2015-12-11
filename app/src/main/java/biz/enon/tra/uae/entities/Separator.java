package biz.enon.tra.uae.entities;

import android.content.Context;

/**
 * Created by ak-buffalo on 7/26/15.
 */
public class Separator {

	private Context context;
	public int dX = -1;
	private int width;
	private int height;
	private int color;

	private Separator(Context context){
		this.context = context;
	}

	public Separator(Context contex, int width, int height, int color) {
		this(contex);
		this.width = width;
		this.height = height;
		this.color = color;
	}

	public Separator(Context contex, int width, int height, int color, int _dX) {
		this(contex, width, height, color);
		dX = _dX;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getColor() {
		return context.getResources().getColor(color);
	}
}