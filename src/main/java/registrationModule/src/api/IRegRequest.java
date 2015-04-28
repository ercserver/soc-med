package registrationModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */

public interface IRegRequest {
    //Perhaps we should enum in the database for the possible user types?

    void getRegDetails(HashMap<String,String> request);
}
