package registrationModule.src.controller;


import registrationModule.src.api.IRegController;
import registrationModule.src.api.IRegRequest_model;
import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.model.RegRequest_V1;
import registrationModule.src.model.RegVerify_V1;

import java.util.ArrayList;

/**
 * Created by NAOR on 06/04/2015.
 */
public class RegController_V1 implements IRegController {
    IRegRequest_model registrator = new RegRequest_V1();
    IRegVerify_model verification = new RegVerify_V1();

    @Override
    public void regRequest(int userType) {
        registrator.regRequest(userType);
    }
    @Override
    //public void IVerify(int userType){verification.IVerify(userType);}
    public boolean VerifyDetail(int cmid){
        return verification.VerifyDetail(cmid);
    }
    //boolean verifyDetailsDueToType(int userType);

    public void resendMail(int cmid)
    {
        verification.resendMail(cmid);
    }

    public void responeDoctor(int cmid,String reason)
    {
        verification.responeDoctor(cmid,reason);
    }
}
