package BorrowVideoPanel;

import Connections.Database;
import Tools.Types.*;
import Tools.Utils;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class BorrowVideo {
    private final Logger logger;
    int user_id, borrowing_rate, fine_rate, total_charge, num_of_pending_videos;
    String mem_num;
    String[] video_information;
    Boolean lost, damaged;
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
    private JButton backButton;
    public JButton homeButton;
    private JButton returnButton;
    private JLabel overdueDaysLabel;
    private JLabel overdueDays;
    private JCheckBox damagedCheckBox;
    private JCheckBox lostCheckBox;
    private JPanel reportPanel;
    private JLabel sideInfo;
    private final JLabel[] allErrors = {dbError, memUserError, searchDatabaseError, searchUserError, borrowingError, sideInfo};
    private final JTextField[] allTexts = {searchBar, membershipNumber};
    private final String[] choices = {"Pay Now", "Add to Bill"};

    public BorrowVideo(Database database, Logger logger){
        this.logger = logger;
        this.database = database;
        authenticateUser();
    }

    public void authenticateUser(){
        clearAll();
        prepareComponents();
        getUserInfo();
    }

    private void prepareComponents(){
        setAllErrorsInvisible();
        authPanel.setVisible(true);
        mainPanel.setVisible(false);
        backButton.setVisible(false);
        overdueDays.setVisible(false);
        overdueDaysLabel.setVisible(false);
        reportPanel.setVisible(false);
    }

    private void setAllErrorsInvisible(){
        for(JLabel error: allErrors){
            error.setVisible(false);
        }
    }

    private void clearAll(){
        for(JTextField text: allTexts)
            text.setText("");
    }

    private void prepareReturnPanel(){
        reportPanel.setVisible(false);
        overdueDays.setVisible(false);
        overdueDaysLabel.setVisible(false);
    }

    private void getUserInfo(){
        ImageIcon imageIcon = new ImageIcon("./res/Error_36910.gif");
        enterButton.addActionListener( e -> {
            int user_id;
            setAllErrorsInvisible();
            String user_mem_id = membershipNumber.getText().trim();
            if(user_mem_id.trim().equals("")){
                memUserError.setVisible(true);
                memUserError.setText(BorrowVideoStrings.memberIDMissingErrorText);
                return;
            }
            user_id = database.getUserId(user_mem_id);
            this.mem_num = user_mem_id;
            if(user_id == -2){
                dbError.setIcon(imageIcon);
                dbError.setText(BorrowVideoStrings.memberNotFoundErrorText);
                logger.warning("Error! User not found");
                dbError.setVisible(true);
                return;
            }else if(user_id == -1){
                dbError.setIcon(imageIcon);
                dbError.setVisible(true);
                logger.warning(BorrowVideoStrings.backendErrorText);
                dbError.setText(BorrowVideoStrings.backendErrorText);
                return;
            }else{
                this.user_id = user_id;
            }
            userAuthorized();
            init();
        });
    }
    private void userAuthorized(){
        mainPanel.setVisible(true);
        authPanel.setVisible(false);
        resultPanel.setVisible(false);
    }

    private void init(){
        prepareReturnPanel();
        resultPanel.setVisible(false);
        backButton.setVisible(true);
        backButton.addActionListener( e -> {
            clearAll();
            JOptionPane.showMessageDialog(BorrowVideo, BorrowVideoStrings.videoManagementExitText);
            authenticateUser();
        });

        searchButton.addActionListener( e -> {

            String[] video_info;
            setAllErrorsInvisible();
            prepareReturnPanel();
            String query = searchBar.getText().trim();
            if(query.equals("")){
                searchUserError.setText(BorrowVideoStrings.searchBarPlaceholderText);
                searchUserError.setVisible(true);
                return;
            }
            this.num_of_pending_videos = database.checkPendingVideos(this.user_id);
            video_info = database.getVideoInformation(query);
            if(video_info[0] == null){
                searchDatabaseError.setText(BorrowVideoStrings.videoNotFoundText);
                searchDatabaseError.setVisible(true);
                return;
            }else if(video_info[0].equals("-1")){
                searchDatabaseError.setText(BorrowVideoStrings.backendErrorText);
                searchDatabaseError.setVisible(true);
                return;
            }else{
                this.video_information = video_info;
            }
            populateVideoSearchPanel();
        });

        returnButton.addActionListener( e -> {
            int video_st = 1;
            if(lostCheckBox.isSelected()){
                if(!this.lost)
                    this.total_charge += 700;
                this.lost = true;
            }
            if(damagedCheckBox.isSelected()){
                if (!this.lost) {
                    this.total_charge += 700;
                }
                this.damaged = true;
            }
            StringBuilder videoReport = new StringBuilder();
            if(this.damaged){
                videoReport.append(BorrowVideoStrings.videoStatusDamagedText);
                video_st = 2;
            }

            if (this.lost){
                videoReport.append(BorrowVideoStrings.videoStatusLostText);
                video_st = 3;
            }
            if(!(this.lost || this.damaged))
                videoReport.append(BorrowVideoStrings.videoStatusNormalText);
            QueryProgress queryProgress = database.returnVideo(Integer.parseInt(this.video_information[0]), String.valueOf(videoReport), video_st);
            if(queryProgress == QueryProgress.ERROR){
                borrowingError.setText(BorrowVideoStrings.backendErrorText);
                borrowingError.setVisible(true);
                return;
            }

            String defaultChoice = choices[0];
            int option = JOptionPane.showOptionDialog(BorrowVideo,
                    "Select one of the values",
                    "Title message",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    choices,
                    defaultChoice);
            if(option == 0){
                database.carryTransaction(this.borrowing_rate, 2);
            }else {
                database.addToBill(this.borrowing_rate, this.mem_num);
            }
            JOptionPane.showMessageDialog(BorrowVideo, BorrowVideoStrings.videoReturnSuccessText);
            init();
        });


        borrowButton.addActionListener( e -> {
            if(this.num_of_pending_videos == 5){
                borrowingError.setText(BorrowVideoStrings.videoCapacityExceededText);
                borrowingError.setVisible(true);
                return;
            }
            if(!this.video_information[2].equals("1")){
                borrowingError.setText(BorrowVideoStrings.videoUnavailableText);
                borrowingError.setVisible(true);
                return;
            }
            String defaultChoice = this.choices[0];
            int option = JOptionPane.showOptionDialog(BorrowVideo,
                    "Select one of the values",
                    "Title message",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    this.choices,
                    defaultChoice);
            if(option == 0){
                database.carryTransaction(this.borrowing_rate, 2);
            }else {
                database.addToBill(this.borrowing_rate, this.mem_num);
            }
            AtomicInteger confirm = new AtomicInteger();
            QueryProgress queryProgress;
            if(confirm.get() == 0){
                queryProgress = database.borrowVideo(Integer.parseInt(this.video_information[0]), user_id);
                if(queryProgress == QueryProgress.ERROR){
                    borrowingError.setText(BorrowVideoStrings.backendErrorText);
                    borrowingError.setVisible(true);
                    return;
                }
            }else{return;}
            JOptionPane.showMessageDialog(BorrowVideo, BorrowVideoStrings.videoBorrowedSuccessText);
            init();
        });
    }

    private int calculateDueDays(String then){
        int days = 0;
        String[] dateArgs = then.split("-");
        LocalDate localDate = LocalDate.of(Integer.parseInt(dateArgs[0]),
                Integer.parseInt(dateArgs[1]),
                Integer.parseInt(dateArgs[2]));
        Period period = Period.between(localDate, LocalDate.now());
        days += (period.getDays() + (period.getMonths() * 28) + (period.getYears() * 12 * 28));
        return days;
    }

    private void populateVideoSearchPanel() {
        turnOn();
        resultPanel.setVisible(true);
        this.total_charge = 0;
        this.lost = false;
        this.damaged = false;
        boolean lost = false;
        String[] videoInformation = this.video_information;
        this.borrowing_rate = Utils.getBorrowingRate().get(Integer.parseInt(videoInformation[1]));
        this.fine_rate = Utils.getFineRate().get(Integer.parseInt(videoInformation[1]));
        String videoCategory = Utils.getCategoryName().get(Integer.parseInt(videoInformation[1])),
                video_status = Utils.getStatusInfo().get(Integer.parseInt(videoInformation[2]));
        if(videoInformation[2].equals("4") && Integer.parseInt(videoInformation[4]) == this.user_id){
            borrowButton.setVisible(false);
            returnButton.setVisible(true);
            overdueDays.setVisible(true);
            overdueDaysLabel.setVisible(true);
            int due_days = calculateDueDays(videoInformation[5]);
            if(due_days > 14) lost = true;
            else due_days = 0;
            if(due_days > 3) due_days -= 3;
            if(lost){
                sideInfo.setText(BorrowVideoStrings.lateVideoText);
                this.total_charge += 700;
            }
            overdueDays.setText(due_days + "%s days");
            this.total_charge += due_days * this.fine_rate;
            videoPriceLabel.setText(String.format(BorrowVideoStrings.kshMoneyFormat, this.total_charge));
            reportPanel.setVisible(true);

        }else{
            returnButton.setVisible(false);
            videoPriceLabel.setText(String.format(BorrowVideoStrings.kshMoneyFormat, this.borrowing_rate));
        }
        if (this.video_information[2].equals("1")){
            videoStatus.setText(video_status);
            videoStatus.setForeground(new Color(6, 105,0));
        }else{
            videoStatus.setText(video_status);
            videoStatus.setForeground(new Color(137, 6, 35));
        }
        videoCategoryLabel.setText(videoCategory);
        videoNameLabel.setText(videoInformation[3]);
    }

    private void turnOn(){
        returnButton.setVisible(true);
        borrowButton.setVisible(true);
    }
}
