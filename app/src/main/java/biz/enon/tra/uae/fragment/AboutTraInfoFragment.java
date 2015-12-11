package biz.enon.tra.uae.fragment;

import android.annotation.SuppressLint;
import android.webkit.WebView;

import com.enon.tra.uae.R;

import biz.enon.tra.uae.fragment.base.BaseFragment;

/**
 * Created by mobimaks on 16.09.2015.
 */
public class AboutTraInfoFragment extends BaseFragment {

    private WebView wvWebView;

    public static AboutTraInfoFragment newInstance() {
        return new AboutTraInfoFragment();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {
        super.initViews();
        wvWebView = findView(R.id.wvText_FAT);
        wvWebView.getSettings().setJavaScriptEnabled(true);
        wvWebView.loadDataWithBaseURL(null, getString(R.string.fragment_about_tra_html), "text/html", "utf-8", null);
    }

    @Override
    protected int getTitle() {
        return 0;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_about_info_tra;
    }

}
