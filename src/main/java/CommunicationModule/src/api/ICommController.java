package CommunicationModule.src.api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface ICommController extends ICommToUsers,ICommOfficial{
    //ISP (Interface Segregation Principle)

    //set methods for the members
    void setCommOfficial(HashMap<String,String> data,int type);
    void setCommToUsers(HashMap<Integer, HashMap<String, String>> data,
                               ArrayList<String> target,boolean initiatedComm);
}
