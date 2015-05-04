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

    public CommToUsers_V1(HashMap<Integer, HashMap<String, String>> data) {
    }

    public JSONArray sendResponse () {
        return objToSend;
    }
}
