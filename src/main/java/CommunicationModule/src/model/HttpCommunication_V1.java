package CommunicationModule.src.model;

import CommunicationModule.src.model.CommToUsers_V1;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */
public class HttpCommunication_V1 extends CommToUsers_V1 {

    public HttpCommunication_V1(HashMap<Integer,HashMap<String,String>> data,
                                ArrayList<String> target      ) {
        super(data,target);
    }
    @Override
    public void SendResponse () {
        //TODO
    }
}
