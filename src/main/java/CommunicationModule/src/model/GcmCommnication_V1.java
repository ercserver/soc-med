package CommunicationModule.src.model;


import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */
public class GcmCommnication_V1 extends CommToUsers_V1 {

    public GcmCommnication_V1(HashMap<Integer,HashMap<String,String>> data) {
        super(data);
    }
    @Override
    public void SendResponse () {

    }
}
