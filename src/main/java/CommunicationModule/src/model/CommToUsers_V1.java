package CommunicationModule.src.model;

import CommunicationModule.src.api.ICommToUsers_model;
import CommunicationModule.src.utilities.JSONResponseCreator;
import org.json.JSONArray;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class CommToUsers_V1 implements ICommToUsers_model {
    //members
    JSONArray objToSend = null;
    ArrayList<String> targets = null;

    //C'tor - building up the JSON response to be sent when instantiated
    public CommToUsers_V1(HashMap<Integer,HashMap<String,String>> data){
        objToSend = new JSONResponseCreator().establishResponse(data);
    }
    public JSONArray sendResponse () {
        return objToSend;
    }
}
