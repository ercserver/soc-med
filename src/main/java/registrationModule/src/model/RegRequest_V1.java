package registrationModule.src.model;

import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import registrationModule.src.api.IRegRequest_model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class RegRequest_V1 implements IRegRequest_model {



    private void sendRegData(HashMap<String,String> data){
        //TODO
    }

    @Override
    public void regRequest(int userType) {
        //generate the data to be sent
        HashMap<Integer,HashMap<String,String>>  dataToSend = establishRequestParams(userType);
        //determine how to send the data
        CommToUsersFactory_V1 commToUsersFact = new CommToUsersFactory_V1();
        CommToUsers_V1 commToUsers = commToUsersFact.createComm(dataToSend, determineCommunicationMethod(userType));
        //send the data
        commToUsers.SendResponse();
    }
    //a helper method to determine the communication method
    private int determineCommunicationMethod(int userType){
        switch(userType){
            // 1,2 and 3 indicate the Doctor, Apotropus and Patient - Communicate through Gsm
            case 1:
            case 2:
            case 3:{
                return 1;
            }
            //4 indicates EMS
            case 4:{
                return 2;
            }
            default:{
                return -1;
            }
        }
    }

    private HashMap<Integer,HashMap<String,String>>  establishRequestParams(int userType){
        //TODO - Communicate the DB to retrieve the data - need to implement a method on the database to retrieve registration data by user type
        return null;
    }
}
