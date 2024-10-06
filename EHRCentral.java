import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.sql.*;
import java.util.Base64;

public class EHRCentral {

    private static final String HOST_DB_URL_PREFIX = "jdbc:mysql://localhost:3308/ehr_b";
    private static final String HOST_DB_USER = "root";
    private static final String HOST_DB_PASS = "";
    private static final String CENTRAL_DB_URL = "jdbc:mysql://localhost:3306/ehr_vault_central";
    private static final String CENTRAL_DB_USER = "newuser";
    private static final String CENTRAL_DB_PASS = "vijai1234@";
    private static final String SECRET_KEY = "nCBtoA61MNgYnT0JYOHCOJoPrCnGui5c";

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            String hostDbUrl = HOST_DB_URL_PREFIX + i;
            readAndTransferData(hostDbUrl);
        }
    }

    private static void readAndTransferData(String hostDbUrl) {
        try (Connection hostConn = DriverManager.getConnection(hostDbUrl, HOST_DB_USER, HOST_DB_PASS);
             Connection centralConn = DriverManager.getConnection(CENTRAL_DB_URL, CENTRAL_DB_USER, CENTRAL_DB_PASS)) {

            transferUsers(hostConn, centralConn);
            transferAppointments(hostConn, centralConn);
            transferMedicalHistory(hostConn, centralConn);
            transferPrescription(hostConn, centralConn);
            transferTreatmentRecords(hostConn, centralConn);
            transferBillings(hostConn, centralConn);
            transferPayments(hostConn, centralConn);

            System.out.println("Data transferred successfully from " + hostDbUrl + " to centralized repository.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void transferUsers(Connection hostConn, Connection centralConn) throws SQLException {
        String query = "SELECT * FROM Users";
        try (Statement stmt = hostConn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PreparedStatement centralStmt = centralConn.prepareStatement("INSERT INTO Users (UserID, UserType, FullName, Address) VALUES (?, ?, ?, ?)")) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String userType = rs.getString("UserType");
                String fullName = rs.getString("FullName");
                String address = rs.getString("Address");

                // Encrypt FullName and Address using AES encryption
                String encryptedFullName = encrypt(fullName);
                String encryptedAddress = encrypt(address);

                centralStmt.setInt(1, userID);
                centralStmt.setString(2, userType);
                centralStmt.setString(3, encryptedFullName);
                centralStmt.setString(4, encryptedAddress);

                centralStmt.executeUpdate();
            }
        }
    }

    private static void transferAppointments(Connection hostConn, Connection centralConn) throws SQLException {
        String query = "SELECT * FROM Appointments";
        try (Statement stmt = hostConn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PreparedStatement centralStmt = centralConn.prepareStatement(
                     "INSERT INTO Appointments (UserID, Description) VALUES (?, ?)")) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String encryptedUserID = encrypt(String.valueOf(userID));
                String description = rs.getString("Description");

                centralStmt.setString(1, encryptedUserID);
                centralStmt.setString(2, description);

                centralStmt.executeUpdate();
            }
        }
    }

    private static void transferMedicalHistory(Connection hostConn, Connection centralConn) throws SQLException {
        String query = "SELECT * FROM MedicalHistory";
        try (Statement stmt = hostConn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PreparedStatement centralStmt = centralConn.prepareStatement(
                     "INSERT INTO MedicalHistory (UserID, Cond, Treatment, Surgery) VALUES (?, ?, ?, ?)")) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String encryptedUserID = encrypt(String.valueOf(userID));
                String condition = rs.getString("Cond");
                String treatment = rs.getString("Treatment");
                String surgery = rs.getString("Surgery");

                centralStmt.setString(1, encryptedUserID);
                centralStmt.setString(2, condition);
                centralStmt.setString(3, treatment);
                centralStmt.setString(4, surgery);

                centralStmt.executeUpdate();
            }
        }
    }

    private static void transferPrescription(Connection hostConn, Connection centralConn) throws SQLException {
        String query = "SELECT * FROM Prescription";
        try (Statement stmt = hostConn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PreparedStatement centralStmt = centralConn.prepareStatement(
                     "INSERT INTO Prescription (UserID, MedicationName, Dosage, Instructions) VALUES (?, ?, ?, ?)")) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String encryptedUserID = encrypt(String.valueOf(userID));
                String medicationName = rs.getString("MedicationName");
                String dosage = rs.getString("Dosage");
                String instructions = rs.getString("Instructions");

                centralStmt.setString(1, encryptedUserID);
                centralStmt.setString(2, medicationName);
                centralStmt.setString(3, dosage);
                centralStmt.setString(4, instructions);

                centralStmt.executeUpdate();
            }
        }
    }

    private static void transferTreatmentRecords(Connection hostConn, Connection centralConn) throws SQLException {
        String query = "SELECT * FROM TreatmentRecords";
        try (Statement stmt = hostConn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PreparedStatement centralStmt = centralConn.prepareStatement(
                     "INSERT INTO TreatmentRecords (UserID, ProcedureName, DatePerformed, Notes) VALUES (?, ?, ?, ?)")) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String encryptedUserID = encrypt(String.valueOf(userID));
                String procedureName = rs.getString("ProcedureName");
                Timestamp datePerformed = rs.getTimestamp("DatePerformed");
                String notes = rs.getString("Notes");

                centralStmt.setString(1, encryptedUserID);
                centralStmt.setString(2, procedureName);
                centralStmt.setTimestamp(3, datePerformed);
                centralStmt.setString(4, notes);

                centralStmt.executeUpdate();
            }
        }
    }

    private static void transferBillings(Connection hostConn, Connection centralConn) throws SQLException {
        String query = "SELECT * FROM Billings";
        try (Statement stmt = hostConn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PreparedStatement centralStmt = centralConn.prepareStatement(
                     "INSERT INTO Billings (UserID, Amount, PaymentStatus) VALUES (?, ?, ?)")) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String encryptedUserID = encrypt(String.valueOf(userID));
                double amount = rs.getDouble("Amount");
                String paymentStatus = rs.getString("PaymentStatus");

                centralStmt.setString(1, encryptedUserID);
                centralStmt.setDouble(2, amount);
                centralStmt.setString(3, paymentStatus);

                centralStmt.executeUpdate();
            }
        }
    }

    private static void transferPayments(Connection hostConn, Connection centralConn) throws SQLException {
        String query = "SELECT * FROM Payments";
        try (Statement stmt = hostConn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             PreparedStatement centralStmt = centralConn.prepareStatement(
                     "INSERT INTO Payments (PaymentAmount, PaymentDate, PaymentMethod) VALUES (?, ?, ?)")) {

            while (rs.next()) {
                double paymentAmount = rs.getDouble("PaymentAmount");
                Timestamp paymentDate = rs.getTimestamp("PaymentDate");
                String paymentMethod = rs.getString("PaymentMethod");

                centralStmt.setDouble(1, paymentAmount);
                centralStmt.setTimestamp(2, paymentDate);
                centralStmt.setString(3, paymentMethod);

                centralStmt.executeUpdate();
            }
        }
    }
    private static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Key secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            Key secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}