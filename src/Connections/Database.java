package Connections;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private Connection conn = null;
    private Connection updateConnection = null;
    private static String url;
    private static Logger logger;
    private final static String CONNECTION_CLOSE_ERROR_MESSAGE = "Error closing connection";
    private final static String CONNECTION_OPEN_ERROR_MESSAGE = "Error opening connection";
    private void logErrorMessage(String errorMessage) {
        Database.logger.log(Level.WARNING, String.format("Error occurred while executing query. MSG : %s", errorMessage));
    }
    private void logErrorMessage(String errorDeclaration, String errorMessage) {
        Database.logger.log(Level.WARNING, String.format("%s. MSG : %s",errorDeclaration, errorMessage));
    }
    private void logInfoMessage(String logMessage) {
        Database.logger.log(Level.INFO, logMessage);
    }

    public Database(String dbPath, Logger logger){
        Database.logger = logger;
        Database.url = String.format("jdbc:sqlite:%s", dbPath);
        try {
            conn = DriverManager.getConnection(Database.url);
        } catch (SQLException e) {
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, e.getMessage());
        }
        finally {
            closeConnection();
        }

        try {
            this.updateConnection = DriverManager.getConnection(Database.url);
        }catch (SQLException e){
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, e.getMessage());
        }finally {
            closeUpdateConnection();
        }
    }

    private void openUpdateConnection(){
        try {
            this.updateConnection = DriverManager.getConnection(Database.url);
        }catch (SQLException e){
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, e.getMessage());
        }
    }

    private void closeUpdateConnection(){
        try {
            this.updateConnection.close();
        }catch (SQLException e){
            logErrorMessage(Database.CONNECTION_CLOSE_ERROR_MESSAGE, e.getMessage());
        }
    }
    private void openConnection(){
        try {
            conn = DriverManager.getConnection(Database.url);
        } catch (SQLException e) {
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, e.getMessage());
        }
    }
    public void closeConnection(){
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException ex) {
            logErrorMessage(Database.CONNECTION_CLOSE_ERROR_MESSAGE, ex.getMessage());
        }
    }

    public boolean isUnique(String data, String table, String field){
        openConnection();
        boolean unique = false;
        String query = "SELECT * FROM " + table + " WHERE " + field + "='" + data + "'";
        try {
            Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(!resultSet.next()) {
                unique = true;
            }
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
        }finally {
            closeConnection();
        }
        return unique;
    }

    public int addMember(String[] data){
        //first_name, surname, phone_number, res_address, date_added, national_id
        openUpdateConnection();
        int temp = 0;
        Date now = Date.valueOf(LocalDate.now());
        String sql = "INSERT INTO Members(first_name, surname, phone_number, Residential_address, date_added, national_id, membership_number) VALUES ('" +
                data[0] + "', '" +
                data[1] + "', '" +
                data[2] + "', '" +
                data[3] + "', '" +
                now.toString() + "', '" +
                data[4] + "', '" +
                data[5] + "')";
        try {
            Statement statement  = this.updateConnection.createStatement();
            temp = statement.executeUpdate(sql);
            logInfoMessage("Member added successfully");
        } catch (SQLException e) {
            logErrorMessage(e.getMessage());
            temp = -1;
        }
        finally {
            closeUpdateConnection();
        }
        return temp;
    }
    public int addVideo(String videoName, int videCategory){
        Date now = Date.valueOf(LocalDate.now());
        this.openUpdateConnection();
        int result = 0;
        String query = "INSERT INTO videos (video_name, video_category, video_status, date_added) VALUES ( '"
                + videoName + "', " + videCategory + ", 1, '" + now.toString() + "')";
        try{
            Statement statement = this.updateConnection.createStatement();
            result = statement.executeUpdate(query);
            logInfoMessage(String.format("Video with id %s has been added", now));
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            result = -1;
        }finally {
            closeUpdateConnection();
        }
        return result;
    }

    public int getUserId(String mem_number){
        int id = -2;
        openConnection();
        String query = "SELECT * FROM members WHERE membership_number='" + mem_number + "'";
        try{
            Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                System.out.println(resultSet.getInt("user_id"));
                id = resultSet.getInt("user_id");
            }
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            id = -1;
        }finally {
            closeConnection();
        }
        return id;
    }

    public String[] getVideoInformation(String query){
        String[] info = new String[6];
        String sql = "SELECT * FROM videos WHERE video_id=" + query + " OR video_name='" + query + "'";
        openConnection();
        try{
            Statement st = this.conn.createStatement();
            ResultSet videoInfo = st.executeQuery(sql);
            while (videoInfo.next()){
                info[0] = videoInfo.getString("video_id");
                info[1] = String.valueOf(videoInfo.getInt("video_category"));
                info[2] = String.valueOf(videoInfo.getInt("video_status"));
                info[3] = videoInfo.getString("video_name");
                info[4] = String.valueOf(videoInfo.getInt("borrower"));
                info[5] = videoInfo.getString("date_last_borrowed");
            }
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            info[0] = "-1";
        }finally {
            closeConnection();
        }
        return info;
    }

    public int borrowVideo(int video_id, int user_id){
        int res = 0;
        openUpdateConnection();
        Date now = Date.valueOf(LocalDate.now());
        String query = "UPDATE videos " +
                "SET borrower=" + user_id + ", date_last_borrowed='" + now.toString() + "', video_status=4 WHERE video_id=" + video_id;
        try{
            Statement statement = this.updateConnection.createStatement();
            res = statement.executeUpdate(query);
            logInfoMessage(String.format("Book with ID %d queried and found.", video_id));
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            res = -1;
        }finally {
            closeUpdateConnection();
        }
        return res;
    }

    public int returnVideo(int video_id, String videoReport, int videoStatus){
        int res = 0;
        openUpdateConnection();
        String query = "UPDATE videos " +
                "SET borrower=null, video_status="+ videoStatus + ", latest_report='" + videoReport + "' WHERE video_id=" + video_id;
        try{
            Statement statement = this.updateConnection.createStatement();
            res = statement.executeUpdate(query);
            logInfoMessage(String.format("Book with ID %d returned.", video_id));
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            res = -1;
        }finally {
            closeUpdateConnection();
        }
        return res;
    }

    public int checkPendingVideos(int user_id){
        openConnection();
        int video_count = 0;
        String queryString = "SELECT * FROM videos WHERE video_status=4 AND borrower=" + user_id;
        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st.executeQuery(queryString);
            while(rs.next()){
                video_count++;
            }
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
        }finally {
            closeConnection();
        }
        return video_count;
    }

    public int getTotalAmount(){
        int total = 0;
        openConnection();
        String queryString = "SELECT * FROM finance";
        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st.executeQuery(queryString);
            while(rs.next()){
                total += rs.getInt("amount");
            }
        }catch (SQLException e){
            total = -1;
            logErrorMessage(e.getMessage());
        }finally {
            closeConnection();
        }
        return total;
    }

    public int carryTransaction(int amount, int type){
        int res = 0;
        openUpdateConnection();
        Date now = Date.valueOf(LocalDate.now());
        String query = "INSERT INTO finance (amount, date_added, transaction_type) VALUES (" + amount +", '" + now.toString() + "', " + type + ")";
        try{
            Statement statement = this.updateConnection.createStatement();
            res = statement.executeUpdate(query);
            logInfoMessage(String.format("Transaction ID %s carried out successfully", now));
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            res = -1;
        }finally {
            closeUpdateConnection();
        }
        return res;
    }

    public int getBill(String mem_num){
        openConnection();
        int bill_amount = 0;
        String queryString = "SELECT * FROM members WHERE membership_number='" + mem_num + "'";
        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st.executeQuery(queryString);
            while(rs.next()){
                bill_amount = rs.getInt("bill_balance");
            }
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            bill_amount = -1;
        }finally {
            closeConnection();
        }
        return bill_amount;
    }

    public int payBill(String mem_num){
        int res = 0;
        openUpdateConnection();
        String query = "UPDATE members SET bill_balance=0 WHERE membership_number='" + mem_num + "'";
        try{
            Statement statement = this.updateConnection.createStatement();
            res = statement.executeUpdate(query);
            logInfoMessage("Bill paid");
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            res = -1;
        }finally {
            closeUpdateConnection();
        }
        return res;
    }
    public void addToBill(int amount, String mem_num){
        int res = 0;
        int pending_bill = getBill(mem_num);
        openUpdateConnection();
        String query = "UPDATE members SET bill_balance=" + (pending_bill + amount) + " WHERE membership_number='" + mem_num + "'";
        try{
            Statement statement = this.updateConnection.createStatement();
            res = statement.executeUpdate(query);
        }catch (SQLException e){
            logErrorMessage(e.getMessage());
            res = -1;
        }finally {
            closeUpdateConnection();
        }
    }
}
