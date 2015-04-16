package registrationModule.src.api;

/**
 * Created by NAOR on 05/04/2015.
 */

public interface IRegRequest {
    //Perhaps we should enum in the database for the possible user types?
    void regRequest(int userType);
}
