package RoutineModule.src.model;

import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import RoutineModule.src.api.ILocation_model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 28/04/2015.
 */
public class Location_V1 implements ILocation_model {

    public Object transferLocation(HashMap<String, String> data)
    {
        HashMap<Integer,HashMap<String,String>> response = new HashMap<Integer,HashMap<String,String>>();
        response.put(1, data);
        ArrayList<String> target = new ArrayList<String>();
        target.add("url...");//*
        CommToUsers_V1 comm = new CommToUsersFactory_V1().createComm(response, target, true);
        return comm.sendResponse();
    }
}
