package biz.enon.tra.uae.rest.robo_requests;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.UserNameModel;
import biz.enon.tra.uae.rest.model.response.UserProfileResponseModel;
import biz.enon.tra.uae.util.ImageUtils;

/**
 * Created by mobimaks on 03.10.2015.
 */
public class ChangeUserProfileRequest extends BaseRequest<UserProfileResponseModel, TRAServicesAPI> {

    private final Context mContext;
    private final UserNameModel mNameModel;

    public ChangeUserProfileRequest(@NonNull final Context _context,
                                    @NonNull final UserNameModel _userNameModel) {
        super(UserProfileResponseModel.class, TRAServicesAPI.class);
        mContext = _context;
        mNameModel = _userNameModel;
    }

    @Override
    public UserProfileResponseModel loadDataFromNetwork() throws Exception {
        try {
            if (mNameModel.imageUri != null) {
                mNameModel.imageBase64 = ImageUtils.imageToBase64(mContext.getContentResolver(), mNameModel.imageUri);
            }
            return getService().editUserProfile(mNameModel);
        } catch (IOException e) {
            throw new Exception("Can't load image from device");
        }
    }
}
