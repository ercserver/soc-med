package CommunicationModule.src.model;

import CommunicationModule.src.model.CommToUsers_V1;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */
public class InitiatedHTTPCommunication_V1 extends CommToUsers_V1 {

    private String communicateToURL = null;

    public InitiatedHTTPCommunication_V1(HashMap<Integer,HashMap<String,String>> data,
                                ArrayList<String> target, String communicateTo      ) {
        super(data,target);
        communicateToURL = communicateTo;
    }

    public JSONArray sendResponse () {





        return null;
    }

}
