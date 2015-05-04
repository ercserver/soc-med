package DatabaseModule.src.controller;


import DatabaseModule.src.api.*;
import DatabaseModule.src.model.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class DbController_V1 implements IDbController {
    private final int dbInitVersion = 1;
    private final int dbCommVersion = 1;

    //holding the implementations chosen for the interface (composition)
    private IDbInit_model DB_initializer = determineDbInitVersion();
    private IDbComm_model DB_communicator = determineDbCommVersion();


    public void initializeAndConnect() {
        DB_initializer.initializeAndConnect();
    }

	//key = Email/InternalID vlaue = cmid do to string by class to  string/mail('') staring a  = "'aaaa'";
    public HashMap<Integer,HashMap<String,String>> getRegistrationFields(int userType) {
        return DB_communicator.getRegistrationFields(userType);
    }

    public HashMap<String,String> getUserByParameter(HashMap<String,String> whereConditions){
        return DB_communicator.getUserByParameter(whereConditions);
    }

	//update personal detais 
    public void updateUserDetails(HashMap<String,String> updates){
        DB_communicator.updateUserDetails(updates);
    }

    public HashMap<Integer,HashMap<String,String>> getFrequency(HashMap<String,String> kindOfFrequency){
        return DB_communicator.getFrequency(kindOfFrequency);
    }

    public HashMap<Integer,HashMap<String,String>> getDefaultInEmergency(String state){
        return DB_communicator.getDefaultInEmergency(state);
    }

    public HashMap<String,String> getRejectCodes(){
        return DB_communicator.getRejectCodes();
    }

    public HashMap<Integer,HashMap<String,String>> getFromEnum(HashMap<String,String> cond){
        return DB_communicator.getFromEnum(cond);
    }

    public ArrayList<String> getWaitingPatientsCMID(int docCMID){
        return DB_communicator.getWaitingPatientsCMID(docCMID);
    }
	
    public void updateUrgentInRefreshDetailsTime(int CMID, String fieldName, int urgentBit){
        DB_communicator.updateUrgentInRefreshDetailsTime(CMID, fieldName, urgentBit);
    }

    public boolean isCommunityMemberExists(int cmid){
        return DB_communicator.isCommunityMemberExists(cmid);
    }

    public int addNewCommunityMember(HashMap<String,String> details){
        return DB_communicator.addNewCommunityMember(details);
    }

    public int getAuthenticationMethod(String state){
        return DB_communicator.getAuthenticationMethod(state);
    }

    public HashMap<String,String> getEmailOfDoctorsAuthorizer(String state){
        return DB_communicator.getEmailOfDoctorsAuthorizer(state);
    }

    public HashMap<String,String> getLoginDetails(String email){
        return DB_communicator.getLoginDetails(email);
    }

    public void updateLastRefreshTime(HashMap<String,String> params){
        DB_communicator.updateLastRefreshTime(params);
    }

    public HashMap<Integer,HashMap<String,String>> getAllRefreshTimes(){
        return DB_communicator.getAllRefreshTimes();
    }

    public void updateStatus(int cmid, String oldStatus, String newStatus) {
        DB_communicator.updateStatus(cmid,oldStatus,newStatus);
    }

    public HashMap<Integer, HashMap<String, String>> getRowsFromTable(HashMap<String, String> whereConditions, String tableName) {
        return DB_communicator.getRowsFromTable(whereConditions, tableName);
    }

    public void deleteUser(int cmid){DB_communicator.deleteUser(cmid);}

    public int getUserType(String cmid){return DB_communicator.getUserType(cmid);}

    private IDbInit_model determineDbInitVersion(){
        switch (dbInitVersion) {
            //determine version of CommToUsers to use
            case 1: {
                return new DbInit_V1();
            }
            default: {
                return null;
            }
        }
    }

    private IDbComm_model determineDbCommVersion(){
        switch (dbCommVersion) {
            //determine version of CommToUsers to use
            case 1: {
                return new DbComm_V1();
            }
            default: {
                return null;
            }
        }
    }
}
