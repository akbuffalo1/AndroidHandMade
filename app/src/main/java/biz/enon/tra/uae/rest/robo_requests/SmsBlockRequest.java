package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.SmsBlockRequestModel;
import biz.enon.tra.uae.rest.model.response.SmsSpamResponseModel;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public class SmsBlockRequest extends BaseRequest<SmsSpamResponseModel, TRAServicesAPI> {

    private SmsBlockRequestModel mSmsSpamReportModel;

    public SmsBlockRequest(final SmsBlockRequestModel _smsSpamModel) {
        super(SmsSpamResponseModel.class, TRAServicesAPI.class);
        mSmsSpamReportModel = _smsSpamModel;
    }

    @Override
    public final SmsSpamResponseModel loadDataFromNetwork() throws Exception {
        return getService().blockSmsSpam(mSmsSpamReportModel);
    }
}

