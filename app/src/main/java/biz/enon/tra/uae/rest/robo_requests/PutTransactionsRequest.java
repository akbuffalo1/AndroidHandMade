package biz.enon.tra.uae.rest.robo_requests;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.IOException;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.response.TransactionModel;
import biz.enon.tra.uae.util.ImageUtils;
import retrofit.client.Response;

/**
 * Created by and on 16.12.15.
 */

public class PutTransactionsRequest extends BaseRequest<Response, TRAServicesAPI>  {
    private final TransactionModel mtransactionModel;
    private final ContentResolver mContentResolver;
    private final Uri mImageUri;

    public PutTransactionsRequest(final TransactionModel _transactionModel, final Context _context, @Nullable final Uri _imageUri) {
        super(Response.class, TRAServicesAPI.class);
        mtransactionModel = _transactionModel;
        mContentResolver = _context.getApplicationContext().getContentResolver();
        mImageUri = _imageUri;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        try {
            if (mImageUri != null) {
                mtransactionModel.attachment = ImageUtils.imageToBase64(mContentResolver, mImageUri);
            }
            return getService().putTransactions(mtransactionModel._id, mtransactionModel);
        } catch (IOException e) {
            throw new Exception("Can't load image from device");
        }
    }
}
