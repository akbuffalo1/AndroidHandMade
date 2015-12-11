package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.SmsReportRequestModel;
import biz.enon.tra.uae.rest.model.response.SmsSpamResponseModel;

/**
 * Created by Mikazme on 26/08/2015.
 */
public class SmsReportRequest extends BaseRequest<SmsSpamResponseModel, TRAServicesAPI> {

    private SmsReportRequestModel mSmsSpamReportModel;

    public SmsReportRequest(final SmsReportRequestModel _smsSpamModel) {
        super(SmsSpamResponseModel.class, TRAServicesAPI.class);
        mSmsSpamReportModel = _smsSpamModel;
    }

    @Override
    public SmsSpamResponseModel loadDataFromNetwork() throws Exception {
        return getService().reportSmsSpam(mSmsSpamReportModel);
    }
}
