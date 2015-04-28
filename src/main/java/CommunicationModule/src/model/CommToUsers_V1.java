package CommunicationModule.src.model;

import CommunicationModule.src.*;
import CommunicationModule.src.api.ICommToUsers_model;
import CommunicationModule.src.utilities.JSONResponseCreator;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NAOR on 06/04/2015.
 */
public class CommToUsers_V1 implements ICommToUsers_model {

    private boolean initialedByServer = false;
    JSONArray objToSend = null;
    ArrayList<String> targets = null;
    //C'tor - building up the JSON response to be sent when instantiated
    public CommToUsers_V1(HashMap<Integer,HashMap<String,String>> data,
                          ArrayList<String> target){
        targets = target;
        objToSend = new JSONResponseCreator().establishResponse(data);
    }
    public JSONArray SendResponse () {
        return objToSend;
    }
}
