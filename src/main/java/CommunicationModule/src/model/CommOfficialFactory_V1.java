package CommunicationModule.src.model;

import CommunicationModule.src.api.ICommOfficialFactory;
import CommunicationModule.src.api.ICommOfficial_model;

import java.util.HashMap;

/**
 * Created by NAOR on 30/04/2015.
 */

public class CommOfficialFactory_V1 implements ICommOfficialFactory {

    //TODO - we should probably turn "type" into enum at some point...
    public ICommOfficial_model createComm(HashMap<String, String> data, int type) {
        switch(type){
            case 0:{
                return new CommToMail_V1(data);
            }
            case 1:{
                return new CommToSMS_V1(data);
            }
            default:{
                return null;
            }
        }
    }
}


