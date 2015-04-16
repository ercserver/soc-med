package DatabaseModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IDbComm_model {
    //TODO - lots of methods to query the DB
    public HashMap<Integer,HashMap<String,String>> getRegistrationFields(int userType);
    public HashMap<String,String> getUserByParameter(HashMap<String,String> whereConditions);
    public void updateUserDetails(HashMap<String,String> updates);
    public HashMap<Integer,HashMap<String,String>> getFrequency(HashMap<String,String> kindOfFrequency);
    public HashMap<Integer,HashMap<String,String>> getDefaultInEmergency(String state);
    public HashMap<String,String> getRejectCodes();
    public HashMap<Integer,HashMap<String,String>> getFromEnum(HashMap<String,String> cond);
    public HashMap<String,String> getWaitingPatientsCMID(int status, int docCMID);
    public void updateUrgentInRefreshDetailsTime(int CMID, String fieldName, int urgentBit);
    public boolean isCommunityMemberExists(int cmid);
    public int addNewCommunityMember(HashMap<String,String> details);
    public HashMap<String,String> getAuthenticationMethod(String state);
    public HashMap<String,String> getEmailOfDoctorsAuthorizer(String state);
    public HashMap<String,String> getLoginDetails(String email);
    public void updateLastRefreshTime(HashMap<String,String> params);
    public HashMap<Integer,HashMap<String,String>> getAllRefreshTimes();
    public void updateStatus(int cmid, String oldStatus, String newStatus);
}
