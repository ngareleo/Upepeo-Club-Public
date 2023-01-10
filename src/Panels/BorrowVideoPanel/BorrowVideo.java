package Panels.BorrowVideoPanel;

import Connections.Database;
import Entities.Member;
import Entities.Video;
import Entities.VideoHandler;
import Tools.Types.*;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BorrowVideo {
    private final Logger logger;
    private final VideoHandler videoHandler;
    private int numberOfPendingVideos;
    public JButton backButton;
    public JButton homeButton;
    private Member loggedInUser;
    private Video focusedVideo;
    private final Database database;
    public JPanel BorrowVideo;
    private JTextField searchBar;
    private JButton searchButton;
    private JButton borrowButton;
    private JTextField membershipNumber;
    private JButton enterButton;
    private JLabel memUserError;
    private JPanel authPanel;
    private JPanel mainPanel;
    private JLabel dbError;
    private JLabel searchDatabaseError;
    private JPanel resultPanel;
    private JLabel searchUserError;
    private JLabel videoStatus;
    private JLabel videoNameLabel;
    private JLabel videoCategoryLabel;
    private JLabel videoPriceLabel;
    private JLabel borrowingError;
    private JButton returnButton;
    private JLabel overdueDaysLabel;
    private JLabel overdueDays;
    private JCheckBox damagedCheckBox;
    private JCheckBox lostCheckBox;
    private JPanel reportPanel;
    private JLabel sideInfo;
    private final JLabel[] errorFieldsLabels = {
            dbError,
            memUserError,
            searchDatabaseError,
            searchUserError,
            borrowingError,
            sideInfo
    };
    private final JTextField[] textFields = {
            searchBar,
            membershipNumber
    };
    private static final String PAY_NOW_PAYMENT_OPTION = "Pay Now";
    private static final String ADD_TO_BILL_PAYMENT_OPTION = "Add to bill";

    public BorrowVideo(Database database, Logger logger){
        this.logger = logger;
        this.database = database;
        videoHandler = new VideoHandler(this.database, this.logger);
        launchAuthenticationPanel();
    }

    public void launchAuthenticationPanel(){
        clearAllTextFields();
        renderAuthenticationPanel();
        authenticateUser();
    }

    private void prepareReturnPanel(){
        reportPanel.setVisible(false);
        overdueDays.setVisible(false);
        overdueDaysLabel.setVisible(false);
    }

    private void authenticateUser(){
        enterButton.addActionListener( e -> {
            Member member;
            setAllErrorsInvisible();
            String userMembershipID = membershipNumber.getText().trim();
            if(userMembershipID.trim().equals(BorrowVideoStrings.NO_TEXT)){
                memUserError.setVisible(true);
                memUserError.setText(BorrowVideoStrings.memberIDMissingErrorText);
                return;
            }
            member = this.videoHandler.getMemberInformation(userMembershipID);
            if(member == null){
                dbError.setText(BorrowVideoStrings.memberNotFoundErrorText);
                logger.warning("Error! User not found");
                dbError.setVisible(true);
                return;
            }else{
                this.loggedInUser = member; 
            }
            removeLoginPanel();
            initializeBorrowPanel();
        });
    }

    private void initializeBorrowPanel(){
        prepareReturnPanel();
        resultPanel.setVisible(false);
        backButton.setVisible(true);
        addButtonListeners();
    }

    private void addButtonListeners() {
        backButton.addActionListener( e -> {
            clearAllTextFields();
            JOptionPane.showMessageDialog(BorrowVideo, BorrowVideoStrings.videoManagementExitText);
            launchAuthenticationPanel();
        });

        searchButton.addActionListener( e -> {
            setAllErrorsInvisible();
            prepareReturnPanel();
            handleVideoSearch();
        });

        returnButton.addActionListener( e -> {
            Video focusedVideo = this.focusedVideo;
            StringBuilder videoReport = new StringBuilder();
            boolean lostCheckBoxIsSelected = lostCheckBox.isSelected();
            boolean damagedCheckBoxIsSelected = damagedCheckBox.isSelected();

            if(lostCheckBoxIsSelected){
                videoReport.append(BorrowVideoStrings.videoStatusDamagedText);
                focusedVideo.setVideoStatus(VideoStatus.LOST);
                focusedVideo.chargeBookForLossOrDamage();
            }
            else if (damagedCheckBoxIsSelected) {
                videoReport.append(BorrowVideoStrings.videoStatusDamagedText);
                focusedVideo.setVideoStatus(VideoStatus.WITHDRAWN);
                focusedVideo.chargeBookForLossOrDamage();
            }
            else {
                videoReport.append(BorrowVideoStrings.videoStatusNormalText);
            }
            focusedVideo.setLatestVideoReport(videoReport.toString());
            if (!this.videoHandler.returnVideo(focusedVideo)) {
                borrowingError.setText(BorrowVideoStrings.backendErrorText);
                borrowingError.setVisible(true);
                return;
            }
            handleVideoPayment(focusedVideo.getBorrowingRate());
            initializeBorrowPanel();
        });


        borrowButton.addActionListener( e -> {
            Video focusedVideo = this.focusedVideo;
            if(this.numberOfPendingVideos == 5){
                borrowingError.setText(BorrowVideoStrings.videoCapacityExceededText);
                borrowingError.setVisible(true);
                return;
            }
            if(!focusedVideo.isAvailable()){
                borrowingError.setText(BorrowVideoStrings.videoUnavailableText);
                borrowingError.setVisible(true);
                return;
            }
            if (!this.videoHandler.borrowVideo(focusedVideo, loggedInUser)){
                borrowingError.setText(BorrowVideoStrings.backendErrorText);
                borrowingError.setVisible(true);
                return;
            }
            this.handleVideoPayment(focusedVideo.getBorrowingRate());
            initializeBorrowPanel();
        });
    }

    private void populateVideoSearchPanel() {
        int videoFineAccumulated;
        this.renderReturnPanel();
        Video focusedVideo = this.focusedVideo;
        try {
             videoFineAccumulated = focusedVideo.checkBookFine();
        } catch (Exception exception) {
            this.logger.log(Level.WARNING, "Attempted to return un-borrowed video");
            return;
        }
        if (videoFineAccumulated <= 0) {
            this.renderVideoBorrowComponents();
            videoPriceLabel.setText(String.format(BorrowVideoStrings.kshMoneyFormat,  focusedVideo.getBorrowingRate()));
        }else {
            renderVideoReturnComponents();
            if (focusedVideo.isLost()) {
                sideInfo.setText(BorrowVideoStrings.lateVideoText);
            }
            overdueDays.setText(String.format("%s days", focusedVideo.getDueDays()));
            videoPriceLabel.setText(String.format(BorrowVideoStrings.kshMoneyFormat, videoFineAccumulated));
            reportPanel.setVisible(true);
        }

        String videoStatusString = focusedVideo.getVideoStatusString();
        if (focusedVideo.isAvailable()){
            videoStatus.setText(videoStatusString);
            videoStatus.setForeground(new Color(6, 105,0));
        }else{
            videoStatus.setText(videoStatusString);
            videoStatus.setForeground(new Color(137, 6, 35));
        }
        videoCategoryLabel.setText(focusedVideo.getVideoCategoryString());
        videoNameLabel.setText(focusedVideo.getVideoName());
    }

    private void handleVideoSearch() {
        String query = searchBar.getText().trim();
        if(query.equals(BorrowVideoStrings.NO_TEXT)){
            searchUserError.setText(BorrowVideoStrings.searchBarPlaceholderText);
            searchUserError.setVisible(true);
            return;
        }
        Video searchedVideo = this.videoHandler.fetchVideo(query);
        if(searchedVideo == null){
            searchDatabaseError.setText(BorrowVideoStrings.backendErrorText);
            searchDatabaseError.setVisible(true);
        }
        else if(searchedVideo.isInvalid()){
            searchDatabaseError.setText(BorrowVideoStrings.videoNotFoundText);
            searchDatabaseError.setVisible(true);
        }else{
            this.focusedVideo = searchedVideo;
            this.populateVideoSearchPanel();
        }
    }

   private void handleVideoPayment(int borrowingRate) {
       int option = JOptionPane.showOptionDialog(BorrowVideo,
               "Select one of the values",
               "Title message",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               new String[] {PAY_NOW_PAYMENT_OPTION, ADD_TO_BILL_PAYMENT_OPTION},
               PAY_NOW_PAYMENT_OPTION);

       if(option == 0){
           database.carryTransaction(borrowingRate, TransactionTypes.BORROWING_FEE);
       }else {
           database.addToBill(borrowingRate, loggedInUser.getMembershipID());
       }
       JOptionPane.showMessageDialog(BorrowVideo, BorrowVideoStrings.videoReturnSuccessText);
   }

    private void setAllErrorsInvisible(){
        for(JLabel error: errorFieldsLabels) error.setVisible(false);
    }

    private void clearAllTextFields(){
        for(JTextField text: textFields) text.setText(BorrowVideoStrings.NO_TEXT);
    }

   private void renderVideoReturnComponents() {
       borrowButton.setVisible(false);
       returnButton.setVisible(true);
       overdueDays.setVisible(true);
       overdueDaysLabel.setVisible(true);
   }

    private void renderVideoBorrowComponents() {
        this.overdueDays.setVisible(false);
        this.overdueDaysLabel.setVisible(false);
        this.returnButton.setVisible(false);
    }

    private void renderAuthenticationPanel(){
        setAllErrorsInvisible();
        authPanel.setVisible(true);
        mainPanel.setVisible(false);
        backButton.setVisible(false);
        overdueDays.setVisible(false);
        overdueDaysLabel.setVisible(false);
        reportPanel.setVisible(false);
    }

    private void removeLoginPanel(){
        mainPanel.setVisible(true);
        authPanel.setVisible(false);
        resultPanel.setVisible(false);
    }

    private void renderReturnPanel() {
        returnButton.setVisible(true);
        borrowButton.setVisible(true);
        resultPanel.setVisible(true);
    }
}
