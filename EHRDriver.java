import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class EHRDriver {
    private static final String DB_URL = "jdbc:mysql://localhost:3308/";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void main(String[] args) {
        try {
            // Connect to MySQL server
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create databases b1, b2, ..., b10
            for (int i = 1; i <= 10; i++) {
                String dbName = "ehr_b" + i;
                createDatabase(conn, dbName);
                createTablesAndSampleData(dbName);
            }

            conn.close();
            System.out.println("All databases created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDatabase(Connection conn, String dbName) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE DATABASE " + dbName);
        System.out.println("Database '" + dbName + "' created successfully.");
        stmt.close();
    }

    private static void createTablesAndSampleData(String dbName) {
        String dbUrl = DB_URL + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // Create Users table
            stmt.executeUpdate("""
                CREATE TABLE Users (
                    UserID INT PRIMARY KEY AUTO_INCREMENT,
                    UserType ENUM('Patient', 'Healthcare Provider', 'Administrative Staff') NOT NULL,
                    FullName VARCHAR(100) NOT NULL,
                    Email VARCHAR(100),
                    Phone VARCHAR(20),
                    Address VARCHAR(255)
                )
                """);

            // Insert sample data into Users table
            stmt.executeUpdate("""
                INSERT INTO Users (UserType, FullName, Email, Phone, Address)
                VALUES ('Patient', 'John Doe', 'john@example.com', '123-456-7890', '123 Main St')
                """);

            // Create Appointments table
            stmt.executeUpdate("""
                CREATE TABLE Appointments (
                    AppointmentID INT PRIMARY KEY AUTO_INCREMENT,
                    UserID INT,
                    AppointmentDate DATETIME,
                    Description TEXT,
                    FOREIGN KEY (UserID) REFERENCES Users(UserID)
                )
                """);

            // Insert sample data into Appointments table
            stmt.executeUpdate("""
                INSERT INTO Appointments (UserID, AppointmentDate, Description)
                VALUES (1, '2024-04-23 10:00:00', 'General checkup')
                """);

            // Create MedicalHistory table
            stmt.executeUpdate("""
                CREATE TABLE MedicalHistory (
                    MedicalHistoryID INT PRIMARY KEY AUTO_INCREMENT,
                    UserID INT,
                    Cond VARCHAR(100),
                    Treatment VARCHAR(100),
                    Surgery VARCHAR(100),
                    FOREIGN KEY (UserID) REFERENCES Users(UserID)
                )
                """);

            // Insert sample data into MedicalHistory table
            stmt.executeUpdate("""
                INSERT INTO MedicalHistory (UserID, Cond, Treatment, Surgery)
                VALUES (1, 'Hypertension', 'Medication', 'None')
                """);

            // Create Prescription table
            stmt.executeUpdate("""
                CREATE TABLE Prescription (
                    PrescriptionID INT PRIMARY KEY AUTO_INCREMENT,
                    UserID INT,
                    MedicationName VARCHAR(100),
                    Dosage VARCHAR(50),
                    Instructions TEXT,
                    FOREIGN KEY (UserID) REFERENCES Users(UserID)
                )
                """);

            // Insert sample data into Prescription table
            stmt.executeUpdate("""
                INSERT INTO Prescription (UserID, MedicationName, Dosage, Instructions)
                VALUES (1, 'Aspirin', '10mg', 'Take once daily with food')
                """);

            // Create TreatmentRecords table
            stmt.executeUpdate("""
                CREATE TABLE TreatmentRecords (
                    TreatmentRecordID INT PRIMARY KEY AUTO_INCREMENT,
                    UserID INT,
                    ProcedureName VARCHAR(100),
                    DatePerformed DATETIME,
                    Notes TEXT,
                    FOREIGN KEY (UserID) REFERENCES Users(UserID)
                )
                """);

            // Insert sample data into TreatmentRecords table
            stmt.executeUpdate("""
                INSERT INTO TreatmentRecords (UserID, ProcedureName, DatePerformed, Notes)
                VALUES (1, 'Dental Cleaning', '2024-04-20 09:00:00', 'Routine checkup')
                """);

            // Create Billings table
            stmt.executeUpdate("""
                CREATE TABLE Billings (
                    BillingID INT PRIMARY KEY AUTO_INCREMENT,
                    UserID INT,
                    InvoiceNumber VARCHAR(50),
                    Amount DECIMAL(10, 2),
                    PaymentStatus ENUM('Paid', 'Pending', 'Overdue'),
                    FOREIGN KEY (UserID) REFERENCES Users(UserID)
                )
                """);

            // Insert sample data into Billings table
            stmt.executeUpdate("""
                INSERT INTO Billings (UserID, InvoiceNumber, Amount, PaymentStatus)
                VALUES (1, 'INV-001', 100.00, 'Paid')
                """);

            // Create Payments table
            stmt.executeUpdate("""
                CREATE TABLE Payments (
                    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
                    BillingID INT,
                    PaymentAmount DECIMAL(10, 2),
                    PaymentDate DATETIME,
                    PaymentMethod VARCHAR(50),
                    FOREIGN KEY (BillingID) REFERENCES Billings(BillingID)
                )
                """);

            // Insert sample data into Payments table
            stmt.executeUpdate("""
                INSERT INTO Payments (BillingID, PaymentAmount, PaymentDate, PaymentMethod)
                VALUES (1, 100.00, '2024-04-20 10:00:00', 'Credit Card')
                """);

            System.out.println("Tables created and sample data inserted successfully in database '" + dbName + "'.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
