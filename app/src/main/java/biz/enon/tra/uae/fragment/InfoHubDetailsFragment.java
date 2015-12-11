package biz.enon.tra.uae.fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.enon.tra.uae.R;
import com.squareup.picasso.Picasso;

import biz.enon.tra.uae.entities.ImageViewTarget;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.global.C;
import biz.enon.tra.uae.rest.model.response.GetAnnouncementsResponseModel;


/**
 * Created by ak-buffalo on 20.08.15.
 */
public class InfoHubDetailsFragment extends BaseFragment {

    private ImageView headerImage;
    private TextView headerDate;
    private TextView headerTitle;
    private TextView bodyFullDescription;

    private GetAnnouncementsResponseModel.Announcement infoHubAnnModel;

    public static InfoHubDetailsFragment newInstance(Bundle _bundle) {
        InfoHubDetailsFragment fragment = new InfoHubDetailsFragment();
        fragment.setArguments(_bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();
        infoHubAnnModel = getArguments().getParcelable(C.INFO_HUB_ANN_DATA);
    }

    @Override
    protected void initViews() {
        headerImage = findView(R.id.tvHeaderImage_FIHD);
        Picasso.with(getActivity()).load(infoHubAnnModel.image).placeholder(R.drawable.logo).into(new ImageViewTarget(headerImage));
        headerDate = findView(R.id.tvHeaderDate_FIHD);
        headerDate.setText(infoHubAnnModel.createdAt);
        headerTitle = findView(R.id.tvHeaderTitle_FIHD);
        headerTitle.setText(infoHubAnnModel.title);
        bodyFullDescription = findView(R.id.tvBodyDescr_FIHD);
        bodyFullDescription.setText(infoHubAnnModel.description);
    }

    @Override
    protected int getTitle() {
        return R.string.str_info_hub_announcement_details;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_info_hub_announcement_details;
    }
}