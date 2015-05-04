package CommunicationModule.src.model;

import CommunicationModule.src.api.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */

public class CommToUsersFactory_V1 implements ICommToUsersFactory {
    public CommToUsers_V1 createComm(HashMap<Integer,HashMap<String,String>> data,ArrayList<String> target,boolean initiatedComm) {
        //If we communicate to EMS or Doctor website
        if (null == target || target.get(0).equals("0"))
        {
            return new CommToUsers_V1(data, target);
        }
        //If we initiate comm to GIS (or other serverr)
        else if(initiatedComm) {
            return new InitiatedHTTPCommunication_V1(data,target);
        }
        //If we communicate to Apps
        else
        {
            return new GcmCommnication_V1(data,target);
        }
        }
}
