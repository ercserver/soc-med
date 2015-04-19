package CommunicationModule.src.api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IResponseCreator {
    Object establishResponse(HashMap<Integer,HashMap<String,String>> data);
}
