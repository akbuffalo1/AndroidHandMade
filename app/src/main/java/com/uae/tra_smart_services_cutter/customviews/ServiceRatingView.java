package com.uae.tra_smart_services_cutter.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uae.tra_smart_services_cutter.R;
import com.uae.tra_smart_services_cutter.interfaces.LoaderMarker;

/**
 * Created by ak-buffalo on 14.09.15.
 */
public class ServiceRatingView extends LinearLayout implements View.OnClickListener{

    private ImageView rbBad, rbNeut, rbGood;
    private CallBacks mCallBacks;
    public ServiceRatingView(Context context) {
        this(context, null);
    }

    public ServiceRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniLayout();
        initViews();
        initListeners();
    }

    public void init(CallBacks _callBacks){
        mCallBacks = _callBacks;
    }

    private void iniLayout(){
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initViews(){
        inflate(getContext(), R.layout.layout_service_rating, this);

        rbBad = (ImageView) findViewById(R.id.rbDomainCheckRating_1_FDC);
//        rbBad.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_bad_line)));
        rbNeut = (ImageView) findViewById(R.id.rbDomainCheckRating_2_FDC);
//        rbNeut.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_neut_line)));
        rbGood = (ImageView) findViewById(R.id.rbDomainCheckRating_3_FDC);
//        rbGood.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_good_line)));
    }

    private void initListeners(){
        rbBad.setOnClickListener(this);
        rbNeut.setOnClickListener(this);
        rbGood.setOnClickListener(this);
    }

    @Override
    public void onClick(View _view) {
        if(mCallBacks != null)
            mCallBacks.onRate(Integer.valueOf(_view.getTag().toString()));
    }

    public interface CallBacks extends LoaderMarker {
        void onRate(int _rate);
    }
}