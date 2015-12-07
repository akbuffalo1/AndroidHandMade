package com.uae.tra_smart_services.fragment;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.dialog.AlertDialogFragment;
import com.uae.tra_smart_services.fragment.base.BaseServiceFragment;
import com.uae.tra_smart_services.global.C;
import com.uae.tra_smart_services.global.Service;
import com.uae.tra_smart_services.rest.model.response.DomainInfoCheckResponseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public class DomainInfoFragment extends BaseServiceFragment implements AlertDialogFragment.OnOkListener, LoaderManager.LoaderCallbacks<Map<String, String>> {

    private TextView tvDomainName_FDI;
    private TextView tvRegisterId_FDI;
    private TextView tvRegisterName_FDI;
    private TextView tvStatus_FDI;
    private TextView tvRegContactId_FDI;
    private TextView tvRegContactName_FDI;

    public static DomainInfoFragment newInstance(final DomainInfoCheckResponseModel _domainInfo) {
        final DomainInfoFragment fragment = new DomainInfoFragment();
        final Bundle args = new Bundle();
        args.putParcelable(C.DOMAIN_INFO, _domainInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rate, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem;
        if((menuItem = menu.findItem(R.id.action_show_info)) != null){
            menuItem.setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Nullable
    @Override
    protected Service getServiceType() {
        return Service.DOMAIN_CHECK_INFO;
    }

    @Override
    protected String getServiceName() {
        return C.RATE_NAME_DOMAIN_INFO;//"Domain info";
    }

    @Override
    protected void initData() {
        super.initData();
        getLoaderManager().initLoader(0, getArguments(), this).forceLoad();
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvDomainName_FDI = findView(R.id.tvDomainName_FDI);
        tvRegisterId_FDI = findView(R.id.tvRegisterId_FDI);
        tvRegisterName_FDI = findView(R.id.tvRegisterName_FDI);
        tvStatus_FDI = findView(R.id.tvStatus_FDI);
        tvRegContactId_FDI = findView(R.id.tvRegContactId_FDI);
        tvRegContactName_FDI = findView(R.id.tvRegContactName_FDI);
    }

    @Override
    protected int getTitle() {
        return R.string.fragment_domain_info_title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_domain_info;
    }

    @Override
    public void onOkPressed(final int _mMessageId) {
        // Unimplemented method
        // Used exceptionally to specify OK button in dialog
    }

    @Override
    public DomainDataParser onCreateLoader(int id, Bundle args) {
        return new DomainDataParser(getActivity(), (DomainInfoCheckResponseModel) args.getParcelable(C.DOMAIN_INFO));
    }

    @Override
    public void onLoadFinished(Loader<Map<String, String>> loader, Map<String, String> data) {
        tvDomainName_FDI.setText(data.get("Domain Name"));
        tvRegisterId_FDI.setText(data.get("Registrar ID"));
        tvRegisterName_FDI.setText(data.get("Registrar Name"));
        tvStatus_FDI.setText(data.get("Status"));
        tvRegContactId_FDI.setText(data.get("Registrant Contact ID"));
        tvRegContactName_FDI.setText(data.get("Registrant Contact Name"));
    }

    @Override
    public void onLoaderReset(Loader loader) {
        loader.reset();
    }

    @Override
    public void onLoadingCanceled() {
        // Not implemented
    }

    private static class DomainDataParser extends AsyncTaskLoader<Map<String, String>> {
        private DomainInfoCheckResponseModel mModel;

        public DomainDataParser(Context _context, DomainInfoCheckResponseModel _model) {
            super(_context);
            mModel = _model;
        }

        @Override
        public Map<String, String> loadInBackground() {
            return new HashMap<String, String>() {
                {
                    for (String line : mModel.urlData.split("\\r\\n")) {
                        String[] keyvaluePair = line.split(":");
                        if (keyvaluePair.length == 2) {
                            put(keyvaluePair[0].trim(), keyvaluePair[1].trim());
                        }
                    }
                }
            };
        }
    }
}
