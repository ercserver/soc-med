package CommunicationModule.src.model;

import CommunicationModule.src.api.*;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */

public class CommToUsersFactory_V1 implements ICommToUsersFactory {
    public CommToUsers_V1 createComm(HashMap<Integer,HashMap<String,String>> data,ArrayList<String> target) {
        if (null == target)
        {
            return new HttpCommunication_V1(data,target);
        }
        else
        {
            return new GcmCommnication_V1(data,target);
        }

        }

}
