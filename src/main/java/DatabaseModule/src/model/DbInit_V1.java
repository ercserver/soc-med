package DatabaseModule.src.model;

import DatabaseModule.src.api.IDbInit_model;

import java.sql.*;


/**
 * Created by NAOR on 06/04/2015.
 */
public class DbInit_V1 implements IDbInit_model {
    static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static final String DB_URL = "jdbc:sqlserver://socmedserver.mssql.somee.com;";//databaseName=ercserver-socmed";
    static final String DBName = "socmedserver";
    static final private String USERNAME = "saaccount";
    static final private String PASS = "saaccount";
    private static Connection connection = null;
    private static Statement statement = null;

    public  void initializeAndConnect()
    {
        try {
            connect();
            createTables();
            /**/
            releaseResources(statement, connection);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }



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
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    private  void createTables(){

        try {
            statement.clearBatch();
            connection.setAutoCommit(true);

            statement.addBatch("CREATE TABLE Enum ("
                    +"table_name VARCHAR(30) NOT NULL,"
                    +"column_name VARCHAR(30) NOT NULL,"
                    +"enum_code INT NOT NULL,"
                    +"enum_value VARCHAR(30) NOT NULL)");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_MedicalConditions ("
                    +"medical_condition_id INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"medical_condition_description VARCHAR(100))");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_BrandNames ("
                    +"brand_name_id INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"brand_name_external_id VARCHAR(30),"
                    +"brand_name_description VARCHAR(30),"
                    +"manufacturer VARCHAR(30))");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_Indications ("
                    +"indication_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"medical_condition_id INT NOT NULL FOREIGN KEY REFERENCES M_MedicalConditions(medical_condition_id),"
                    +"brand_name_id INT NOT NULL FOREIGN KEY REFERENCES M_BrandNames(brand_name_id))");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_ActiveComponents ("
                    +"active_component_id INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"active_component_description VARCHAR(30) NOT NULL)");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_Compositions ("
                    +"composiotion_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"active_component_id INT NOT NULL FOREIGN KEY REFERENCES M_ActiveComponents (active_component_id),"
                    +"brand_name_id INT NOT NULL FOREIGN KEY REFERENCES M_BrandNames(brand_name_id),"
                    +"quantity INT NOT NULL ,"
                    +"unit_of_measure INT NOT NULL)");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_Substitutives ("
                    +"subsitutive_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"brand_name_id1 INT NOT NULL FOREIGN KEY REFERENCES M_BrandNames(brand_name_id),"
                    +"brand_name_id2 INT NOT NULL FOREIGN KEY REFERENCES M_BrandNames(brand_name_id),"
                    +"conversion_ratio FLOAT NOT NULL,"
                    +"compliance VARCHAR(10) NOT NULL)"); // --Enum(full, partial))
//            connection.commit();

            statement.addBatch("CREATE TABLE P_Devices ("
                    +"device_internal_id INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"device_external_id VARCHAR(30),"
                    +"device_type INT," // --Enum(Smartphone, Computer,Tablet,...)
                    +"device_model VARCHAR(30) NOT NULL,"
                    +"os VARCHAR(30) NOT NULL,"
                    +"os_version VARCHAR(30) NOT NULL,"
                    +"cerc_app_version VARCHAR(30) NOT NULL,"
                    +"gps_enabled INT NOT NULL,"
                    +"nfc_enabled INT NOT NULL,"
                    +"sim_enabled INT NOT NULL,"
                    +"installation_date Datetime NOT NULL,"
                    +"last_update_date Datetime )");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Statuses ("
                    +"status_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"status_name VARCHAR(30) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE Table P_CommunityMembers("
                    +"community_member_id INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"external_id VARCHAR(25) NOT NULL,"
                    +"external_id_type INT NOT NULL," // --Enum = {IdCard, Passport,...}
                    +"first_name VARCHAR(50) NOT NULL,"
                    +"last_name VARCHAR(50) NOT NULL,"
                    +"birth_date DATE NOT NULL,"
                    +"gender INT NOT NULL, " //--Enum = {male, female}
                    +"state VARCHAR(50) NOT NULL,"
                    +"city VARCHAR(50) NOT NULL,"
                    +"street VARCHAR(50) NOT NULL,"
                    +"house_number INT,"
                    +"zip_code VARCHAR(15),"
                    +"home_phone_number VARCHAR(20),"
                    +"mobile_phone_number VARCHAR(20) NOT NULL,"
                    +"email_address VARCHAR(50) NOT NULL,"
                    +"member_since DATETIME NOT NULL DEFAULT current_timestamp)");
            //connection.commit();

            statement.addBatch("CREATE Table P_Patients("
                    +"patient_id INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id))");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_StatusLog ("
                    +"status_history_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"status_num INT NOT NULL FOREIGN KEY REFERENCES P_Statuses(status_num),"
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"date_from Datetime NOT NULL DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_DeviceLog ("
                    +"status_history_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"status_num INT NOT NULL FOREIGN KEY REFERENCES P_Statuses(status_num),"
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"date_from Datetime NOT NULL DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Buddies ("
                    +"relationship_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"community_member_id1 INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"community_member_id2 INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"date_from Datetime NOT NULL DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();
            statement.executeBatch();

            createPatientsDBRelationTypes();

            statement.addBatch("CREATE TABLE P_Relations ("
                    +"relation_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"patient_id INT NOT NULL FOREIGN KEY REFERENCES P_Patients(patient_id),"
                    +"relation_type_num INT NOT NULL FOREIGN KEY REFERENCES P_RelationTypes(relation_type_num),"
                    +"date_from Datetime NOT NULL DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_EmergencyContact ("
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"contact_phone VARCHAR(20) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_TypeLog ("
                    +"type_history_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"user_type INT NOT NULL," // --Enum={patient, doctor, guardian, ems}
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"date_from Datetime NOT NULL DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Doctors ("
                    +"doctor_id INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"first_name VARCHAR(30) NOT NULL,"
                    +"last_name VARCHAR(30) NOT NULL,"
                    +"doc_license_number INT NOT NULL,"
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id))");
            //connection.commit();

            statement.addBatch("CREATE TABLE MembersLoginDetails("
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"password VARCHAR(30) NOT NULL,"
                    +"email_address VARCHAR(30) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE RefreshDetailsTime ("
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id),"
                    +"field_name VARCHAR(30) NOT NULL,"
                    +"last_update_time Datetime NOT NULL,"
                    +"urgent BIT DEFAULT 0)");
            //connection.commit();

            statement.addBatch("CREATE TABLE RegistrationFields ("
                    +"field_name VARCHAR(30) NOT NULL,"
                    +"type INT NOT NULL," // --Enum = {string, int, float}
                    +"user_type INT NOT NULL," // --Enum = {patient, doctor, guardian}
                    +"fields_group INT NOT NULL," // --Enum = {personal, medical, professional, preferences}
                    +"needs_verification BIT NOT NUll DEFAULT 0," // 0-no, 1-yes
                    +"is_required BIT NOT NUll DEFAULT 0,"//0-yes, 1-no
                    +"max_length INT NOT NUll DEFAULT 0,"
                    +"get_possible_values_from VARCHAR(50),"
                    +"serial_num INT NOT NUll," //begin with 1
                    +"insert_data_to VARCHAR(200),"
                    +"refresh_time INT)");
            //connection.commit();

            statement.addBatch("CREATE TABLE ServerUserNames ("
                    +"user_name VARCHAR(30) NOT NULL PRIMARY KEY,"
                    +"password VARCHAR(30) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Supervision ("
                    +"treatment_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"doctor_id INT NOT NULL FOREIGN KEY REFERENCES P_Doctors(doctor_id),"
                    +"patient_id INT NOT NULL FOREIGN KEY REFERENCES P_Patients(patient_id),"
                    +"date_from Datetime DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Medications ("
                    +"medication_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"medication_name VARCHAR(100) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Prescriptions ("
                    +"prescription_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"medication_num INT NOT NULL FOREIGN KEY REFERENCES P_Medications(medication_num),"
                    +"dosage FLOAT NOT NULL,"
                    +"doctor_id INT NOT NULL FOREIGN KEY REFERENCES P_Doctors(doctor_id),"
                    +"patient_id INT NOT NULL FOREIGN KEY REFERENCES P_Patients(patient_id),"
                    +"date_from Datetime DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Diagnosis ("
                    +"diagnosis_num INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"patient_id INT NOT NULL FOREIGN KEY REFERENCES P_Patients(patient_id),"
                    +"medical_condition_id INT NOT NULL FOREIGN KEY REFERENCES M_MedicalConditions(medical_condition_id),"
                    +"doctor_id INT NOT NULL FOREIGN KEY REFERENCES P_Doctors(doctor_id),"
                    +"date_from Datetime DEFAULT current_timestamp,"
                    +"date_to Datetime)");
            //connection.commit();
            statement.addBatch("CREATE TABLE RegIDs ("
                    +"reg_id VARCHAR(100) NOT NULL PRIMARY KEY,"
                    +"community_member_id INT NOT NULL)");

            statement.addBatch("CREATE TABLE Availability ("
                    +"hour_from INT NOT NULL,"
                    +"minutes_from INT NOT NULL,"
                    +"hour_to INT NOT NULL,"
                    +"minutes_to INT NOT NULL,"
                    +"community_member_id INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id))");

            statement.executeBatch();

            //createPatientDBDiagnosis();
            createMedicalPersonnelDBOrganizationTypes();
            createMedicalPersonnelDBSpecifalization();
            createMedicalPesonnelDBOrganizations();
            //createMedicalPesonnelDBMedicalPersonnel();
            createMedicalPersonnelDBCertification();
            createMedicalPesonnelDBPositions();
            createMedicalPesonnelDBAffiliation();
            createBuisnessLogicDBDecisionGivenAmbulaneETA();
            createOperationalDBActionTypes();
            createPatientsDBRelationTypes();
            createEventStatuses();
            createInvolvedCommunityMembers();
            createEmergencyEvents();
            connection.commit();
            createActionStatuses();
            createEmergencyEventActions();
            createOperationalDBEmergencyEventResponse();
            createAutomaticDispensers();
            createEmergencyMedicationUse();
            createUpdatesDB();
            createFrequencies();
            createRejectCodes();
            createAuthenticationMethod();
            createDoctorAuthorizers();
            createDefaultCallerSettings();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private  void createPatientDBDiagnosis()
    {
        //Connection connection = null;
        //Statement statement = null;
        ResultSet rs = null;
        try
        {
            // Connects to the server
            //connect(connection, statement);
            // Connects to the data-base
            //sqlServerDataSource ds = new MysqlConnectionPoolDataSource();
            //ds.setServerName("localhost");
            //ds.setDatabaseName("ServerDB");
            //connection = ds.getConnection("root", "");
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "P_Diagnosis", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE P_Diagnosis " +
                        "(DiagnosisNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " PatientID INTEGER not NULL FOREIGN KEY REFERENCES P_Patient(PatientID), " +
                        " MedicalConditionID INTEGER not NULL FOREIGN KEY REFERENCES P_MedicalConditions(MedicalConditionNum), " +
                        " DoctorID INTEGER not NULL FOREIGN KEY REFERENCES P_Doctors(InternalID), " +
                        " DateFrom DATE, " +
                        " DateTo DATE)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
            //releaseResources(rs, statement, connection);
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

    private  void createMedicalPersonnelDBSpecifalization()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "MP_Specialization", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE MP_Specialization " +
                        "(specialization_id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " specialization_description VARCHAR(3000))");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createMedicalPersonnelDBCertification()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "MP_Certification", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE MP_Certification " +
                        "(certification_internal_id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " certification_external_id INTEGER not NULL, " +
                        " medical_personnel_id INTEGER not NULL FOREIGN KEY REFERENCES MP_MedicalPersonnel(medical_personnel_id), " +
                        " date_from DATE not NULL DEFAULT current_timestamp, " +
                        " date_to DATE , " +
                        " specialization_id INTEGER not NULL FOREIGN KEY REFERENCES MP_Specialization(specialization_id))");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createMedicalPesonnelDBMedicalPersonnel()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "MP_MedicalPersonnel", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE MP_MedicalPersonnel " +
                        "(medical_personnel_id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " community_member_id INTEGER not NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id))");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createMedicalPesonnelDBAffiliation()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "MP_Affiliation", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE MP_Affiliation " +
                        "(affiliation_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " organization_id INTEGER not NULL FOREIGN KEY REFERENCES MP_Organizations(organization_id), " +
                        " medical_personnel_id INTEGER not NULL FOREIGN KEY REFERENCES MP_MedicalPersonnel(medical_personnel_id), " +
                        " position_num INTEGER not NULL FOREIGN KEY REFERENCES MP_Positions(position_num), " +
                        " date_from DATETime not NULL DEFAULT current_timestamp, " +
                        " date_to DATETime)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createMedicalPesonnelDBPositions()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "MP_Positions", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE MP_Positions " +
                        "(position_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " position_description VARCHAR(50) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createMedicalPesonnelDBOrganizations()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "MP_Organizations", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE MP_Organizations " +
                        "(organization_id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY," +
                        "organization_description VARCHAR(50) not NULL," +
                        "organization_type_num INTEGER not NULL FOREIGN KEY REFERENCES MP_OrganizationTypes(organization_type_num)," +
                        "org_state VARCHAR(50) not NULL," +
                        "org_city VARCHAR(50) not NULL," +
                        "org_street VARCHAR(50) not NULL," +
                        "org_house INTEGER not NULL," +
                        "org_phone_number VARCHAR(50) not NULL," +
                        "fax_number VARCHAR(50) not NULL," +
                        "email_address_of_organization VARCHAR(100) not NULL," +
                        "web_site VARCHAR(100) not NULL," +
                        "remarks VARCHAR(100))");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createBuisnessLogicDBDecisionGivenAmbulaneETA()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "B_DecisionGivenAmbulaneETA", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE B_DecisionGivenAmbulaneETA " +
                        "(medical_condition_id INTEGER NOT NULL FOREIGN KEY REFERENCES M_MedicalConditions(medical_condition_id), " +
                        " state  VARCHAR(50) not NULL, " +
                        " patient_age VARCHAR(50) not NULL, " +
                        " ems_eta VARCHAR(50) not NULL, " +
                        " use_erc BIT not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createOperationalDBActionTypes()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_ActionTypes", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_ActionTypes " +
                        "(action_type_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " action_type_name  VARCHAR(100) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createOperationalDBEmergencyEventResponse()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_EmergencyEventResponse", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_EmergencyEventResponse " +
                        "(response_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " community_member_id INTEGER not NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id), " +
                        " event_id INTEGER not NULL FOREIGN KEY REFERENCES O_EmergencyEvents(event_id), " +
                        " emergency_event_num INTEGER not NULL, " +
                        " action_type_num INTEGER not NULL, " +
                        " eta_by_foot INTEGER not NULL, " +
                        " action_status_num INTEGER not NULL, " +
                        " eta_by_car INTEGER not NULL, " +
                        " created_date TIMESTAMP not NULL, " +
                        " x REAL not NULL, " +
                        " y REAL not NULL, " +
                        " request_sent_date DATETIME, " +
                        " response_date DATETIME, " +
                        " response_type VARCHAR(50) not NULL, " +
                        " transformation_mean INTEGER not NULL, " +  //enum: 0 for foot, 1 for car
                        " activation_date DATETIME, " +
                        " arrival_date DATETIME, " +
                        " result VARCHAR(100))");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createPatientsDBRelationTypes()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "P_RelationTypes", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE P_RelationTypes " +
                        "(relation_type_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " relation_type_description  VARCHAR(30) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createMedicalPersonnelDBOrganizationTypes()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "MP_OrganizationTypes", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE MP_OrganizationTypes " +
                        "(organization_type_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " organization_type_description  VARCHAR(30) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createEventStatuses()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_EventStatuses", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_EventStatuses " +
                        "(status_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " status_name  VARCHAR(30) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createEmergencyEvents()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_EmergencyEvents", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_EmergencyEvents " +
                        "(event_id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " create_by_member_id INTEGER not NULL FOREIGN KEY REFERENCES O_InvolvedCommunityMembers(internal_id), " +
                        " patient_id INTEGER not NULL, " +
                        " created_date TIMESTAMP not NULL, " +
                        " finished_date DATETIME, " +
                        " ems_member_id INTEGER not NULL FOREIGN KEY REFERENCES P_CommunityMembers(community_member_id), " +
                        " status_num INTEGER not NULL FOREIGN KEY REFERENCES O_EventStatuses(status_num), " +
                        " x REAL not NULL, " +
                        " y REAL not NULL, " +
                        " location_remark VARCHAR(100), " +
                        " patient_condition_remarks VARCHAR(300), " +
                        " last_action_time DATETIME, " +
                        " time_to_next_reminder INTEGER, " +
                        " memo VARCHAR(300))");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createEmergencyEventActions()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_EmergencyEventActions", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_EmergencyEventActions " +
                        "(emergency_action_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " event_id INTEGER not NULL FOREIGN KEY REFERENCES O_EmergencyEvents(event_id), " +
                        " action_type_num INTEGER not NULL FOREIGN KEY REFERENCES O_ActionTypes(action_type_num), " +
                        " action_status_num INTEGER not NULL FOREIGN KEY REFERENCES O_ActionStatus(action_status_num), " +
                        " created_date TIMESTAMP not NULL, " +
                        " finished_date DATETIME not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createEmergencyMedicationUse()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_EmergenctMedicationUse", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_EmergencyMedicationUse " +
                        "(emergency_medication_use_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " event_id INTEGER not NULL FOREIGN KEY REFERENCES O_EmergencyEvents(event_id), " +
                        " providing_member_id INTEGER not NULL FOREIGN KEY REFERENCES O_InvolvedCommunityMembers(internal_id), " +
                        " providing_dispenser_num INTEGER not NULL FOREIGN KEY REFERENCES O_AutomaticDispensers(dispensers_num), " +
                        " approved_by_id INTEGER not NULL, " +
                        " medication_num INTEGER not NULL FOREIGN KEY REFERENCES P_Medications(medication_num), " +
                        " approval_date DATETIME, " +
                        " medication_provision_date DATETIME, " +
                        " digital_signature_file VARCHAR(100))");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createActionStatuses()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_ActionStatus", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_ActionStatus " +
                        "(action_status_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " status_name VARCHAR(30) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createAutomaticDispensers()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_AutomaticDispensers", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_AutomaticDispensers " +
                        "(dispensers_num INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " dispensers_name VARCHAR(30) not NULL, " +
                        " dispensers_location VARCHAR(100) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createInvolvedCommunityMembers()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "O_InvolvedCommunityMembers", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE O_InvolvedCommunityMembers " +
                        "(internal_id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " external_id VARCHAR(30) not NULL, " +
                        " external_id_type INTEGER not NULL, " +  //enum:0 for id, 1 for passport num
                        " first_name VARCHAR(30) not NULL, " +
                        " last_name VARCHAR(30) not NULL, " +
                        " birthday_date DATE not NULL, " +
                        " gender INTEGER not NULL, " +  //enum:0 for male, 1 for female
                        " state VARCHAR(50) not NULL, " +
                        " city VARCHAR(50) not NULL, " +
                        " street VARCHAR(50) not NULL, " +
                        " house_number INTEGER not NULL, " +
                        " zip_code INTEGER, " +
                        " home_phone_number VARCHAR(50), " +
                        " mobile_phone_number VARCHAR(50) not NULL, " +
                        " email_address VARCHAR(100) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createUpdatesDB()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "UpdatesDB", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE UpdatesDB " +
                        "(id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " json INTEGER not NULL, " +
                        " community_member_id INTEGER not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createFrequencies()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "Frequencies", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE Frequencies " +
                        "(name VARCHAR(30) not NULL PRIMARY KEY, " +
                        " frequency REAL not NULL, " +
                        " medical_condition_id INT FOREIGN KEY REFERENCES M_MedicalConditions(medical_condition_id), " +
                        " state VARCHAR(50) not NULL, " +
                        " area VARCHAR(50), " +
                        " patient_age INTEGER)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createRejectCodes()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "RejectCodes", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE RejectCodes " +
                        "(id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " description VARCHAR(100) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private void createAuthenticationMethod()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "AuthenticationMethod", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE AuthenticationMethod " +
                        "(state VARCHAR(50) not NULL PRIMARY KEY, " +
                        " method INT not NULL)");   //enum:0 for mail, 1 for SMS, 2 for phoneCall
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private void createDoctorAuthorizers()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "DoctorAuthorizers", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE DoctorAuthorizers " +
                        "(state VARCHAR(50) not NULL PRIMARY KEY, " +
                        " email_address VARCHAR(100) not NULL)");
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

    private  void createDefaultCallerSettings()
    {
        ResultSet rs = null;
        try
        {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            // Checks the existence of the tables of the database
            rs = dbm.getTables(null, null, "DefaultCallerSettings", null);
            // The tables are not existing now-creates those
            if(!rs.next())
            {
                statement.executeUpdate("CREATE TABLE DefaultCallerSettings " +
                        "(state VARCHAR(50) not NULL PRIMARY KEY, " +
                        " default_caller INTEGER not NULL)"); // enum:0 for app, 1 for server
            }
        }
        // There was a fault with the connection to the server or with SQL
        catch (SQLException e) {e.printStackTrace();}
        // Releases the resources of this method
        finally
        {
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

