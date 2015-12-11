package biz.enon.tra.uae.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import biz.enon.tra.uae.R;

/**
 * Created by ak-buffalo on 14.09.15.
 */
public class ServiceRatingView extends LinearLayout implements OnClickListener {

    private ArrayList<CheckableHexagonView> ratingButtons = new ArrayList<>();
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

        ratingButtons.add((CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_1_FDC));
        ratingButtons.add((CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_2_FDC));
        ratingButtons.add((CheckableHexagonView) findViewById(R.id.hvDomainCheckRating_3_FDC));
        etFeedBack = (EditText) findViewById(R.id.etFeedBack_LSR);
    }

    private void initListeners() {
        for(CheckableHexagonView button : ratingButtons){
            button.setOnClickListener(this);
        }
    }

    public Object[] getRating(){
        for(CheckableHexagonView button : ratingButtons){
            if(button.isChecked()){
                return new Object[]{Integer.valueOf((String) button.getTag()), etFeedBack.getText().toString()};
            }
        }
        return null;
    }

    @Override
    public void onClick(View _view) {
        switch (_view.getId()){
            case R.id.hvDomainCheckRating_1_FDC:
            case R.id.hvDomainCheckRating_2_FDC:
            case R.id.hvDomainCheckRating_3_FDC:
                for(CheckableHexagonView button : ratingButtons){
                    button.setChecked(false);
                    if(button.getId() == _view.getId()){
                        button.setChecked(true);
                    }
                }
                break;
        }
    }
}