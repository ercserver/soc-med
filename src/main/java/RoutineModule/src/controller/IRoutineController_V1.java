package RoutineModule.src.controller;

import CommunicationModule.src.api.ICommController;
import RoutineModule.src.api.ILocation_model;
import RoutineModule.src.api.IRoutineController;

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
        return location.transferLocation(data);
    }
}
