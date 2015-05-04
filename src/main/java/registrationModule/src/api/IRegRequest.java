package registrationModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */

public interface IRegRequest {
    Object handleReg(HashMap<String, String> filledForm);
    Object getRegDetails(HashMap<String,String> request);
    Object getWaitingForDoctor (int doctorCmid);
}
