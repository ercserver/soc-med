package RoutineModule.src.controller;

import CommunicationModule.src.api.ICommController;
import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import RoutineModule.src.api.ILocation_model;
import RoutineModule.src.api.IRoutineController;
import RoutineModule.src.api.IUpdates;
import RoutineModule.src.api.IUpdates_model;
import Utilities.ModelsFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 28/04/2015.
 */
public class RoutineController_V1 implements IRoutineController {
    private ICommController commController = null;
    private IUpdates_model updates = null;

    public RoutineController_V1()
    {
        ModelsFactory models = new ModelsFactory();
        commController = models.determineCommControllerVersion();
        updates = models.determineIUpdatesVersion();
    }

    public Object transferLocation(HashMap<String, String> data)
    {
        // just transfer the location data to the GIS
        HashMap<Integer,HashMap<String,String>> response = new HashMap<Integer,HashMap<String,String>>();
        response.put(1, data);
        ArrayList<String> target = new ArrayList<String>();
        target.add("url...");//*
        commController.setCommToUsers(response, target, true);
        return commController.sendResponse();
    }

    public Object getUpdatesFields(HashMap<String, String> data)
    {
        // ToDo-need to verify cmid and password
        HashMap<Integer,HashMap<String,String>> fields = updates.getFieldsForUpdate(data);

        ArrayList<String> target = new ArrayList<String>();
        // Sign in of doctor/ems
        if(data.get("reg_id") == "0")
        {
            commController.setCommToUsers(fields, null, false);
        }
        // Sign-in of patient
        else
        {
            target.add(data.get("reg_id"));
            commController.setCommToUsers(fields, target, false);
        }
        // Sends response to the proper user
        return commController.sendResponse();
    }
}
