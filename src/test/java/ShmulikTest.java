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
       /* HashMap<String,String> h = new HashMap<String,String>();
        h.put("community_member_id","1002");
        h.put("password","1234");
        h.put("reg_id","0");//h.put("RegID","adasdfasfas");
        RegController_V1 v = new RegController_V1();
        v.verifyDetail(h);*/
        RegVerify_V2 v2 = new  RegVerify_V2();
        v2.getUserByCmid(1002);
        //v.resendMail(1002);
    }
}
