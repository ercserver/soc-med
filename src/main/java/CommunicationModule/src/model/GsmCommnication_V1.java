package CommunicationModule.src.model;

import CommunicationModule.src.model.CommToUsers_V1;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */
public class GsmCommnication_V1 extends CommToUsers_V1 {

    public GsmCommnication_V1(HashMap<Integer,HashMap<String,String>> data) {
        super(data);
    }
    @Override
    public void SendResponse () {
        //TODO + To understand Gsm
    }
}
