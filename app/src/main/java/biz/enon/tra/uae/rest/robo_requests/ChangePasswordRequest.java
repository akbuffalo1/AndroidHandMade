package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.ChangePasswordModel;
import retrofit.client.Response;

/**
 * Created by mobimaks on 04.10.2015.
 */
public class ChangePasswordRequest extends BaseRequest<Response, TRAServicesAPI> {

    private final ChangePasswordModel mChangePasswordModel;

    public ChangePasswordRequest(final ChangePasswordModel _changePasswordModel) {
        super(Response.class, TRAServicesAPI.class);
        mChangePasswordModel = _changePasswordModel;
    }

    public ChangePasswordRequest(final String _oldPassword, final String _newPassword) {
        super(Response.class, TRAServicesAPI.class);
        mChangePasswordModel = new ChangePasswordModel();
        mChangePasswordModel.oldPassword = _oldPassword;
        mChangePasswordModel.newPassword = _newPassword;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().changePassword(mChangePasswordModel);
    }
}
