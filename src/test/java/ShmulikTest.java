import DatabaseModule.src.model.DbComm_V1;
import DatabaseModule.src.model.DbInit_V1;
import org.json.JSONArray;
import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.controller.RegController_V1;
import registrationModule.src.model.RegVerify_V2;

import java.util.HashMap;

/**
 * Created by User on 16/04/2015.
 */
public class ShmulikTest {
    public static void main(String[] args) {
        /*HashMap<String,String> h = new HashMap<String,String>();
        h.put("CommunityMemberID","1002");
        h.put("Password","1234");
        h.put("RegID","0");//h.put("RegID","adasdfasfas");
        RegController_V1 v = new RegController_V1();
        v.verifyDetail(h);*/
        RegController_V1 r = new RegController_V1();
        HashMap<String,String> h = new HashMap<String,String>();
        h.put("CommunityMemberID", "1003");
        h.put("Password", "123");
        h.put("EmailAddress", "erc2server@gmail.com");
        h.put("RegID", "0");
        JSONArray j = (JSONArray) r.signIn(h);
        System.out.println(j);
        //System.out.println(h.get(1).get("RegID"));
        //v.resendMail(1002);
    }
}
