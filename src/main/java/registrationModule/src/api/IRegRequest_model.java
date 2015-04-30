package registrationModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegRequest_model {


    HashMap<String,String> regDetailsRequest(HashMap<String,String> request);

    String doesUserExist(HashMap<String, String> filledForm);
}
