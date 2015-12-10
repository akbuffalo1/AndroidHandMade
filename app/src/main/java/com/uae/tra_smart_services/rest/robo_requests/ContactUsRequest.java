package com.uae.tra_smart_services.rest.robo_requests;

import android.support.annotation.NonNull;

import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse.List;

import java.util.Locale;

/**
 * Created by mobimaks on 02.12.2015.
 */
public class ContactUsRequest extends BaseRequest<List, TRAServicesAPI> {

    private final String mLanguage;

    public ContactUsRequest() {
        super(List.class, TRAServicesAPI.class);
        mLanguage = Locale.getDefault().getLanguage().toUpperCase();
    }

    public ContactUsRequest(@NonNull final String _language) {
        super(List.class, TRAServicesAPI.class);
        mLanguage = _language.toUpperCase();
    }

    @Override
    public List loadDataFromNetwork() throws Exception {
        return getService().getContactUsInfo(mLanguage);
    }
}
