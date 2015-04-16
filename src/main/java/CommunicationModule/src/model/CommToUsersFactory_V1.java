package CommunicationModule.src.model;

import java.util.ArrayList;

/**
 * Created by NAOR on 06/04/2015.
 */

public class CommToUsersFactory_V1 {
    public CommToUsers_V1 createComm(ArrayList<ArrayList<String>> data, int type) {
        switch (type) {
            case 1: {
                return new GsmCommnication_V1(data);
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
