package DatabaseModule.src.model;


import DatabaseModule.src.api.IDbComm_model;
import com.sun.deploy.util.StringUtils;
import org.json.JSONObject;

import java.sql.*;
import java.sql.Date;
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
    private static Connection connection = null;
    private Statement statement = null;
    private String SCHEMA = "Ohad";//*

    private  void connect() throws SQLException
    {
        try
        {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASS);
            connection.setAutoCommit(true);
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
        /*if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception e) {e.printStackTrace();}
        }*/
    }

    /*For all of the methods:in each HashMap if the value represent value of column with type varchar,
      the value need to be in this format: 'value' */

    public HashMap<Integer,HashMap<String,String>>
    getRowsFromTable(HashMap<String,String> whereConditions, String tableName)
    {
        String conditions = "";
        // select *....
        if(whereConditions == null)
            conditions = "1=1";
        else
        {
            int numOfConditions = whereConditions.size();
            Set<String> keys = whereConditions.keySet();
            Iterator<String> iter = keys.iterator();
            // creates the where condition for sql query
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
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT DISTINCT * FROM " + tableName +
                    " WHERE " + conditions);
            // gets columns names
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++ )
                columnNames.add(rsmd.getColumnName(i));
            HashMap<Integer,HashMap<String,String>> results= new HashMap<Integer,HashMap<String,String>>();
            // no data this time
            if (!rs.next())
                return null;
            else
            {
                int j = 1;
                /* each simple HashMap represent a tuple from the resultSet: key=column-name, value=column-value
                 * the complex HashMap has all of the tuples: key=serial number from the resultSet(begins with 1)
                  * value=the tuple*/
                do
                {
                    HashMap<String,String> line = new HashMap<String,String>();
                    Iterator<String> iter = columnNames.iterator();
                    for (int i = 0; i < columnCount; i++)
                    {
                        String column = iter.next();
                        if(rs.getObject(column) != null)
                            line.put(column, rs.getObject(column).toString());
                        // no data in this column ofthat tuple
                        else
                            line.put(column, "null");
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
        conds.put("user_type", Integer.toString(userType));
        // gets registration fields according to the givven usetType
        HashMap<Integer,HashMap<String,String>> ret = getRowsFromTable(conds, "RegistrationFields");
        /* gets for each registration field the possible values from the proper table, if
           the fiel is not "free text". we put a json object that converted to string */
        for(int i = 1; i <= ret.size(); i++)
        {
            if(ret.get(i).get("get_possible_values_from") == "null")
                continue;
            String tableName = ret.get(i).get("get_possible_values_from");
            JSONObject jo;
            // field that has few possible values from Enum table
            if(tableName.substring(0, 5) == "Enum.")
            {
                ArrayList<String> l = new ArrayList<String>();
                l.add("enum_value");
                HashMap<String, String> conds1 = new HashMap<String, String>();
                conds1.put("table_name", "'" + tableName.split(".")[1] + "'");
                conds1.put("column_name", "'" + tableName.split(".")[2] + "'");
                jo = new JSONObject(selectFromTable("Enum", l, conds1));
            }
            else
                jo = new JSONObject(getRowsFromTable(null, tableName));
            ret.get(i).remove("get_possible_values_from");
            ret.get(i).put("get_possible_values_from", jo.toString());
        }
        return ret;
    }

    public HashMap<String,String> getUserByParameter(HashMap<String,String> whereConditions)
    {
        String conditions = "";
        int numOfConditions = whereConditions.size();
        Set<String> keys = whereConditions.keySet();
        Iterator<String> iter = keys.iterator();
        /* creates where coditions for sql query. each key in the input here should be in this format:
           table-name.column-name */
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
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            statement = connection.createStatement();
            // gets basic data about the user
            rs = statement.executeQuery("SELECT DISTINCT * FROM P_CommunityMembers " +
                            "INNER JOIN MembersLoginDetails ON P_CommunityMembers.community_member_id=MembersLoginDetails.community_member_id "
                   + "WHERE " + conditions);
            // no user exists for the givven conditions
            if(!rs.next())
                return null;
            String cmid = rs.getObject("community_member_id").toString();
            // gets the userType by the user's cmid
            int userType = getUserType(cmid);
            statement = connection.createStatement();
            // gets all important data about Patient user
            if(userType == 0)
                rs = statement.executeQuery("SELECT DISTINCT * FROM " + "P_CommunityMembers INNER JOIN "
                        + "P_Patients ON P_CommunityMembers.community_member_id=P_Patients.community_member_id "
                        + "INNER JOIN P_EmergencyContact ON P_Patients.community_member_id=P_EmergencyContact.community_member_id "
                        + "INNER JOIN MembersLoginDetails ON P_EmergencyContact.community_member_id=MembersLoginDetails.community_member_id "
                        + "INNER JOIN P_Supervision ON P_Patients.patient_id=P_Supervision.patient_id "
                        + "INNER JOIN P_Prescriptions ON P_Supervision.patient_id=P_Prescriptions.patient_id "
                        + "INNER JOIN P_Medications ON P_Medications.medication_num=P_Prescriptions.medication_num "
                        + "INNER JOIN P_Diagnosis ON P_Prescriptions.patient_id=P_Diagnosis.patient_id "
                        + "INNER JOIN M_MedicalConditions ON M_MedicalConditions.medical_condition_id=P_Diagnosis.medical_condition_id "
                        + "INNER JOIN Availability ON Availability.community_member_id=P_CommunityMembers.community_member_id "
                        + "INNER JOIN P_StatusLog ON MembersLoginDetails.community_member_id=P_StatusLog.community_member_id "
                        + "INNER JOIN P_Statuses ON P_StatusLog.status_num=P_Statuses.status_num " +
                        "WHERE " + conditions + " ORDER BY " + "P_StatusLog.date_from");
            // gets all important data about Doctor or ems user
            else
                rs = statement.executeQuery("SELECT DISTINCT * FROM " + "P_CommunityMembers INNER JOIN "
                        + "P_Doctors ON P_CommunityMembers.community_member_id=P_Doctors.community_member_id "
                        + "INNER JOIN P_EmergencyContact ON P_Doctors.community_member_id=P_EmergencyContact.community_member_id "
                        + "INNER JOIN MembersLoginDetails ON P_EmergencyContact.community_member_id=MembersLoginDetails.community_member_id "
                        + "INNER JOIN P_StatusLog ON MembersLoginDetails.community_member_id=P_StatusLog.community_member_id "
                        + "INNER JOIN P_Statuses ON P_StatusLog.status_num=P_Statuses.status_num "
                        + "INNER JOIN MP_MedicalPersonnel ON MP_MedicalPersonnel.community_member_id=P_Doctors.community_member_id "
                        + "INNER JOIN MP_Certification ON MP_MedicalPersonnel.medical_personnel_id=MP_Certification.medical_personnel_id "
                        + "INNER JOIN MP_Specialization ON MP_Specialization.specialization_id=MP_Certification.specialization_id "
                        + "INNER JOIN MP_Affiliation ON MP_MedicalPersonnel.medical_personnel_id=MP_Affiliation.medical_personnel_id "
                        + "INNER JOIN MP_Positions ON MP_Positions.position_num=MP_Affiliation.position_num "
                        + "INNER JOIN MP_Organizations ON MP_Organizations.organization_id=MP_Affiliation.organization_id "
                        + "INNER JOIN MP_OrganizationTypes ON MP_Organizations.organization_type_num=MP_OrganizationTypes.organization_type_num "
                        + "INNER JOIN Availability ON Availability.community_member_id=P_CommunityMembers.community_member_id "
                        + "WHERE " + conditions + " ORDER BY " + "P_StatusLog.date_from");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<String>();
            // gets all column names from the executed query
            for (int i = 1; i <= columnCount; i++ )
                columnNames.add(rsmd.getColumnName(i));
            // no user for the givven conditions is exists
            if (!rs.next())
                return null;
            else
            {
                HashMap<String,String> user = new HashMap<String,String>();;
                // gets the data about the user:key=column-name, value=column-value-needs the most updated data
                do
                {
                    user.clear();
                    iter = columnNames.iterator();
                    for (int i = 0; i < columnCount; i++)
                    {
                        String column = iter.next();
                        // no need for duplications or data about dates in this system
                        if((!user.containsKey(column)) && (column != "date_from") && (column != "date_to"))
                        {
                            if (rs.getObject(column) != null)
                                user.put(column, rs.getObject(column).toString());
                            // no data about the user in this column
                            else
                                user.put(column, "null");
                        }
                    }
                }while (rs.next());
                // for patient user-gets his doctor's license
                if(userType == 0)
                {
                    rs = statement.executeQuery("SELECT DISTINCT * FROM " + "P_CommunityMembers INNER JOIN "
                            + "P_Patients ON P_CommunityMembers.community_member_id=P_Patients.community_member_id "
                            + "INNER JOIN P_Supervision ON P_Patients.patient_id=P_Supervision.patient_id "
                            + "INNER JOIN P_Doctors ON P_Supervision.doctor_id=P_Doctors.doctor_id " +
                            "INNER JOIN MembersLoginDetails ON P_CommunityMembers.community_member_id=MembersLoginDetails.community_member_id " +
                            "WHERE " + conditions + " ORDER BY " + "P_StatusLog.date_from");
                    user.put("doc_license_number", rs.getObject("doc_license_number").toString());
                }
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

    public int getUserType(String cmid)
    {
        ResultSet rs = null;
        try {
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            statement = connection.createStatement();
            // gets the userType by cmid
            rs = statement.executeQuery("SELECT DISTINCT * FROM P_TypeLog " +
                    "WHERE community_member_id=" + cmid + " AND date_to IS NULL");
            rs.next();
            return rs.getInt("user_type");
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace(); return -1;}
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
        int CMID = Integer.parseInt(updates.get("community_member_id"));
        updates.remove("community_member_id");
        String Supdates = "";
        int numOfUpdates = updates.size();
        Set<String> keys = updates.keySet();
        Iterator<String> iter = keys.iterator();
        // gets personal updates for this user. the input format should be:key=column-name,value=column-new-value
        String key = iter.next();
        Supdates = key + "=" + updates.get(key);
        for (int i = 1; i < numOfUpdates; i++)
        {
            key = iter.next();
            Supdates += ", " + key + "=" + updates.get(key);
        }
        // update user's personal details
        try
        {
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            statement = connection.createStatement();
            statement.execute("UPDATE " +  "P_CommunityMembers SET " +
                    Supdates + " WHERE community_member_id=" + Integer.toString(CMID));
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
        // gets all data about specific frequency
        return selectFromTable("Frequencies", null, kindOfFrequency);
    }

    public HashMap<Integer,HashMap<String,String>> getDefaultInEmergency(String state)
    {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("state", "'" + state + "'");
        ArrayList<String> select = new ArrayList<String>();
        select.add("default_caller");
        // gets the default caller in emergency event according to the givven state
        return selectFromTable("DefaultCallerSettings", select, cond);
    }

    public HashMap<String,String> getRejectCodes()
    {
        // gets all reject codes of patient that can givven by doctor
        HashMap<Integer,HashMap<String,String>> rejectCodes = getRowsFromTable(null, "RejectCodes");
        int numOfCodes = rejectCodes.size();
        HashMap<String,String> codes = new HashMap<String,String>();
        Collection<HashMap<String,String>> col = rejectCodes.values();
        Iterator<HashMap<String,String>> iter = col.iterator();
        // The returned HashMap will be in this format:key=id-of-reject-code, value=the-reject-code
        for(int i = 0; i < numOfCodes; i++)
        {
            HashMap<String,String> m = iter.next();
            codes.put(m.get("id"), m.get("description"));
        }
        return codes;
    }

    // gets value of enum by the enum number(or the opposite) from specific table and column
    public HashMap<Integer,HashMap<String,String>> getFromEnum(HashMap<String,String> cond)
    {
        return selectFromTable("Enum", null, cond);
    }

    public ArrayList<String> getWaitingPatientsCMID(int docCMID)
    {
        ResultSet rs = null;
        ResultSet rs1 = null;
        try
        {
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            statement = connection.createStatement();
            // gets all related patients for this doctor
            rs = statement.executeQuery("SELECT DISTINCT * FROM " + "P_Doctors INNER JOIN "+
                    "P_Supervision ON P_Doctors.doctor_id=P_Supervision.doctor_id "
                    + "WHERE P_Doctors.community_member_id="
                    + Integer.toString(docCMID));
            // no patient related for this doctor
            if(!rs.next())
                return null;
            else
            {
                ArrayList<String> res = new ArrayList<String>();
                int numOfPatients = 0;
                do
                {
                    int patientID = rs.getInt("patient_id");
                    Statement statement2 = connection.createStatement();
                    // gets all relevant data about related patient that waits for doctor's approval
                    rs1 = statement2.executeQuery("SELECT DISTINCT * FROM " + "P_Patients INNER JOIN "
                            + "P_StatusLog ON P_Patients.community_member_id=P_StatusLog.community_member_id"
                            + "INNER JOIN P_Statuses ON P_Statuses.status_num=P_StatusLog.status_num"
                            + " WHERE P_Patients.patient_id=" + Integer.toString(patientID) +
                            " AND P_Statuses.status_name='verifying details'");
                    // this patient is not waiting for doctor's approval
                    if (!rs1.next())
                        continue;
                    // gets patient's cmid
                    else
                    {
                        numOfPatients++;
                        res.add(Integer.toString(rs1.getInt("community_member_id")));
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
        cond.put("community_member_id", Integer.toString(CMID));
        cond.put("field_name", fieldName);
        // updates refresh of specific field of spesific user to be urgent
        updateTable("RefreshDetailsTime", cond, "urgent", Integer.toString(urgentBit));
        updateTable("RefreshDetailsTime", cond, "last_update_time", Calendar.getInstance());
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
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
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
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
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
        where.put("community_member_id", Integer.toString(cmid));
        List<String> columns = Arrays.asList("community_member_id");
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("P_CommunityMembers", columns, where);
        return (res.size() != 0);
    }

    public boolean isEmailMemberExists(int cmid) {
        HashMap<String,String> where = new HashMap<String,String>();
        where.put("community_member_id", Integer.toString(cmid));
        List<String> columns = Arrays.asList("community_member_id");
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("P_CommunityMembers", columns, where);
        return (res.size() != 0);
    }


    public int addNewCommunityMember(HashMap<String,String> details) {
        /* - I'm assuming the fields 'doctor_id' (from p_doctors) will
             be in the form 'TABLENAME.doctor_id' to prevent ambiguity.
             e.g. p_supervision.doctor_id

           - Because of the multiple occurrences of the field 'date_to' and
             mainly because the lack of actual meaning of it, regarding the registration
             process, a simple 'date_to' is expected

           - output: the createn cmid
         */
        int cmid = -1;
        try {
            // Validate connection
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();

            // Insert basic details and get the new cmid
            PreparedStatement stmt = connection.prepareStatement("insert into P_CommunityMembers (external_id, external_id_type, first_name, last_name,\n" +
                    "birth_date, gender, state, city, street, house_number, zip_code, home_phone_number, mobile_phone_number," +
                    "email_address) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, details.get("external_id"));
            stmt.setString(2, details.get("external_id_type"));
            stmt.setString(3, details.get("first_name"));
            stmt.setString(4, details.get("last_name"));
            stmt.setString(5, details.get("birth_date"));
            stmt.setString(6, details.get("gender"));
            stmt.setString(7, details.get("state"));
            stmt.setString(8, details.get("city"));
            stmt.setString(9, details.get("street"));
            stmt.setString(10, details.get("house_number"));
            stmt.setString(11, details.get("zip_code"));
            stmt.setString(12, details.get("home_phone_number"));
            stmt.setString(13, details.get("mobile_phone_number"));
            stmt.setString(14, details.get("email_address"));

            stmt.executeUpdate();

            // Get the new primary key
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                cmid = rs.getInt(1);
            } else{
                // There was a problem inserting the new member
                return -1;
            }
            stmt.close();

            // Insert login details
            stmt = connection.prepareStatement("INSERT INTO MembersLoginDetails (community_member_id, password, email_address)" +
                    " VALUES (?,?,?)");
            stmt.setInt(1, cmid);
            stmt.setString(2, details.get("password"));
            stmt.setString(3, details.get("email_address"));
            stmt.executeUpdate();
            stmt.close();

            // Insert contact info
            stmt = connection.prepareStatement("INSERT INTO P_EmergencyContact (community_member_id, contact_phone) VALUES (?,?)");
            stmt.setInt(1, cmid);
            stmt.setString(2, details.get("contact_phone"));
            stmt.executeQuery();
            stmt.close();

            // Insert reg_id
            stmt = connection.prepareStatement("INSERT INTO RegIDs (reg_id, community_member_id) VALUES (?,?)");
            stmt.setString(1, details.get("reg_id"));
            stmt.setInt(2, cmid);
            stmt.executeQuery();
            stmt.close();

            // Insert availability hours
            stmt = connection.prepareStatement(" INSERT INTO Availability (hour_from, minutes_from, hour_to, minutes_to)" +
                    " VALUES (?,?,?,?)");
            stmt.setInt(1, Integer.parseInt(details.get("hour_from")));
            stmt.setInt(2, Integer.parseInt(details.get("minutes_from")));
            stmt.setInt(3, Integer.parseInt(details.get("hour_to")));
            stmt.setInt(4, Integer.parseInt(details.get("minutes_to")));
            stmt.executeQuery();
            stmt.close();


            /* Insert the details based on the user type */
            int userType = Integer.parseInt(details.get("user_type"));

            // Insert to type log
            stmt = connection.prepareStatement("INSERT INTO P_TypeLog (user_type, community_member_id, date_to) VALUES (?,?,?)");

            stmt.setInt(1, Integer.parseInt(details.get("user_type")));
            stmt.setInt(2, cmid);
            stmt.setString(3, details.get("date_to"));

            stmt.executeUpdate();
            stmt.close();


            switch(userType){
                case 0:
                    // Patient
                    stmt = connection.prepareStatement("INSERT INTO p_patients (community_member_id) VALUES (?)"
                            ,  Statement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, cmid);
                    stmt.executeUpdate();
                    // Get the new patient id
                    rs = stmt.getGeneratedKeys();
                    int patientID;
                    if (rs.next()) {
                        patientID = rs.getInt(1);
                    } else{
                        // There was a problem inserting the new member
                        return -1;
                    }
                    stmt.close();

                    // Supervision
                    stmt = connection.prepareStatement("INSERT INTO P_Supervision (doctor_id, patient_id, date_to) " +
                            "VALUES (?,?,?)");
                    stmt.setInt(1, Integer.parseInt(details.get("p_supervision.doctor_id")));
                    stmt.setInt(2, patientID);
                    stmt.setString(3, details.get("date_to"));
                    stmt.executeUpdate();
                    stmt.close();

                    stmt = connection.prepareStatement("INSERT INTO P_Prescriptions (medication_num, dosage," +
                            "medical_condition_id, doctor_id, date_to, patient_id) VALUES (?,?,?,?,?,?)");
                    stmt.setInt(1, Integer.parseInt(details.get("medication_num")));
                    stmt.setFloat(2, Float.parseFloat(details.get("dosage")));
                    stmt.setInt(3, Integer.parseInt(details.get("medical_condition_id")));
                    stmt.setInt(4, Integer.parseInt(details.get("p_prescriptions.doctor_id")));
                    stmt.setString(5, details.get("date_to"));
                    stmt.setInt(6, patientID);
                    stmt.executeUpdate();
                    stmt.close();

                    stmt = connection.prepareStatement("INSERT INTO P_Diagnosis (patient_id, medical_condition_id," +
                            "doctor_id, date_to) VALUES (?,?,?,?)");
                    stmt.setInt(1, patientID);
                    stmt.setInt(2, Integer.parseInt(details.get("medical_condition_id")));
                    stmt.setInt(3, Integer.parseInt(details.get("p_diagnosis.doctor_id")));
                    stmt.setString(4, details.get("date_to"));
                    stmt.executeUpdate();
                    stmt.close();

                    break;

                default:
                    // Doctor or EMS
                    stmt = connection.prepareStatement("INSERT INTO P_Doctors (first_name, last_name, doc_license_number," +
                            "community_member_id) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, details.get("first_name"));
                    stmt.setString(2, details.get("last_name"));
                    stmt.setInt(3, Integer.parseInt(details.get("doc_license_number")));
                    stmt.setInt(4, cmid);
                    stmt.executeUpdate();
                    rs = stmt.getGeneratedKeys();
                    int doctorID;
                    if (rs.next()) {
                        doctorID = rs.getInt(1);
                    } else{
                        // There was a problem inserting the new member
                        return -1;
                    }
                    stmt.close();

                    stmt = connection.prepareStatement("INSERT INTO MP_Affiliation (organization_id, medical_personnel_id," +
                            "position_num, date_to) VALUES (?,?,?,?)");
                    stmt.setInt(1, Integer.parseInt(details.get("organization_id")));
                    stmt.setInt(2, doctorID);
                    stmt.setInt(3, Integer.parseInt(details.get("position_num")));
                    stmt.setString(4, details.get("date_to"));
                    stmt.executeUpdate();
                    stmt.close();

                    stmt = connection.prepareStatement("INSERT INTO MP_Certification (certification_external_id," +
                            "medical_personnel_id, date_to, specialization_id) VALUES (?,?,?,?)");
                    stmt.setInt(1, Integer.parseInt(details.get("certification_external_id")));
                    stmt.setInt(2, doctorID);
                    stmt.setString(3, details.get("date_to"));
                    stmt.setInt(4, Integer.parseInt(details.get("specialization_id")));
                    stmt.executeUpdate();
                    stmt.close();

                    break;


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cmid;

    }

    // expected format of the state:'state-name'
    public int getAuthenticationMethod(String state) {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("state", state);
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("AuthenticationMethod", Arrays.asList("method"), cond);
        // returns authentication method in this state:mail,SMS...
        if (res.size() != 0){
            Collection<HashMap<String,String>> coll = res.values();
            return Integer.parseInt(coll.iterator().next().get("method"));
        }
        return -1;
    }

    // expected format of the state:'state-name'
    public HashMap<String,String> getEmailOfDoctorsAuthorizer(String state)
    {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("state", state);
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("DoctorAuthorizers", Arrays.asList("email_address"), cond);
        // returns the mail of doctors authorizer
        if (res.size() != 0){
            Collection<HashMap<String,String>> coll = res.values();
            return coll.iterator().next();
        }
        return null;
    }

    // expected format of the email:'mail-address'
    public HashMap<String,String> getLoginDetails(String email) {
        HashMap<String,String> cond = new HashMap<String,String>();
        cond.put("email_address", email);
        HashMap<Integer,HashMap<String,String>> res =
                selectFromTable("MembersLoginDetails", null, cond);
        // returns login details of the givven mail if exists in the system
        if (res.size() != 0){
            Collection<HashMap<String,String>> coll = res.values();
            return coll.iterator().next();
        }
        return null;
    }

    // NOT TESTED
    public void updateLastRefreshTime(HashMap<String,String> params) {
        // Get all the relevant values from parms
        int cmid = Integer.parseInt(params.get("community_member_id"));
        String fieldToUpdate = params.get("field_name");
        String tsS = params.get("last_update_time");
        Timestamp ts =  Timestamp.valueOf(tsS);
        // Create the where clause json
        HashMap<String,String> whereMap = new HashMap<String,String>();
        whereMap.put("community_member_id", Integer.toString(cmid));
        whereMap.put("field_name", fieldToUpdate);
        // Update the datetime field
        updateTable("RefreshDetailsTime", whereMap, "last_update_time", ts);
    }

    public HashMap<Integer,HashMap<String,String>> getAllRefreshTimes() {
        return selectFromTable("RefreshDetailsTime", null, null);
    }

    // the status format should be:'status-name'
    public void updateStatus(int cmid, String oldStatus, String newStatus)
    {
        try
        {
            HashMap<String,String> cond = new HashMap<String,String>();
            HashMap<Integer, HashMap<String, String>> s;
            Collection<HashMap<String, String>> val;
            String statusNum;
            if(oldStatus != null)
            {
                cond.put("status_name", oldStatus);
                // gets status number of the givven old status
                s = getRowsFromTable(cond, "P_Statuses");
                val = s.values();
                statusNum = val.iterator().next().get("status_num");
                if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                    connect();
                // closed time of this user in the old status
                statement = connection.createStatement();
                statement.execute("UPDATE P_StatusLog SET date_to=CURRENT_TIMESTAMP" +
                        " WHERE" + " status_num=" + statusNum + " AND community_member_id="
                        + Integer.toString(cmid));
            }
            cond.clear();
            cond.put("status_name", newStatus);
            // gets status number of the givven new status
            s = getRowsFromTable(cond, "P_Statuses");
            val = s.values();
            statusNum = val.iterator().next().get("status_num");
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            statement = connection.createStatement();
            // updates the givven user with the givven new status
            statement.execute("INSERT INTO P_StatusLog (status_num,community_member_id) VALUES (" +
                    statusNum + "," + Integer.toString(cmid) + ")");
        }
        catch (SQLException e) {e.printStackTrace();}
        finally
        {
            releaseResources(statement, connection);
        }
    }

    // expected regID format:'redID'
    public void insertRegID(String regId, int cmid)
    {
        try
        {
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            statement = connection.createStatement();
            // inserts the regid to the regIDs table
            statement.execute("INSERT INTO RegIDs (reg_id,community_member_id) VALUES (" +
                    regId + "," + Integer.toString(cmid) + ")");
        }
        catch (SQLException e) {e.printStackTrace();}
        finally
        {
            releaseResources(statement, connection);
        }
    }

    // return user's regID in a HashMap according to his cmid
    public HashMap<Integer,HashMap<String,String>> getRegIDsOfUser(int cmid)
    {
        HashMap<String,String> conds = new HashMap<String,String>();
        conds.put("community_member_id", Integer.toString(cmid));
        return getRowsFromTable(conds, "RegIDs");
    }

    public void deleteUser(int cmid)
    {
        // tables that relevant for each user in this community
        String[] tables =  {"P_StatusLog", "P_DeviceLog", "P_EmergencyContact", "P_TypeLog",
                            "MembersLoginDetails", "RefreshDetailsTime", "RegIDs"
                            , "P_Relations"};
        // tables that relevant for each Patient or Doctor user
        String[] pTables = {"P_Supervision", "P_Prescriptions", "P_Diagnosis", "P_Relations", "P_Patients", "P_Doctors"};
        // tables that relevant for each Doctor or ems user
        String[] mpTables = {"MP_Affiliation", "MP_Certification", "MP_MedicalPersonnel"};
        try
        {
            if (!(connection != null && !connection.isClosed() && connection.isValid(1)))
                connect();
            HashMap<String,String> cond = new HashMap<String,String>();
            cond.put("community_member_id", Integer.toString(cmid));
            // gets userTypeID according to the cmid, if exists
            HashMap<Integer,HashMap<String,String>> patientID = getRowsFromTable(cond, "P_Patients");
            HashMap<Integer,HashMap<String,String>> docID = getRowsFromTable(cond, "P_Doctors");
            HashMap<Integer,HashMap<String,String>> medPersonelID = getRowsFromTable(cond, "MP_MedicalPersonnel");
            // deletes the user from the data-base
            for(int i = 0; i < 8; i++)
            {
                statement = connection.createStatement();
                statement.execute("DELETE FROM " + tables[i] +
                        " WHERE community_member_id=" + Integer.toString(cmid));
            }
            statement = connection.createStatement();
            statement.execute("DELETE FROM P_Buddies" +
                    " WHERE community_member_id1=" + Integer.toString(cmid) +
                    " OR community_member_id2=" + Integer.toString(cmid));
            // deletes patient or doctor from relevant tables
            if((patientID != null) || (docID != null))
            {
                String id = patientID.get(1).get("patient_id");
                for(int i = 0; i < 6; i++)
                {
                    statement = connection.createStatement();
                    statement.execute("DELETE FROM " + pTables[i] +
                            " WHERE patient_id=" + id);
                }
            }
            // deletes ems or doctor from relevant tables
            if(medPersonelID != null)
            {
                String id = medPersonelID.get(1).get("medical_personnel_id");
                for(int i = 0; i < 3; i++) {
                    statement = connection.createStatement();
                    statement.execute("DELETE FROM " + mpTables[i] +
                            " WHERE medical_personnel_id=" + id);
                }
            }
            statement = connection.createStatement();
            statement.execute("DELETE FROM P_CommunityMembers" +
                    " WHERE community_member_id=" + Integer.toString(cmid));
        }
        catch (SQLException e) {e.printStackTrace();}
        finally
        {
            releaseResources(statement, connection);
        }

    }
}
