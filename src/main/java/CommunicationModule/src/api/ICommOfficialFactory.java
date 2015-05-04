package CommunicationModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 30/04/2015.
 */
public interface ICommOfficialFactory {

    ICommOfficial_model createComm(HashMap<String, String> data,int type);

}
