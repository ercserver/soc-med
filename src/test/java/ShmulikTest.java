/*
import DatabaseModule.src.model.DbComm_V1;
import DatabaseModule.src.model.DbInit_V1;
import org.json.JSONArray;
import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.controller.RegController_V1;
import registrationModule.src.model.RegVerify_V2;

import java.util.HashMap;

*/

import registrationModule.src.controller.RegController_V1;
import registrationModule.src.model.RegVerify_V2;

import java.util.HashMap;

/**
 * Created by User on 16/04/2015.
 */

public class ShmulikTest {
    public static void main(String[] args) {
/*
        RegVerify_V2 v2 = new RegVerify_V2();
        HashMap<String,String> h = new HashMap<String,String>();
        h = v2.getUserByCmid(1002);
        h.put("reg_id", "acasvvsdgsdgs");
        RegController_V1 v = new RegController_V1();
        v2.changeStatusToVerifyDetailAndSendToApp(1002,h);
        //v.verifyDetail(h);
*/
        test3();

    }

    private static void test3() {

        RegVerify_V2 v2 = new  RegVerify_V2();
        HashMap<String, String> s = v2.getUserByCmid(1002);
        System.out.println(s);
    }

    public static void test2()
    {
        HashMap<String,String> details = new HashMap<String,String>();
        details.put("status_num","1002");
        RegVerify_V2 v2 = new  RegVerify_V2();
        String s = v2.getStatus(details);
        System.out.println(s);

    }
}

