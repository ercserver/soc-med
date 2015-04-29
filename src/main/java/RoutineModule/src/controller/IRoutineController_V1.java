package RoutineModule.src.controller;

import CommunicationModule.src.api.ICommController;
import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import RoutineModule.src.api.ILocation_model;
import RoutineModule.src.api.IRoutineController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 28/04/2015.
 */
public class IRoutineController_V1 implements IRoutineController {
    private ICommController commController = null;//*
    private ILocation_model location = null;

    public IRoutineController_V1() {//to do}
    }

    public Object transferLocation(HashMap<String, String> data)
    {
        HashMap<Integer,HashMap<String,String>> response = new HashMap<Integer,HashMap<String,String>>();
        response.put(1, data);
        ArrayList<String> target = new ArrayList<String>();
        target.add("url...");//*
        commController.setCommToUsers(response, target, true);
        return commController.sendResponse();
    }
}
