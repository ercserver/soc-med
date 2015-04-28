package CommunicationModule.src.api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface ICommController extends ICommToUsers,ICommToMail{
    //ISP (Interface Segregation Principle)

    //set methods for the members
    public void setCommToMail(String emailAdress,String emailMessage,
                              String subj);
    public void setCommToUsers(HashMap<Integer, HashMap<String, String>> data,
                               ArrayList<String> target,boolean initiatedComm);
}
