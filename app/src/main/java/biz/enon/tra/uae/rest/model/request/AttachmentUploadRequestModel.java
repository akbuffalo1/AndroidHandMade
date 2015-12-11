package biz.enon.tra.uae.rest.model.request;

import com.google.gson.annotations.Expose;

/**
 * Created by mobimaks on 04.11.2015.
 */
public class AttachmentUploadRequestModel {

    @Expose
    public final String attachment;

    public AttachmentUploadRequestModel(String _attachment) {
        attachment = _attachment;
    }
}
