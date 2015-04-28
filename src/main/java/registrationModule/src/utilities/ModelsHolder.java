package registrationModule.src.utilities;

import CommunicationModule.src.api.ICommController;
import CommunicationModule.src.controller.CommController_V1;
import DatabaseModule.src.api.IDbController;
import DatabaseModule.src.controller.DbController_V1;
import registrationModule.src.api.IRegRequest_model;
import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.model.RegRequest_V1;
import registrationModule.src.model.RegVerify_V1;

/**
 * Created by NAOR on 22/04/2015.
 */
public class ModelsHolder {
    private final int commControllerVersion = 1;
    private final int dbControllerVersion = 1;
    private final int regRequestVersion = 1;
    private final int regVerifyVersion = 1;


    public ModelsHolder(){}


    public ICommController determineCommControllerVersion(){
        switch (commControllerVersion) {
            //Communicate the DB to retrieve the data
            case 1: {
                return new CommController_V1();
            }
            default: {
                return null;
            }
        }
    }
    public IDbController determineDbControllerVersion(){
        switch (dbControllerVersion) {
            //Communicate the DB to retrieve the data
            case 1: {
                return new DbController_V1();
            }
            default: {
                return null;
            }
        }
    }
    public IRegRequest_model determineRegRequestVersion(){
        switch (dbControllerVersion) {
            //Communicate the DB to retrieve the data
            case 1: {
                return new RegRequest_V1();
            }
            default: {
                return null;
            }
        }
    }
    public IRegVerify_model determineRegVerifyVersion(){
        switch (dbControllerVersion) {
            //Communicate the DB to retrieve the data
            case 1: {
                return new RegVerify_V1();
            }
            default: {
                return null;
            }
        }
    }

}
