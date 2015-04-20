package CommunicationModule.src.model;

import CommunicationModule.src.api.*;


import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */

public class CommToUsersFactory_V1 implements ICommToUsersFactory {
    public CommToUsers_V1 createComm(HashMap<Integer,HashMap<String,String>> data, int type) {
        switch (type) {
            case 1: {
                return new GcmCommnication_V1(data);
            }
            case 2: {
                return new HttpCommunication_V1(data);
            }
            default:{
                return null;
            }
        }
    }
}
