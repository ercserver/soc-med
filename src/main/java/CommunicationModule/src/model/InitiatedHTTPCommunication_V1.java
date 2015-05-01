package CommunicationModule.src.model;

import org.json.JSONArray;
import org.jsoup.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */
public class InitiatedHTTPCommunication_V1 extends CommToUsers_V1 {



    private String communicateToURL = "www.Gis.com";
    private String username = "ImSoCool";
    private String password = "MyPWisCoolAlso";

    public InitiatedHTTPCommunication_V1(HashMap<Integer,HashMap<String,String>> data, ArrayList<String> target) {
        super(data,target);
        /*TODO - Is this needed? Or do we assume we don't need to send the details here. If we do - perhaps HashMap rather than ArrayList?
        communicateToURL = target.get(0);
        username = target.get(1);
        password = target.get(2);
        */
    }
    public JSONArray sendResponse () {
        try {
            Jsoup.connect(communicateToURL)
                    .data("username", username)
                    .data("password", password)
                    .data("JSONFile", objToSend.toString())
                    .method(Connection.Method.POST).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
