package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.interfaces.LoaderMarker;

/**
 * Created by ak-buffalo on 14.09.15.
 */
public class ServiceRatingView extends LinearLayout implements OnClickListener {

    private HexagonView hvBad, hvNeut, hvGood;
    private CallBacks mCallBacks;

    public ServiceRatingView(Context context) {
        this(context, null);
    }

    public ServiceRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniLayout();
        initViews();
//        initListeners();
    }

    public void init(CallBacks _callBacks) {
        mCallBacks = _callBacks;
    }

    private void iniLayout() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initViews() {
        inflate(getContext(), R.layout.layout_service_rating, this);

        hvBad = (HexagonView) findViewById(R.id.hvDomainCheckRating_1_FDC);
//        hvBad.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_bad_line)));
        hvNeut = (HexagonView) findViewById(R.id.hvDomainCheckRating_2_FDC);
//        hvNeut.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_neut_line)));
        hvGood = (HexagonView) findViewById(R.id.hvDomainCheckRating_3_FDC);
//        hvGood.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_good_line)));
    }

    private void initListeners() {
        hvBad.setOnClickListener(this);
        hvNeut.setOnClickListener(this);
        hvGood.setOnClickListener(this);
    }

    @Override
    public void onClick(View _view) {
        if (_view.getId() == R.id.btnSendRating_LSR && mCallBacks != null)
            mCallBacks.onRate(Integer.valueOf(_view.getTag().toString()));
    }

    public interface CallBacks extends LoaderMarker {
        void onRate(int _rate);
    }
}