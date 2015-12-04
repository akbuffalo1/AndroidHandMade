package com.uae.tra_smart_services.rest.robo_requests;

import com.uae.tra_smart_services.rest.TRAServicesAPI;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse.List;

/**
 * Created by mobimaks on 02.12.2015.
 */
public class ContactUsRequest extends BaseRequest<List, TRAServicesAPI> {

    public ContactUsRequest() {
        super(List.class, TRAServicesAPI.class);
    }

    @Override
    public List loadDataFromNetwork() throws Exception {
        return getService().getContactUsInfo();
    }
}
