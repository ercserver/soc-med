package registrationModule.src.model;

import DatabaseModule.src.api.IDbController;
import registrationModule.src.api.IRegRequest_model;
import registrationModule.src.utilities.ModelsFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class RegRequest_V1 implements IRegRequest_model {
    private static final String userExistsMessage = "An active user with this mail already exists";

    private IDbController dbController = null;

    public RegRequest_V1()
    {
        ModelsFactory models = new ModelsFactory();
        dbController = models.determineDbControllerVersion();
    }

    public String doesUserExist(HashMap<String, String> filledForm) {
        //search for an active user with that email - if not found return null, else return "userExistsMessage".
        HashMap<String,String> whereConditions = new HashMap<String, String>();
        whereConditions.put("P_CommunityMember.email_address", filledForm.get("email_address"));
        HashMap<String,String> result = dbController.getUserByParameter(whereConditions);
        String message = null;
        if((null == result) || (result.get("status").equals("active"))){
            return null;
        }
        return userExistsMessage;
    }

    public HashMap<String, String> filterFieldsForDoctorAuth(HashMap<String, String> fieldsToFilter) {
        HashMap<String,String> filteredResponse = new HashMap<String, String>();
        //Get the list of fields to filter
        ArrayList<String> listOfFieldsToFilter = decideFieldsToFilterForDoctorAuth();
        //One-by-one - retrieve the fields into the response
        for(String field : listOfFieldsToFilter){
            filteredResponse.put(field,fieldsToFilter.get(field));
        }
        return filteredResponse;
    }

    private ArrayList<String> decideFieldsToFilterForDoctorAuth() {
        ArrayList<String> decision = new ArrayList<String>();
        decision.add("first_name");
        decision.add("last_name");
        decision.add("community_member_id");
        decision.add("external_id");
        //TODO - More fields?
        //decision.add("medical_condition");
        //...
        //...
        return decision;
    }

}
