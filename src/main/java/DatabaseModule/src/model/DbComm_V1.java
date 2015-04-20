package DatabaseModule.src.model;


import DatabaseModule.src.api.IDbComm_model;
import com.sun.deploy.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;

/**
 * Created by NAOR on 06/04/2015.
 */
public class DbComm_V1 implements IDbComm_model {

    final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    final String DB_URL = "jdbc:sqlserver://socmedserver.mssql.somee.com;";//databaseName=ercserver-socmed";
    final String DBName = "socmedserver";
    final private String USERNAME = "saaccount";
    final private String PASS = "saaccount";
    private Connection connection = null;
    private Statement statement = null;
    private String SCHEMA = "Ohad";//*

    private  void connect() throws SQLException
    {
        try
        {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASS);
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            //statement.addBatch("DROP DATABASE " + DBName);
            //statement.addBatch("CREATE database " + DBName);
            statement.addBatch("USE " + DBName);

            connection.commit();
            statement.executeBatch();

            DatabaseMetaData dbm = connection.getMetaData();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();}
    }

    private  void releaseResources(Statement statement, Connection connection)
    {

        if (statement != null)
        {
            try
            {
                statement.close();
            }
            catch (Exception e) {e.printStackTrace();}
        }
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    public HashMap<Integer,HashMap<String,String>>
    getRowsFromTable(HashMap<String,String> whereConditions, String tableName)
    {
        String conditions = "";
        if(whereConditions == null)
            conditions = "1=1";
        else
        {
            int numOfConditions = whereConditions.size();
            Set<String> keys = whereConditions.keySet();
            Iterator<String> iter = keys.iterator();
            String key = iter.next();
            conditions = key + "=" + whereConditions.get(key);
            for (int i = 1; i < numOfConditions; i++)
            {
                key = iter.next();
                conditions += " AND " + key + "=" + whereConditions.get(key);
            }
        }
        ResultSet rs = null;
        try
        {
            connect();
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT DISTINCT * FROM " + tableName +
                    " WHERE " + conditions);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++ )
                columnNames.add(rsmd.getColumnName(i));
            HashMap<Integer,HashMap<String,String>> results= new HashMap<Integer,HashMap<String,String>>();
            if (!rs.next())
                return null;
            else
            {
                int j = 1;
                do
                {
                    HashMap<String,String> line = new HashMap<String,String>();
                    Iterator<String> iter = columnNames.iterator();
                    for (int i = 0; i < columnCount; i++)
                    {
                        String column = iter.next();
                        line.put(column, rs.getObject(column).toString());
                    }
                    results.put(new Integer(j), line);
                    j++;
                }while (rs.next());
                return results;
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace(); return null;}
        // Releases the resources of this method
        finally
        {
            releaseResources(statement, connection);
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    public HashMap<Integer,HashMap<String,String>> getRegistrationFields(int userType)
    {
        HashMap<String,String> conds = new HashMap<String,String>();
        conds.put("UserType", Integer.toString(userType));
        return getRowsFromTable(conds, "RegistrationFields");
    }

    public HashMap<String,String> getUserByParameter(HashMap<String,String> whereConditions)
    {
        String conditions = "";
        int numOfConditions = whereConditions.size();
        Set<String> keys = whereConditions.keySet();
        Iterator<String> iter = keys.iterator();
        String key = iter.next();
        conditions = key + "=" + whereConditions.get(key);
        for (int i = 1; i < numOfConditions; i++)
        {
            key = iter.next();
            conditions += " AND " + key + "=" + whereConditions.get(key);
        }
        ResultSet rs = null;
        try
        {
            connect();
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT DISTINCT * FROM " + "P_CommunityMembers INNER JOIN "
                    + "P_Patients ON P_CommunityMembers.InternalID=P_Patients.CommunityMemberID "
                    + "INNER JOIN P_EmergencyContact ON P_Patients.CommunityMemberID=P_EmergencyContact.CommunityMemberID "
                    + "INNER JOIN MembersLoginDetails ON P_EmergencyContact.CommunityMemberID=MembersLoginDetails.CommunityMemberID "
                    + "INNER JOIN P_Supervision ON P_Patients.PatientID=P_Supervision.PatientID "
                    + "INNER JOIN P_Prescriptions ON P_Supervision.PatientID=P_Prescriptions.PatientID "
                    + "INNER JOIN P_Diagnosis ON P_Prescriptions.PatientID=P_Diagnosis.PatientID "
                    + "INNER JOIN P_StatusLog ON MembersLoginDetails.CommunityMemberID=P_StatusLog.CommunityMemberID "
                    + "INNER JOIN P_Statuses ON P_StatusLog.StatusNum=P_Statuses.StatusNum " +
                    "WHERE " + conditions + " ORDER BY " + "P_StatusLog.DateFrom");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++ )
                columnNames.add(rsmd.getColumnName(i));
            if (!rs.next())
                return null;
            else
            {
                HashMap<String,String> user = new HashMap<String,String>();;
                do
                {
                    user.clear();
                    //if (rs.isLast())
                    //{
                    iter = columnNames.iterator();
                    for (int i = 0; i < columnCount; i++)
                    {
                        String column = iter.next();
                        if((!user.containsKey(column)) && (column != "DateFrom") && (column != "DateTo"))
                        {
                            if (rs.getObject(column) != null)
                                user.put(column, rs.getObject(column).toString());
                            else
                                user.put(column, "null");
                        }
                    }
                    //}
                }while (rs.next());
                return user;
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace(); return null;}
        // Releases the resources of this method
        finally
        {
            releaseResources(statement, connection);
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    public void updateUserDetails(HashMap<String,String> updates)
    {
        int CMID = Integer.parseInt(updates.get("CMID"));
        updates.remove("CMID");
        String Supdates = "";
        int numOfUpdates = updates.size();
        Set<String> keys = updates.keySet();
        Iterator<String> iter = keys.iterator();
        String key = iter.next();
        Supdates = key + "=" + updates.get(key);
        for (int i = 1; i < numOfUpdates; i++)
        {
            key = iter.next();
            Supdates += ", " + key + "=" + updates.get(key);
        }
        try
        {
            connect();
            statement = connection.createStatement();
            statement.execute("UPDATE " +  "P_CommunityMembers SET " +
                    updates + " WHERE InternalID=" + Integer.toString(CMID));
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        finally
        {
            releaseResources(statement, connection);
        }
    }

    public HashMap<Integer,HashMap<String,String>> getFrequency(HashMap<String,String> kindOfFrequency)
    {
        return selectFromTable("Frequencies", null, kindOfFrequency);
    }

    public HashMap<Integer,HashMap<String,String>> getDefaultInEmergency(String state)
    {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("State", "'" + state + "'");
        ArrayList<String> select = new ArrayList<String>();
        select.add("DefaultE");
        return selectFromTable("DefaultForEmergency", select, cond);
    }

    public HashMap<String,String> getRejectCodes()
    {
        HashMap<Integer,HashMap<String,String>> rejectCodes = getRowsFromTable(null, "RejectCodes");
        int numOfCodes = rejectCodes.size();
        HashMap<String,String> codes = new HashMap<String,String>();
        Collection<HashMap<String,String>> col = rejectCodes.values();
        Iterator<HashMap<String,String>> iter = col.iterator();
        for(int i = 0; i < numOfCodes; i++)
        {
            HashMap<String,String> m = iter.next();
            codes.put(m.get("Id"), m.get("Description"));
        }
        return codes;
    }

    public HashMap<Integer,HashMap<String,String>> getFromEnum(HashMap<String,String> cond)
    {
        return selectFromTable("Enum", null, cond);
    }

    public HashMap<String,String> getWaitingPatientsCMID(int status, int docCMID)
    {
        ResultSet rs = null;
        ResultSet rs1 = null;
        try
        {
            connect();
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT DISTINCT * FROM " + "P_Doctors INNER JOIN "+
                    "P_Supervision ON P_Doctors.DoctorID=P_Supervision.DoctorID "
                    + "WHERE P_Doctors.CommunityMemberID="
                    + Integer.toString(docCMID));
            if(!rs.next())
                return null;
            else
            {
                HashMap<String,String> res = new HashMap<String,String>();
                int numOfPatients = 0;
                do
                {
                    int patientID = rs.getInt("PatientID");
                    Statement statement2 = connection.createStatement();
                    rs1 = statement2.executeQuery("SELECT DISTINCT * FROM " + "P_Patients INNER JOIN "
                            + "P_StatusLog ON P_Patients.CommunityMemberID=P_StatusLog.CommunityMemberID"
                            + " WHERE P_Patients.PatientID="
                            + Integer.toString(patientID) + " AND " + "P_StatusLog.StatusNum="
                            + Integer.toString(status));
                    if (!rs1.next())
                        continue;
                    else
                    {
                        numOfPatients++;
                        res.put(Integer.toString(numOfPatients),
                                Integer.toString(rs1.getInt("CommunityMemberID")));
                    }
                }while (rs.next());
                return res;
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace(); return null;}
        // Releases the resources of this method
        finally
        {
            releaseResources(statement, connection);
            if (rs != null)
            {
                try
                {
                    rs.close();
                    rs1.close();
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    public void updateUrgentInRefreshDetailsTime(int CMID, String fieldName, int urgentBit)
    {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("CommunityMemberID", Integer.toString(CMID));
        cond.put("FieldName", fieldName);
        updateTable("RefreshDetailsTime", cond, "Urgent", Integer.toString(urgentBit));
        updateTable("RefreshDetailsTime", cond, "LastUpdateTime", Calendar.getInstance());
    }

    private HashMap<Integer,HashMap<String,String>> resultSetToMap(ResultSet rs){
        HashMap<Integer,HashMap<String,String>> map =
                new HashMap<Integer,HashMap<String,String>>();
        try {
            int j = 1;
            while (rs.next()) {
                int total_rows = rs.getMetaData().getColumnCount();
                HashMap<String,String> obj = new HashMap<String,String>();
                for (int i = 0; i < total_rows; i++) {
                    obj.put(rs.getMetaData().getColumnLabel(i + 1)
                            .toLowerCase(), rs.getObject(i + 1).toString());
                }
                map.put(new Integer(j), obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private HashMap<Integer,HashMap<String,String>> selectFromTable
            (String tableName, List<String> columns, HashMap<String,String> whereConds){

        // Create the select clause
        String selectString;
        if (columns == null) { //Select *
            selectString = "*";
        }else{
            selectString = StringUtils.join(columns, ",");
        }

        // Create the sql query
        String sql = String.format("SELECT %s FROM %s", selectString, tableName);

        // Create the where clause
        String whereString = "";
        if (whereConds != null) {
            Iterator<String> iter = whereConds.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                String val = whereConds.get(key);
                whereString += String.format("%s=%s AND ", key, val);
            }
            // Remove the last "AND"
            whereString = whereString.substring(0, whereString.length() - 4);

            // Update the query string
            sql += " WHERE " + whereString;
        }
        ResultSet rs = null;
        // System.out.println(sql);
        try {
            //connect();
            connect();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            return resultSetToMap(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally
        {
            releaseResources(statement, connection);
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
        return null;
    }

    private void updateTable(String tableName, HashMap<String,String> whereConds,
                             String columnToUpdate, Object newValue)
    {
        // Create the where clause
        String whereString = "";
        Iterator<String> iter = whereConds.keySet().iterator();
        while (iter.hasNext()){
            String key = iter.next();
            String val = whereConds.get(key);
            whereString += String.format("%s=%s AND ", key, val);
        }
        // Remove the last "AND"
        whereString = whereString.substring(0, whereString.length() - 4);

        // Create the sql query
        String sql = String.format("UPDATE %s SET %s=%s WHERE %s", tableName, columnToUpdate, newValue.toString(), whereString);
        //System.out.println(sql);
        try {
            connect();
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally
        {
            releaseResources(statement, connection);
        }

    }

    public boolean isCommunityMemberExists(int cmid) {
        HashMap<String,String> where = new HashMap<String,String>();
        where.put("InternalID", Integer.toString(cmid));
        List<String> columns = Arrays.asList("InternalID");
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("P_CommunityMembers", columns, where);
        return (res.size() != 0);
    }

    public int addNewCommunityMember(HashMap<String,String> details) {
        return 0;
    }

    public HashMap<String,String> getAuthenticationMethod(String state) {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("State", state);
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("AuthenticationMethod", Arrays.asList("method"), cond);
        if (res.size() != 0){
            Collection<HashMap<String,String>> coll = res.values();
            return coll.iterator().next();
        }
        return null;
    }

    public HashMap<String,String> getEmailOfDoctorsAuthorizer(String state)
    {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("State", state);
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("DoctorAuthorizers", Arrays.asList("Email"), cond);
        if (res.size() != 0){
            Collection<HashMap<String,String>> coll = res.values();
            return coll.iterator().next();
        }
        return null;
    }

    public HashMap<String,String> getLoginDetails(String email) {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("EmailAddress", email);
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("DoctorAuthorizers", null, cond);
        if (res.size() != 0){
            Collection<HashMap<String,String>> coll = res.values();
            return coll.iterator().next();
        }
        return null;
    }

    // NOT TESTED
    public void updateLastRefreshTime(HashMap<String,String> params) {
        // Get all the relevant values from parms
        int cmid = Integer.parseInt(params.get("CommunityMemberID"));
        String fieldToUpdate = params.get("FieldName");
        String tsS = params.get("LastUpdateTime");
        Timestamp ts =  Timestamp.valueOf(tsS);
        // Create the where clause json
        HashMap<String,String> whereMap = new HashMap<String,String>();
        whereMap.put("CommunityMemberID", Integer.toString(cmid));
        whereMap.put("FieldName", fieldToUpdate);
        // Update the datetime field
        updateTable("RefreshDetailsTime", whereMap, "LastUpdateTime", ts);
    }

    public HashMap<Integer,HashMap<String,String>> getAllRefreshTimes() {
        return selectFromTable("RefreshDetailsTime", null, null);
    }

    public void updateStatus(int cmid, String oldStatus, String newStatus)
    {
        ResultSet rs = null;
        try
        {
            connect();
            HashMap<String,String> cond = new HashMap<String,String>();
            cond.put("StatusName", oldStatus);
            HashMap<Integer,HashMap<String,String>> s = getRowsFromTable(cond, "P_Statuses");
            Collection<HashMap<String,String>> val = s.values();
            String statusNum = val.iterator().next().get("StatusNum");
            statement = connection.createStatement();
            statement.execute("UPDATE P_StatusLog SET DateTo='" +
                    new java.sql.Date(Calendar.getInstance().getTime().getTime()) + "' WHERE"
                    + " StatusNum=" + statusNum + " AND CommunityMemberID="
                    + Integer.toString(cmid));
            cond = new HashMap<String,String>();
            cond.put("StatusName", newStatus);
            s = getRowsFromTable(cond, "P_Statuses");
            val = s.values();
            statusNum = val.iterator().next().get("StatusNum");
            statement = connection.createStatement();
            statement.execute("INSERT INTO P_StatusLog (StatusNum,CommunityMemberID) VALUES (" +
                    statusNum + "," + Integer.toString(cmid) + ")");
        }
        catch (SQLException e) {e.printStackTrace();}
        finally
        {
            releaseResources(statement, connection);
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }
}
