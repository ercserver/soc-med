package CommunicationModule.src.model;

import CommunicationModule.src.*;
import CommunicationModule.src.api.ICommToUsers_model;
import CommunicationModule.src.utilities.JSONResponseCreator;
import org.json.JSONObject;


import java.util.ArrayList;

/**
 * Created by NAOR on 06/04/2015.
 */
public abstract class CommToUsers_V1 implements ICommToUsers_model {

    JSONObject objToSend = null;

    //C'tor - building up the JSON response to be sent when instantiated
    public CommToUsers_V1(ArrayList<ArrayList<String>> data){
        objToSend = new JSONResponseCreator().establishResponse(data);
    }
}
