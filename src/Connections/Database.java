package Connections;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import Tools.Types.*;

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
        } catch (SQLException sqlException) {
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }
        finally {
            closeConnection();
        }

        try {
            this.updateConnection = DriverManager.getConnection(Database.url);
        }catch (SQLException sqlException){
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }finally {
            closeUpdateConnection();
        }
    }

    private void openUpdateConnection(){
        try {
            this.updateConnection = DriverManager.getConnection(Database.url);
        }catch (SQLException sqlException){
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }
    }

    private void closeUpdateConnection(){
        try {
            this.updateConnection.close();
        }catch (SQLException sqlException){
            logErrorMessage(Database.CONNECTION_CLOSE_ERROR_MESSAGE, sqlException.getMessage());
        }
    }
    private void openConnection(){
        try {
            conn = DriverManager.getConnection(Database.url);
        } catch (SQLException sqlException) {
            logErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }
    }
    public void closeConnection(){
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException sqlException) {
            logErrorMessage(Database.CONNECTION_CLOSE_ERROR_MESSAGE, sqlException.getMessage());
        }
    }

    public boolean entityExists(String data, String table, String field){
        openConnection();
        boolean exists = false;
        String query = String.format(Queries.isUniqueCheckQuery, table, field, data);
        try {
            Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(!resultSet.next()) {
                exists = true;
            }
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
        }finally {
            closeConnection();
        }
        return exists;
    }

    public QueryProgress addMember(String[] data){
        openUpdateConnection();
        QueryProgress queryProgress;
        String now = Date.valueOf(LocalDate.now()).toString();
        String sqlQuery = String.format(Queries.addMemberQuery, data[0], data[1], data[2], data[3], now, data[4], data[5]);
        try {
            Statement statement  = this.updateConnection.createStatement();
            statement.executeUpdate(sqlQuery);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage("Member added successfully");
        } catch (SQLException sqlException) {
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }
        finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }

    public QueryProgress addVideo(String videoName, int videCategory){
        String currentDate = Date.valueOf(LocalDate.now()).toString();
        this.openUpdateConnection();
        QueryProgress queryProgress;
        String query = String.format(Queries.addVideoQuery, videoName, videCategory, currentDate);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage(String.format("Video with id %s has been added", currentDate));
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        } finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }

    public int getUserId(String membershipNumber){
        int userReturnID = 0;
        QueryProgress queryProgress;
        openConnection();
        String sqlQuery = String.format(Queries.getUserIDQuery, membershipNumber);
        try{
            Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while(resultSet.next()){
                userReturnID = resultSet.getInt("user_id");
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeConnection();
        }
        return (queryProgress == QueryProgress.ERROR) ? -1 : userReturnID;
    }

    public String[] getVideoInformation(String videoID){
        String[] info = new String[6];
        String sqlQuery = String.format(Queries.getVideoInformationQuery, videoID, videoID);
        openConnection();
        try{
            Statement statement = this.conn.createStatement();
            ResultSet videoInfo = statement.executeQuery(sqlQuery);
            while (videoInfo.next()){
                info[0] = videoInfo.getString("video_id");
                info[1] = String.valueOf(videoInfo.getInt("video_category"));
                info[2] = String.valueOf(videoInfo.getInt("video_status"));
                info[3] = videoInfo.getString("video_name");
                info[4] = String.valueOf(videoInfo.getInt("borrower"));
                info[5] = videoInfo.getString("date_last_borrowed");
            }
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            info[0] = "-1";
        }finally {
            closeConnection();
        }
        return info;
    }

    public QueryProgress borrowVideo(int video_id, int user_id){
        openUpdateConnection();
        QueryProgress queryProgress;
        String currentDate = Date.valueOf(LocalDate.now()).toString();
        String query = String.format(Queries.borrowVideoQuery, user_id, currentDate, video_id);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage(String.format("Book with ID %d queried and found.", video_id));
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }

    public QueryProgress returnVideo(int video_id, String videoReport, int videoStatus){
        openUpdateConnection();
        QueryProgress queryProgress;
        String query = String.format("UPDATE videos SET video_status=%d, latest_report='%s' WHERE video_id=%d", videoStatus, videoReport, video_id);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage(String.format("Book with ID %d returned.", video_id));
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }

    public int checkPendingVideos(int userId){
        openConnection();
        QueryProgress queryProgress;
        int videoCount = 0;
        String queryString = String.format(Queries.checkPendingVideosQuery, userId);
        try {
            Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            while(resultSet.next()){
                videoCount++;
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            queryProgress = QueryProgress.ERROR;
            logErrorMessage(sqlException.getMessage());
        }finally {
            closeConnection();
        }
        return (queryProgress == QueryProgress.ERROR)? -1 : videoCount;
    }

    public int getTotalAmount(){
        openConnection();
        int total = 0;
        QueryProgress queryProgress;
        String queryString = Queries.totalAmountQuery;
        try {
            Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            while(resultSet.next()){
                total += resultSet.getInt("amount");
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            queryProgress = QueryProgress.ERROR;
            logErrorMessage(sqlException.getMessage());
        }finally {
            closeConnection();
        }
        return (queryProgress == QueryProgress.ERROR) ? -1 : total;
    }

    public QueryProgress carryTransaction(int amount, int type){
        QueryProgress queryProgress;
        openUpdateConnection();
        String now = Date.valueOf(LocalDate.now()).toString();
        String query = String.format(Queries.carryoutTransactionQuery, amount, now, type);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            logInfoMessage(String.format("Transaction ID %s carried out successfully", now));
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }

    public int getBill(String membershipNumber){
        openConnection();
        int billAmount = 0;
        QueryProgress queryProgress;
        String queryString = String.format(Queries.billQuery, membershipNumber);
        try {
            Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            while(resultSet.next()){
                billAmount = resultSet.getInt("bill_balance");
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeConnection();
        }
        return (queryProgress == QueryProgress.ERROR)? -1 : billAmount;
    }

    public QueryProgress payBill(String membershipNumber){
        openUpdateConnection();
        QueryProgress queryProgress;
        String query = String.format(Queries.payBillQuery, membershipNumber);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            logInfoMessage("Bill paid");
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }
    public QueryProgress addToBill(int amount, String membershipNumber){
        QueryProgress queryProgress;
        int pendingBill = getBill(membershipNumber);
        int currentBill = pendingBill + amount;
        openUpdateConnection();
        String query = String.format(Queries.addToBillMutation, currentBill, membershipNumber);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage(sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }
}
