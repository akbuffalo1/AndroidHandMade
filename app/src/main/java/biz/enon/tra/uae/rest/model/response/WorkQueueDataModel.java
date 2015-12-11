package biz.enon.tra.uae.rest.model.response;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ak-buffalo on 20.11.15.
 */
public class WorkQueueDataModel {
    public Integer order;
    public String name;
    public String inputType;
    public HashMap<String, String> additional;
    public boolean required;
    public String validateAs;
    public String _id;
    public ArrayList<HashMap<String,String>> dataSource;
    public HashMap<String, String> displayName;
    public HashMap<String, String> placeHolder;
    public ArrayList<HashMap<String, String>> dataContent;
}