package CommunicationModule.src.model;

import CommunicationModule.src.*;
import CommunicationModule.src.api.ICommToUsers_model;
import CommunicationModule.src.utilities.JSONResponseCreator;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public abstract class CommToUsers_V1 implements ICommToUsers_model {

    JSONArray objToSend = null;
    String sendToCmid = null;
    //C'tor - building up the JSON response to be sent when instantiated
    public CommToUsers_V1(HashMap<Integer,HashMap<String,String>> data){
        sendToCmid = data.get(1).get("SendToCmid");
        objToSend = new JSONResponseCreator().establishResponse(data);
    }
}
