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
public class ServiceRatingView extends LinearLayout implements OnClickListener {

    private CheckableHexagonView hvBad;
    private CheckableHexagonView hvNeut, hvGood;
    private int mRate = 0;
    private EditText etFeedBack;
    public static int MODE = 1;

    public ServiceRatingView(Context context) { this(context, null); }

    public ServiceRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniLayout();
        initViews();
        initListeners();
    }

    private void iniLayout() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void initViews() {
        inflate(getContext(), MODE > 0 ? R.layout.layout_service_rating_loader : R.layout.layout_service_rating_popup, this);

        hvBad = (CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_1_FDC);
        hvNeut = (CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_2_FDC);
        hvGood = (CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_3_FDC);
        etFeedBack = (EditText) findViewById(R.id.etFeedBack_LSR);
    }

    private void initListeners() {
        hvBad.setOnClickListener(this);
        hvNeut.setOnClickListener(this);
        hvGood.setOnClickListener(this);
    }

    public Object[] getRating(){
        return new Object[]{mRate, etFeedBack.getText().toString()};
    }

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
}