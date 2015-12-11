package biz.enon.tra.uae.rest.robo_requests;

import biz.enon.tra.uae.rest.TRAServicesAPI;
import biz.enon.tra.uae.rest.model.request.PostInnovationRequestModel;
import retrofit.client.Response;

/**
 * Created by ak-buffalo on 06.10.15.
 */
public class PostInnovationRequest extends BaseRequest<Response, TRAServicesAPI> {
    private PostInnovationRequestModel model;

    public PostInnovationRequest(PostInnovationRequestModel _model) {
        super(Response.class, TRAServicesAPI.class);
        model = _model;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        return getService().postInnovation(model);
    }
}