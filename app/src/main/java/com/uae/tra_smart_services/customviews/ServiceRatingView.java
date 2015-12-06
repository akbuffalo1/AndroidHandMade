package com.uae.tra_smart_services.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.uae.tra_smart_services.R;

/**
 * Created by ak-buffalo on 14.09.15.
 */
public class ServiceRatingView extends LinearLayout implements OnClickListener, View.OnLayoutChangeListener {

    private CheckableHexagonView hvBad, hvNeut, hvGood;
    private EditText etFeedBack;

    public ServiceRatingView(Context context) {
        this(context, null);
    }

    public ServiceRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        iniLayout();
//        initViews();
        addOnLayoutChangeListener(this);
//        initListeners();
    }
    int mMode;
    public void init(int _mode){
        mMode = _mode;
    }

    private void iniLayout() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void initViews() {
        inflate(getContext(), mMode > 0 ? R.layout.layout_service_rating_loader : R.layout.layout_service_rating_popup, this);

        hvBad = (CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_1_FDC);
//        hvBad.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_bad_line)));
        hvNeut = (CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_2_FDC);
//        hvNeut.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_neut_line)));
        hvGood = (CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_3_FDC);
//        hvGood.setImageDrawable(ImageUtils.getFilteredDrawable(getContext(), ContextCompat.getDrawable(getContext(), R.drawable.btn_good_line)));
        etFeedBack = (EditText) findViewById(R.id.etFeedBack_LSR);
    }

    private void initListeners() {
        hvBad.setOnClickListener(this);
        hvNeut.setOnClickListener(this);
        hvGood.setOnClickListener(this);
    }

    public Object[] getRating(){
        return new Object[]{mRate, etFeedBack};
    }

    private int mRate = 3;
    @Override
    public void onClick(View _view) {
        switch (_view.getId()){
            case R.id.hvDomainCheckRating_1_FDC:
            case R.id.hvDomainCheckRating_2_FDC:
            case R.id.hvDomainCheckRating_3_FDC:
                mRate = Integer.valueOf(_view.getTag().toString());
                break;
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        iniLayout();
        initViews();
        removeOnLayoutChangeListener(this);
    }
}