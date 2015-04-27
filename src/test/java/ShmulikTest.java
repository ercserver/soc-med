import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.model.RegVerify_V1;

import DatabaseModule.src.api.IDbComm_model;
import com.sun.deploy.util.StringUtils;

import java.sql.*;
import java.util.*;

/**
 * Created by User on 16/04/2015.
 */
public class ShmulikTest {
    public static void main(String[] args) {

        //IRegVerify_model v = new RegVerify_V1();
        //v.VerifyDetail(1002);
        ArrayList<String> a = new ArrayList<String>();
        a.add("macbbi");
        a.add("aspah");
        a.add("noar");
        String b = a.toString();
        System.out.println(b);
        ArrayList<String> c = new ArrayList<String>();
        System.out.println(c);
                       //v.resendMail(1002);
    }
}
