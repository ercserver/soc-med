package DatabaseModule.src.controller;


import DatabaseModule.src.api.*;
import DatabaseModule.src.model.*;



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


    @Override
    public void initializeAndConnect() {
        DB_initializer.initializeAndConnect();
    }

    @Override
	//key = Email/InternalID vlaue = cmid do to string by class to  string/mail('') staring a  = "'aaaa'";
    public HashMap<Integer,HashMap<String,String>> getRegistrationFields(int userType) {
        return DB_communicator.getRegistrationFields(userType);
    }

    @Override
    public HashMap<String,String> getUserByParameter(HashMap<String,String> whereConditions){
        return DB_communicator.getUserByParameter(whereConditions);
    }

    @Override
	//update personal detais 
    public void updateUserDetails(HashMap<String,String> updates){
        DB_communicator.updateUserDetails(updates);
    }

    @Override
    public HashMap<Integer,HashMap<String,String>> getFrequency(HashMap<String,String> kindOfFrequency){
        return DB_communicator.getFrequency(kindOfFrequency);
    }

    @Override
    public HashMap<Integer,HashMap<String,String>> getDefaultInEmergency(String state){
        return DB_communicator.getDefaultInEmergency(state);
    }

    @Override
    public HashMap<String,String> getRejectCodes(){
        return DB_communicator.getRejectCodes();
    }

    @Override

    public HashMap<Integer,HashMap<String,String>> getFromEnum(HashMap<String,String> cond){
        return DB_communicator.getFromEnum(cond);
    }

    @Override
	//רשימה של cmid 
    public HashMap<String,String> getWaitingPatientsCMID(int status, int docCMID){
        return DB_communicator.getWaitingPatientsCMID(status, docCMID);
    }

    @Override
	
    public void updateUrgentInRefreshDetailsTime(int CMID, String fieldName, int urgentBit){
        DB_communicator.updateUrgentInRefreshDetailsTime(CMID, fieldName, urgentBit);
    }

    @Override
    public boolean isCommunityMemberExists(int cmid){
        return DB_communicator.isCommunityMemberExists(cmid);
    }

    @Override
    public int addNewCommunityMember(HashMap<String,String> details){
        return DB_communicator.addNewCommunityMember(details);
    }

    @Override
	//מחזיר את אופן איימות הפרטים 
    public HashMap<String,String> getAuthenticationMethod(String state){
        return DB_communicator.getAuthenticationMethod(state);
    }

    @Override
	//מאשר רופאים לקבל את האיימל שלו
    public HashMap<String,String> getEmailOfDoctorsAuthorizer(String state){
        return DB_communicator.getEmailOfDoctorsAuthorizer(state);
    }

    @Override
	// מחזיר מייל סיסמא וcmid 
    public HashMap<String,String> getLoginDetails(String email){
        return DB_communicator.getLoginDetails(email);
    }

    @Override
	//עדכון זמן עידכון אחרון של שדה
    public void updateLastRefreshTime(HashMap<String,String> params){
        DB_communicator.updateLastRefreshTime(params);
    }

    @Override
	//
    public HashMap<Integer,HashMap<String,String>> getAllRefreshTimes(){
        return DB_communicator.getAllRefreshTimes();
    }

    @Override
    public void updateStatus(int cmid, String oldStatus, String newStatus) {
        DB_communicator.updateStatus(cmid,oldStatus,newStatus);
    }

    @Override
    public HashMap<Integer, HashMap<String, String>> getRowsFromTable(HashMap<String, String> whereConditions, String tableName) {
        return DB_communicator.getRowsFromTable(whereConditions, tableName);
    }

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
