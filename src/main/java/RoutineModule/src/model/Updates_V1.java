package RoutineModule.src.model;

import DatabaseModule.src.api.IDbController;
import RoutineModule.src.api.IUpdates_model;
import Utilities.ModelsFactory;

import java.util.HashMap;

/**
 * Created by מאור on 02/05/2015.
 */
public class Updates_V1 implements IUpdates_model {
    private IDbController dbController = null;

    public Updates_V1() {
        ModelsFactory models = new ModelsFactory();
        dbController = models.determineDbControllerVersion();
    }

    public HashMap<Integer,HashMap<String,String>> getFieldsForUpdate(HashMap<String, String> data)
    {
        HashMap<Integer,HashMap<String,String>> fields = dbController.getRegistrationFields(
                dbController.getUserType(data.get("community_member_id")));
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("P_CommunityMembers.community_member_id", data.get("community_member_id"));
        HashMap<String,String> userDetails = dbController.getUserByParameter(cond);
        //ToDo-check format with cases of values with id...
        for(int i = 1; i <= fields.size(); i++)
        {
            if(userDetails.get(fields.get(i).get("field_name")) != null)
                fields.get(i).put("user_value", userDetails.get(fields.get(i).get("field_name")));
            else
                fields.get(i).put("user_value", "null");
        }
        return fields;
    }
}
