package CommunicationModule.src.api;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 18/04/2015.
 */
public interface ICommToUsersFactory {

    ICommToUsers_model createComm(HashMap<Integer, HashMap<String, String>> data,
                                  ArrayList<String> target,boolean initiatedComm);
}
