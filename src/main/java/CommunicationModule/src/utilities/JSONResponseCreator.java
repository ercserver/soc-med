package CommunicationModule.src.utilities;

import CommunicationModule.src.api.IResponseCreator;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class JSONResponseCreator implements IResponseCreator {
    public JSONArray establishResponse(HashMap<Integer,HashMap<String,String>> data) {
        //TODO
        JSONArray objsToSend = new JSONArray();
        for (Map.Entry<Integer,HashMap<String,String>> objs : data.entrySet()){
            HashMap<String,String> obj = objs.getValue();
            JSONObject currJson = new JSONObject(obj);
            objsToSend.put(obj);
        }
        return objsToSend;
    }
}
