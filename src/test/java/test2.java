import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.model.RegVerify_V1;

import DatabaseModule.src.api.IDbComm_model;
import com.sun.deploy.util.StringUtils;

import java.sql.*;
import java.util.*;

/**
 * Created by User on 16/04/2015.
 */
public class test2 {
    public static void main(String[] args) {

        IRegVerify_model v = new RegVerify_V1();
        v.VerifyDetail(1002);
        //maccbi
        // comment
        //Yalla Beitar
    }
}
