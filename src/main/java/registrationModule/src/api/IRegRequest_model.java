package registrationModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegRequest_model {

    String doesUserExist(HashMap<String, String> filledForm);

    HashMap<String,String> filterFieldsForDoctorAuth(HashMap<String, String> userByParameter);
}
