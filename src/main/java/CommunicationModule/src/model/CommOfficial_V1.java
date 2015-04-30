package CommunicationModule.src.model;

import CommunicationModule.src.api.ICommOfficial_model;

import java.util.HashMap;

/**
 * Created by NAOR on 30/04/2015.
 */
public abstract class CommOfficial_V1 implements ICommOfficial_model {
    String msgToSend = null;
    String subject = null;

    CommOfficial_V1(HashMap<String,String> data){
        msgToSend = data.get("Message");
        subject = data.get("Subject");
    }
}
