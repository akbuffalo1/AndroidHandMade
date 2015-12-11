package biz.enon.tra.uae.rest.model.request;

import com.google.gson.annotations.Expose;

/**
 * Created by Mikazme on 12/08/2015.
 */
public class HelpSalimModel {
    public HelpSalimModel(String url, String description) {
        this.url = url;
        this.description = description;
    }

    @Expose
    public String url;
    @Expose
    public String description;
}
