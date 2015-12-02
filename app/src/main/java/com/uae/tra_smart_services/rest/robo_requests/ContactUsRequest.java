package com.uae.tra_smart_services.rest.robo_requests;

import com.uae.tra_smart_services.rest.DynamicServicesApi;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse.List;

/**
 * Created by mobimaks on 02.12.2015.
 */
public class ContactUsRequest extends BaseRequest<List, DynamicServicesApi> {

    public ContactUsRequest() {
        super(List.class, DynamicServicesApi.class);
    }

    @Override
    public List loadDataFromNetwork() throws Exception {
        return getService().getContactUsInfo();
    }
}
