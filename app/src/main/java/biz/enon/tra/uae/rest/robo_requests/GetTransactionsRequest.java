package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.response.GetTransactionResponseModel;

/**
 * Created by Mikazme on 05/10/2015.
 */
public class GetTransactionsRequest extends BaseRequest<GetTransactionResponseModel.List, TRAServicesAPI> {

    private int mPage, mCount, mAsc;

    public GetTransactionsRequest(final int _page, final int _count) {
        super(GetTransactionResponseModel.List.class, TRAServicesAPI.class);
        mPage = _page;
        mCount = _count;
    }

    @Override
    public GetTransactionResponseModel.List loadDataFromNetwork() throws Exception {
        return getService().getTransactions(mPage, mCount);
    }
}