package registrationModule.src.model;

import DatabaseModule.src.api.IDbController;
import registrationModule.src.api.IRegRequest_model;
import registrationModule.src.utilities.ModelsHolder;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class RegRequest_V1 implements IRegRequest_model {

    private IDbController dbController = null;

    public RegRequest_V1()
    {
        ModelsHolder models = new ModelsHolder();
        dbController = models.determineDbControllerVersion();
    }

    public boolean doesUserExist(HashMap<String, String> filledForm) {
        //search for an active user with that email - if found return true, else return false.
        HashMap<String,String> whereConditions = new HashMap<String, String>();
        whereConditions.put("E-mail", filledForm.get("E-mail"));
        HashMap<String,String> result = dbController.getUserByParameter(whereConditions);

        return((null != result) && (result.get("Status").equals("Active")));
    }

    public HashMap<String,String> regDetailsRequest(HashMap<String,String> request) {

        String regID = request.get("regID");
        int userType = Integer.parseInt(request.get("userType"));

        //generate the data to be sent
        return establishRequestParams(userType,regID);
    }

    private HashMap<String,String>  establishRequestParams(int userType,String regID){
        //Communicate the DB to retrieve the fields to be filled by that type of user
        HashMap<String,String> response = dbController.getRegistrationFields(userType).get(1);
        //Add the regID to the response
        response.put("SendToRegID",regID);
        return response;
    }
}
