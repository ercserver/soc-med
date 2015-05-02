import registrationModule.src.model.RegVerify_V2;

import java.util.HashMap;

/**
 * Created by מאור on 01/05/2015.
 */
public class MaorTests {
    public static void main(String[] args) {
        RegVerify_V2 r = new RegVerify_V2();
        HashMap<String,String> h = new HashMap<String,String>();
        h.put("CommunityMemberID", "1002");
        h.put("Password", "1234");
        h.put("EmailAddress", "ercserver@gmail.com");
        System.out.println(r.verifySignIn(h).toString());
    }
}
