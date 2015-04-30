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
    private static String SCHEMA = "Ohad";

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

    private  void createTables(){

        try {
            statement.clearBatch();
            connection.setAutoCommit(true);

            statement.addBatch("CREATE TABLE Enum ("
                    +"TableName VARCHAR(30) NOT NULL,"
                    +"ColumnName VARCHAR(30) NOT NULL,"
                    +"EnumCode INT NOT NULL,"
                    +"EnumValue VARCHAR(30) NOT NULL)");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_MedicalConditions ("
                    +"MedicalConditionNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"MedicalConditionDescription VARCHAR(100))");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_BrandNames ("
                    +"BrandNameInternalID INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"BrandNameExternalID VARCHAR(30),"
                    +"BrandNameDescription VARCHAR(30),"
                    +"Manufacturer VARCHAR(30))");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_Indications ("
                    +"IndicationNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"MedicalConditionID INT NOT NULL FOREIGN KEY REFERENCES m_MedicalConditions(MedicalConditionNum),"
                    +"BrandNameID INT NOT NULL FOREIGN KEY REFERENCES m_BrandNames(BrandNameInternalID))");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_ActiveComponents ("
                    +"ActiveComponentID INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"ActiveComponentDescription VARCHAR(30) NOT NULL)");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_Compositions ("
                    +"ComposiotionNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"ActiveComponentID INT NOT NULL FOREIGN KEY REFERENCES m_ActiveComponents (ActiveComponentID),"
                    +"BrandNameID INT NOT NULL FOREIGN KEY REFERENCES m_BrandNames(BrandNameInternalID),"
                    +"Quantity INT NOT NULL ,"
                    +"UnitOfMeasure INT NOT NULL)");
//            connection.commit();

            statement.addBatch("CREATE TABLE M_Substitutives ("
                    +"SubsitutiveNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"BrandNameID1 INT NOT NULL FOREIGN KEY REFERENCES m_BrandNames(BrandNameInternalID),"
                    +"BrandNameID2 INT NOT NULL FOREIGN KEY REFERENCES m_BrandNames(BrandNameInternalID),"
                    +"ConversionRatio FLOAT NOT NULL,"
                    +"Compliance VARCHAR(10) NOT NULL)"); // --Enum(full, partial))
//            connection.commit();

            statement.addBatch("CREATE TABLE P_Devices ("
                    +"DeviceInternalID INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"DeviceExternalID VARCHAR(30),"
                    +"DeviceType INT," // --Enum(Smartphone, Computer,Tablet,...)
                    +"DeviceModel VARCHAR(30) NOT NULL,"
                    +"OS VARCHAR(30) NOT NULL,"
                    +"OSVersion VARCHAR(30) NOT NULL,"
                    +"CERCAppVersion VARCHAR(30) NOT NULL,"
                    +"GPSEnabled INT NOT NULL,"
                    +"NFCEnabled INT NOT NULL,"
                    +"SIMEnabled INT NOT NULL,"
                    +"InstallationDate Datetime NOT NULL,"
                    +"LastUpdateDate Datetime )");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Statuses ("
                    +"StatusNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"StatusName VARCHAR(30) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE Table P_CommunityMembers("
                    +"CommunityMemberID INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"ExternalID VARCHAR(25) NOT NULL,"
                    +"ExternalIDType INT NOT NULL," // --Enum = {IdCard, Passport,...}
                    +"FirstName VARCHAR(50) NOT NULL,"
                    +"LastName VARCHAR(50) NOT NULL,"
                    +"BirthDate DATE NOT NULL,"
                    +"Gender INT NOT NULL, " //--Enum = {male, female}
                    +"State VARCHAR(50) NOT NULL,"
                    +"City VARCHAR(50) NOT NULL,"
                    +"Street VARCHAR(50) NOT NULL,"
                    +"HouseNumber INT,"
                    +"ZipCode VARCHAR(15),"
                    +"HomePhoneNumber VARCHAR(20),"
                    +"MobilePhoneNumber VARCHAR(20) NOT NULL,"
                    +"EmailAddress VARCHAR(50) NOT NULL,"
                    +"MemberSince DATETIME NOT NULL DEFAULT current_timestamp)");
            //connection.commit();

            statement.addBatch("CREATE Table P_Patients("
                    +"PatientID INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID))");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_StatusLog ("
                    +"StatusHistoryNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"StatusNum INT NOT NULL FOREIGN KEY REFERENCES P_Statuses(StatusNum),"
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"DateFrom Datetime NOT NULL DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_DeviceLog ("
                    +"StatusHistoryNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"StatusNum INT NOT NULL FOREIGN KEY REFERENCES P_Statuses(StatusNum),"
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"DateFrom Datetime NOT NULL DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Buddies ("
                    +"RelationshipNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"CommunityMemberID1 INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"CommunityMemberID2 INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"DateFrom Datetime NOT NULL DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();
            statement.executeBatch();

            createPatientsDBRelationTypes();

            statement.addBatch("CREATE TABLE P_Relations ("
                    +"RelationNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"PatientID INT NOT NULL FOREIGN KEY REFERENCES P_Patients(PatientID),"
                    +"RelationTypeNum INT NOT NULL FOREIGN KEY REFERENCES P_RelationTypes(relationTypeNum),"
                    +"DateFrom Datetime NOT NULL DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_EmergencyContact ("
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"ContactPhone VARCHAR(20) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_TypeLog ("
                    +"TypeHistoryNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"MemberTypeNum INT NOT NULL," // --Enum={patient, doctor, guardian}
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"DateFrom Datetime NOT NULL DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Doctors ("
                    +"DoctorID INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"FirstName VARCHAR(30) NOT NULL,"
                    +"LastName VARCHAR(30) NOT NULL,"
                    +"LicenseNumber INT NOT NULL,"
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID))");
            //connection.commit();

            statement.addBatch("CREATE TABLE MembersLoginDetails("
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"Password VARCHAR(30) NOT NULL,"
                    +"EmailAddress VARCHAR(30) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE RefreshDetailsTime ("
                    +"CommunityMemberID INT NOT NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID),"
                    +"FieldName VARCHAR(30) NOT NULL,"
                    +"LastUpdateTime Datetime NOT NULL,"
                    +"Urgent BIT DEFAULT 0)");
            //connection.commit();

            statement.addBatch("CREATE TABLE RegistrationFields ("
                    +"FieldName VARCHAR(30) NOT NULL,"
                    +"Type INT NOT NULL," // --Enum = {string, int, float}
                    +"UserType INT NOT NULL," // --Enum = {patient, doctor, guardian}
                    +"FieldsGroup INT NOT NULL," // --Enum = {personal, medical, professional, preferences}
                    +"NeedsVerification BIT NOT NUll DEFAULT 0,"
                    +"IsRequired BIT NOT NUll DEFAULT 0,"//0-yes, 1-no
                    +"MaxLength INT NOT NUll DEFAULT 0,"
                    +"GetPossibleValuesFrom VARCHAR(50),"
                    +"SerialNum INT NOT NUll," //begin with 1
                    +"InsertDataTo VARCHAR(200),"
                    +"RefreshTime INT)");
            //connection.commit();

            statement.addBatch("CREATE TABLE ServerUserNames ("
                    +"UserName VARCHAR(30) NOT NULL PRIMARY KEY,"
                    +"Password VARCHAR(30) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Supervision ("
                    +"TreatmentNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"DoctorID INT NOT NULL FOREIGN KEY REFERENCES P_Doctors(DoctorID),"
                    +"PatientID INT NOT NULL FOREIGN KEY REFERENCES P_Patients(PatientID),"
                    +"DateFrom Datetime DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Medications ("
                    +"MedicationNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"MedicationName VARCHAR(100) NOT NULL)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Prescriptions ("
                    +"PrescriptionNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"MedicationNum INT NOT NULL FOREIGN KEY REFERENCES P_Medications(MedicationNum),"
                    +"Dosage FLOAT NOT NULL,"
                    +"DoctorID INT NOT NULL FOREIGN KEY REFERENCES P_Doctors(DoctorID),"
                    +"PatientID INT NOT NULL FOREIGN KEY REFERENCES P_Patients(PatientID),"
                    +"DateFrom Datetime DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();

            statement.addBatch("CREATE TABLE P_Diagnosis ("
                    +"DiagnosisNum INT NOT NULL IDENTITY(1000,1) PRIMARY KEY,"
                    +"PatientID INT NOT NULL FOREIGN KEY REFERENCES P_Patients(PatientID),"
                    +"MedicalConditionID INT NOT NULL FOREIGN KEY REFERENCES m_MedicalConditions(MedicalConditionNum),"
                    +"DoctorID INT NOT NULL FOREIGN KEY REFERENCES P_Doctors(DoctorID),"
                    +"DateFrom Datetime DEFAULT current_timestamp,"
                    +"DateTo Datetime)");
            //connection.commit();
            statement.addBatch("CREATE TABLE RegIDs ("
                    +"RegID VARCHAR(100) NOT NULL PRIMARY KEY,"
                    +"CommunityMemberID INT NOT NULL)");

            statement.executeBatch();

            createPatientDBDiagnosis();
            createMedicalPersonnelDBOrganizationTypes();
            createMedicalPersonnelDBSpecifalization();
            createMedicalPesonnelDBOrganizations();
            createMedicalPersonnelDBCertification();
            createMedicalPesonnelDBMedicalPersonnel();
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
                        "(SpecializationID INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " SpecializationDescription VARCHAR(3000))");
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
                        "(CertificationInternalID INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " CertificationExternalID INTEGER not NULL, " +
                        " SpecializationID INTEGER not NULL FOREIGN KEY REFERENCES MP_Specialization(SpecializationID))");
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
                        "(internalID INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " externalID VARCHAR(25) not NULL, " +
                        " externalIDType INTEGER not NULL, " +  //enum:0 for id, 1 for passport num
                        " firstName VARCHAR(50) not NULL, " +
                        " lastName VARCHAR(50) not NULL, " +
                        " birthdayDate DATE not NULL, " +
                        " Gender INTEGER not NULL, " +  //enum:0 for male, 1 for female
                        " State VARCHAR(50) not NULL, " +
                        " City VARCHAR(50) not NULL, " +
                        " Street VARCHAR(50) not NULL, " +
                        " HouseNumber INTEGER not NULL, " +
                        " ZipCode INTEGER, " +
                        " HomePhoneNumber VARCHAR(50), " +
                        " MobilePhoneNumber VARCHAR(50) not NULL, " +
                        " Email VARCHAR(100) not NULL, " +
                        " CommunityMemberID INTEGER not NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID))");
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
                        "(TypeHistoryNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " OrganizationID INTEGER not NULL FOREIGN KEY REFERENCES MP_Organizations(OrganizationID), " +
                        " MedicalPersonnelID INTEGER not NULL FOREIGN KEY REFERENCES MP_MedicalPersonnel(internalID), " +
                        " PositionNum INTEGER not NULL FOREIGN KEY REFERENCES MP_Positions(PositionNum), " +
                        " DateFrom DATE not NULL, " +
                        " DateTo DATE not NULL)");
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
                        "(PositionNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " PositionDescription VARCHAR(50) not NULL)");
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
                        "(OrganizationID INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " OrganizationDescription VARCHAR(50) not NULL, " +
                        " OrganizationTypeNum INTEGER not NULL FOREIGN KEY REFERENCES MP_OrganizationTypes(OrganizationTypeNum), " +
                        " State VARCHAR(50) not NULL, " +
                        " City VARCHAR(50) not NULL, " +
                        " Street VARCHAR(50) not NULL, " +
                        " house INTEGER not NULL, " +
                        " PhoneNumber VARCHAR(50) not NULL, " +
                        " FaxNumber VARCHAR(50) not NULL, " +
                        " Email VARCHAR(100) not NULL, " +
                        " WebSite VARCHAR(100) not NULL, " +
                        " Remarks VARCHAR(100))");
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
                        "(MedicalID INTEGER NOT NULL FOREIGN KEY REFERENCES M_MedicalConditions(MedicalConditionNum), " +
                        " State  VARCHAR(50) not NULL, " +
                        " PatientAge VARCHAR(50) not NULL, " +
                        " EMS_ETA VARCHAR(50) not NULL, " +
                        " Use_ERC BIT not NULL)");
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
                        "(ActionTypeNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " ActionTypeName  VARCHAR(100) not NULL)");
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
                        "(ResponseNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " CommunityMemberID INTEGER not NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID), " +
                        " EventID INTEGER not NULL FOREIGN KEY REFERENCES O_EmergencyEvents(EventID), " +
                        " EmergencyEventNum INTEGER not NULL, " +
                        " ActionTypeNum INTEGER not NULL, " +
                        " ETAByFoot INTEGER not NULL, " +
                        " ActionStatusNum INTEGER not NULL, " +
                        " ETAByCar INTEGER not NULL, " +
                        " CreatedDate TIMESTAMP not NULL, " +
                        " X REAL not NULL, " +
                        " Y REAL not NULL, " +
                        " RequestSentDate DATETIME, " +
                        " ResponseDate DATETIME, " +
                        " ResponseType VARCHAR(50) not NULL, " +
                        " TransformationMean INTEGER not NULL, " +  //enum: 0 for foot, 1 for car
                        " ActivationDate DATETIME, " +
                        " ArrivalDate DATETIME, " +
                        " Result VARCHAR(100))");
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
                        "(RelationTypeNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " RelationTypeDescription  VARCHAR(30) not NULL)");
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
                        "(OrganizationTypeNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " OrganizationTypeDescription  VARCHAR(30) not NULL)");
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
                        "(StatusNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " StatusName  VARCHAR(30) not NULL)");
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
                        "(EventID INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " CreateBymemberID INTEGER not NULL FOREIGN KEY REFERENCES O_InvolvedCommunityMembers(InternalID), " +
                        " PatientId INTEGER not NULL, " +
                        " CreatedDate TIMESTAMP not NULL, " +
                        " FinishedDate DATETIME, " +
                        " EmsMemberID INTEGER not NULL FOREIGN KEY REFERENCES P_CommunityMembers(InternalID), " +
                        " StatusNum INTEGER not NULL FOREIGN KEY REFERENCES O_EventStatuses(StatusNum), " +
                        " X REAL not NULL, " +
                        " Y REAL not NULL, " +
                        " LocationRemark VARCHAR(100), " +
                        " PatientConditionRemarks VARCHAR(300), " +
                        " LastActionTime DATETIME, " +
                        " TimeToNextReminder INTEGER, " +
                        " Memo VARCHAR(300))");
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
                        "(EmergencyActionNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " EmergencyEventID INTEGER not NULL FOREIGN KEY REFERENCES O_EmergencyEvents(EventID), " +
                        " ActionTypeNum INTEGER not NULL FOREIGN KEY REFERENCES O_ActionTypes(ActionTypeNum), " +
                        " ActionStatusNum INTEGER not NULL FOREIGN KEY REFERENCES O_ActionStatus(StatusNum), " +
                        " CreatedDate TIMESTAMP not NULL, " +
                        " FinishedDate DATETIME not NULL)");
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
                        "(EmergenctMedicationUseNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " EmergencyEventNum INTEGER not NULL FOREIGN KEY REFERENCES O_EmergencyEvents(EventID), " +
                        " ProvidingMemberId INTEGER not NULL FOREIGN KEY REFERENCES O_InvolvedCommunityMembers(InternalID), " +
                        " ProvidingDispenserNum INTEGER not NULL FOREIGN KEY REFERENCES O_AutomaticDispensers(DispensersNum), " +
                        " ApprovedByID INTEGER not NULL, " +
                        " MedicationNum INTEGER not NULL FOREIGN KEY REFERENCES P_Medications(MedicationNum), " +
                        " ApprovalDate DATETIME, " +
                        " MedicationProvisionDate DATETIME, " +
                        " DigitalSignatureFile VARCHAR(100))");
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
                        "(StatusNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " StatusName VARCHAR(30) not NULL)");
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
                        "(DispensersNum INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " DispensersName VARCHAR(30) not NULL, " +
                        " DispensersLocation VARCHAR(100) not NULL)");
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
                        "(InternalID INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " ExternalID VARCHAR(30) not NULL, " +
                        " ExternalIDType INTEGER not NULL, " +  //enum:0 for id, 1 for passport num
                        " FirstName VARCHAR(30) not NULL, " +
                        " LastName VARCHAR(30) not NULL, " +
                        " BirthdayDate DATE not NULL, " +
                        " Gender INTEGER not NULL, " +  //enum:0 for male, 1 for female
                        " State VARCHAR(50) not NULL, " +
                        " City VARCHAR(50) not NULL, " +
                        " Street VARCHAR(50) not NULL, " +
                        " HouseNumber INTEGER not NULL, " +
                        " ZipCode INTEGER, " +
                        " HomePhoneNumber VARCHAR(50), " +
                        " MobilePhoneNumber VARCHAR(50) not NULL, " +
                        " Email VARCHAR(100) not NULL)");
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
                        "(Id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " Json INTEGER not NULL, " +
                        " Cmid INTEGER not NULL)");
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
                        "(Name VARCHAR(30) not NULL PRIMARY KEY, " +
                        " Frequency REAL not NULL, " +
                        " MedicalCondition VARCHAR(50), " +
                        " State VARCHAR(50) not NULL, " +
                        " Area VARCHAR(50), " +
                        " PatientAge INTEGER)");
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
                        "(Id INTEGER not NULL IDENTITY(1000,1) PRIMARY KEY, " +
                        " Description VARCHAR(100) not NULL)");
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
                        "(State VARCHAR(50) not NULL PRIMARY KEY, " +
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
                        "(State VARCHAR(50) not NULL PRIMARY KEY, " +
                        " Email VARCHAR(100) not NULL)");
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
                        "(State VARCHAR(50) not NULL PRIMARY KEY, " +
                        " DefaultCaller INTEGER not NULL)"); // enum:0 for app, 1 for server
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

