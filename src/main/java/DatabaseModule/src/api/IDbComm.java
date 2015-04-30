package DatabaseModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IDbComm {
    //TODO - lots of methods to query the DB
    HashMap<Integer,HashMap<String,String>> getRegistrationFields(int userType);
    HashMap<String,String> getUserByParameter(HashMap<String,String> whereConditions);
    void updateUserDetails(HashMap<String,String> updates);
    HashMap<Integer,HashMap<String,String>> getFrequency(HashMap<String,String> kindOfFrequency);
    HashMap<Integer,HashMap<String,String>> getDefaultInEmergency(String state);
    HashMap<String,String> getRejectCodes();
    HashMap<Integer,HashMap<String,String>> getFromEnum(HashMap<String,String> cond);
    HashMap<String,String> getWaitingPatientsCMID(int status, int docCMID);
    void updateUrgentInRefreshDetailsTime(int CMID, String fieldName, int urgentBit);
    boolean isCommunityMemberExists(int cmid);
    int addNewCommunityMember(HashMap<String,String> details);
    int getAuthenticationMethod(String state);
    HashMap<String,String> getEmailOfDoctorsAuthorizer(String state);
    HashMap<String,String> getLoginDetails(String email);
    void updateLastRefreshTime(HashMap<String,String> params);
    HashMap<Integer,HashMap<String,String>> getAllRefreshTimes();
    void updateStatus(int cmid, String oldStatus, String newStatus);
    void deleteUser(int cmid);
}
