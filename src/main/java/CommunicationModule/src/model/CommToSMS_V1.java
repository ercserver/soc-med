package CommunicationModule.src.model;

import java.util.HashMap;

/**
 * Created by NAOR on 30/04/2015.
 */
public class CommToSMS_V1 extends CommOfficial_V1 {
    String phoneNumber = null;

    private final String SendingCenter = "XXXXXXXXX";

    CommToSMS_V1(HashMap<String,String> data){
        super(data);
        phoneNumber = data.get("PhoneNumber");
    }

    public void sendMessage() {
        //TODO - Implement SMS messaging at some point (not for the prototype)
    }
}
