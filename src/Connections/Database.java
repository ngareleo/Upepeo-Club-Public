package Connections;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import Entities.Member;
import Entities.Video;
import Tools.Types.*;
import Tools.Utils;

import static Tools.Utils.*;

public class Database {
    private Connection queryConnection = null;
    private Connection updateConnection = null;
    private static String url;
    private static Logger logger;
    private final static String CONNECTION_CLOSE_ERROR_MESSAGE = "Error closing connection";
    private final static String CONNECTION_OPEN_ERROR_MESSAGE = "Error opening connection";
    private void logErrorMessage(String methodName, String errorMessage) {
        Database.logger.log(Level.WARNING, String.format("Error occurred while executing %s query. MSG : %s", methodName, errorMessage));
    }
    private void logCustomErrorMessage(String errorDeclaration, String errorMessage) {
        Database.logger.log(Level.WARNING, String.format("%s. MSG : %s", errorDeclaration, errorMessage));
    }
    private void logInfoMessage(String logMessage) {
        Database.logger.log(Level.INFO, logMessage);
    }


    public Database(String dbPath, Logger logger){
        Database.logger = logger;
        Database.url = String.format("jdbc:sqlite:%s", dbPath);
        try {
            queryConnection = DriverManager.getConnection(Database.url);
        } catch (SQLException sqlException) {
            logCustomErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }
        finally {
            closeConnection();
        }

        try {
            this.updateConnection = DriverManager.getConnection(Database.url);
        }catch (SQLException sqlException){
            logCustomErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }finally {
            closeUpdateConnection();
        }
    }

    private void openUpdateConnection(){
        try {
            this.updateConnection = DriverManager.getConnection(Database.url);
        }catch (SQLException sqlException){
            logCustomErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }
    }

    private void closeUpdateConnection(){
        try {
            this.updateConnection.close();
        }catch (SQLException sqlException){
            logCustomErrorMessage(Database.CONNECTION_CLOSE_ERROR_MESSAGE, sqlException.getMessage());
        }
    }
    private void openConnection(){
        try {
            queryConnection = DriverManager.getConnection(Database.url);
        } catch (SQLException sqlException) {
            logCustomErrorMessage(Database.CONNECTION_OPEN_ERROR_MESSAGE, sqlException.getMessage());
        }
    }
    public void closeConnection(){
        try {
            if (this.queryConnection != null) {
                this.queryConnection.close();
            }
        } catch (SQLException sqlException) {
            logCustomErrorMessage(Database.CONNECTION_CLOSE_ERROR_MESSAGE, sqlException.getMessage());
        }
    }

    public boolean entityExists(String data, String table, String field){
        openConnection();
        boolean exists = false;
        String query = String.format(Queries.isUniqueCheckQuery, table, field, data);
        try {
            Statement statement = this.queryConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(!resultSet.next()) {
                exists = true;
            }
        }catch (SQLException sqlException){
            logErrorMessage("entityExists", sqlException.getMessage());
        }finally {
            closeConnection();
        }
        return exists;
    }

    public QueryProgress addMember(Member member){
        openUpdateConnection();
        QueryProgress queryProgress;
        String currentDate = Date.valueOf(LocalDate.now()).toString();
        String sqlQuery = String.format(
                Queries.addMemberQuery,
                member.getFirstName(),
                member.getSurname(),
                member.getPhoneNumber(),
                member.getResidentialAddress(),
                currentDate,
                member.getNationalID(),
                member.getMembershipID()
        );

        try {
            Statement statement  = this.updateConnection.createStatement();
            statement.executeUpdate(sqlQuery);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage("Member added successfully");
        } catch (SQLException sqlException) {
            logErrorMessage("addMember", sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }
        finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }

    public QueryProgress addVideo(Video video){
        String currentDate = Date.valueOf(LocalDate.now()).toString();
        this.openUpdateConnection();
        QueryProgress queryProgress;
        String query = String.format(Queries.addVideoQuery, video.getVideoName(), video.getVideoCategory(), currentDate);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage(String.format("Video with id %s has been added", currentDate));
        }catch (SQLException sqlException){
            logErrorMessage("addVideo", sqlException.getMessage());
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
            Statement statement = this.queryConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while(resultSet.next()){
                userReturnID = resultSet.getInt("user_id");
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage("getUserId", sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeConnection();
        }
        return (queryProgress == QueryProgress.ERROR) ? -1 : userReturnID;
    }

    public Video getVideoInformation(String videoID){
        openConnection();
        QueryProgress queryProgress = QueryProgress.NOT_STARTED;
        Video video = null;
        Member owner = null;
        String sqlQuery = String.format(Queries.getVideoInformationQuery, videoID, videoID);
        try{
            Statement statement = this.queryConnection.createStatement();
            ResultSet videoInfo = statement.executeQuery(sqlQuery);
            while (videoInfo.next()){
                String videoId = videoInfo.getString("video_id");
                VideoCategory videoCategory = getCategoryName().get(videoInfo.getInt("video_category"));
                VideoStatus videoStatus = getStatusInfo().get(videoInfo.getInt("video_status"));
                String videoName = videoInfo.getString("video_name");
                String borrowerID = String.valueOf(videoInfo.getInt("borrower"));
                String dateLastBorrowed = videoInfo.getString("date_last_borrowed");
                owner = this.getMemberInformation(borrowerID);
                video = new Video(videoId, videoName, videoCategory, videoStatus, owner, dateLastBorrowed);
                queryProgress = QueryProgress.COMPLETE;
            }
        }catch (SQLException sqlException){
            logErrorMessage("getVideoInformation", sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeConnection();
        }
        return !(queryProgress == QueryProgress.COMPLETE) ? null : video;
    }

    public Member getMemberInformation(String memberShipID){
        openConnection();
        QueryProgress queryProgress = QueryProgress.NOT_STARTED;
        Member member = null;
        String sqlQuery = String.format(Queries.getMemberInformationQuery, memberShipID);
        try{
            Statement statement = this.queryConnection.createStatement();
            ResultSet memberInfo = statement.executeQuery(sqlQuery);
            while (memberInfo.next()){
                String membershipId = memberInfo.getString("membership_number");
                String firstName = memberInfo.getString("first_name");
                String surname = memberInfo.getString("surname");
                String phoneNumber = memberInfo.getString("phone_number");
                String residentialAddress = memberInfo.getString("residential_address");
                double billBalance = memberInfo.getDouble("bill_balance");
                String dateAdded = memberInfo.getString("date_added");
                String nationalID = memberInfo.getString("national_id");
                member = new Member(firstName, surname, phoneNumber, nationalID, residentialAddress, membershipId, dateAdded, billBalance);
                queryProgress = QueryProgress.COMPLETE;
            }
        }catch (SQLException sqlException){
            logErrorMessage("getMemberInformation",sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeConnection();
        }
        return !(queryProgress == QueryProgress.COMPLETE) ? null : member;
    }

    public QueryProgress borrowVideo(String videoId, String membershipID){
        openUpdateConnection();
        QueryProgress queryProgress;
        String currentDate = Date.valueOf(LocalDate.now()).toString();
        String query = String.format(Queries.borrowVideoQuery, membershipID, currentDate, videoId);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage(String.format("Book with ID %d queried and found.", videoId));
        }catch (SQLException sqlException){
            logErrorMessage("borrowVideo", sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }

    public QueryProgress returnVideo(Video video){
        openUpdateConnection();
        QueryProgress queryProgress;
        int videoStatus = Utils.getStatusNumber().get(video.getVideoStatus());
        String query = String.format("UPDATE videos SET video_status=%d, latest_report='%s' WHERE video_id=%s", videoStatus, video.getLatestVideoReport(), video.getVideoID());
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            queryProgress = QueryProgress.COMPLETE;
            logInfoMessage(String.format("Book with ID %s returned.", video.getVideoID()));
        }catch (SQLException sqlException){
            logErrorMessage("returnVideo", sqlException.getMessage());
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
            Statement statement = this.queryConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            while(resultSet.next()){
                videoCount++;
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            queryProgress = QueryProgress.ERROR;
            logErrorMessage("checkPendingVideos", sqlException.getMessage());
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
            Statement statement = this.queryConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            while(resultSet.next()){
                total += resultSet.getInt("amount");
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            queryProgress = QueryProgress.ERROR;
            logErrorMessage("getTotalAmount", sqlException.getMessage());
        }finally {
            closeConnection();
        }
        return (queryProgress == QueryProgress.ERROR) ? -1 : total;
    }

    public QueryProgress carryTransaction(double amount, TransactionTypes transactionTypes){
        openUpdateConnection();
        QueryProgress queryProgress;
        int transactionType = getTransactionTypes().get(transactionTypes);
        String currentDate = Date.valueOf(LocalDate.now()).toString();
        String query = String.format(Queries.carryoutTransactionQuery, amount, currentDate, transactionType);
        try{
            Statement statement = this.updateConnection.createStatement();
            statement.executeUpdate(query);
            logInfoMessage(String.format("Transaction ID %s carried out successfully", currentDate));
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage("carryTransaction", sqlException.getMessage());
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
            Statement statement = this.queryConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            while(resultSet.next()){
                billAmount = resultSet.getInt("bill_balance");
            }
            queryProgress = QueryProgress.COMPLETE;
        }catch (SQLException sqlException){
            logErrorMessage("getBill", sqlException.getMessage());
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
            logErrorMessage("payBill", sqlException.getMessage());
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
            logErrorMessage("addToBill", sqlException.getMessage());
            queryProgress = QueryProgress.ERROR;
        }finally {
            closeUpdateConnection();
        }
        return queryProgress;
    }
}
