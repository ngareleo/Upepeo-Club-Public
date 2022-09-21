package LandingPanel;

import BorrowVideoPanel.BorrowVideo;
import Connections.Database;
import FinancesManagementPanel.FinanceManager;
import MemberRegistrationPanel.MemberRegister;
import VideoRegistrationPanel.VideoRegister;
import javax.swing.*;

public class LandingPage {
    public JPanel landingPage;
    public JButton addVideoButton;
    public JButton borrowVideoButton;
    public JButton addMemberButton;
    public JButton checkFinances;
    JPanel memberRegistrationPanel;
    JPanel videoRegPanel;
    JPanel financePanel;
    JPanel videoBorrowPanel;
    MemberRegister memberRegister;
    VideoRegister videoRegister;
    BorrowVideo borrowVideo;
    FinanceManager financeManager;
    JButton memberRegistrationBackButton, videoRegistrationBackButton, borrowVideoHomeButton, financesHomeBackButton;
    private final JFrame hostApp;

    public LandingPage(JFrame hostApp) {
        this.hostApp = hostApp;
    }

    public void initializeTabs(Database database) {
        this.memberRegister = new MemberRegister(database);
        this.financeManager = new FinanceManager(database);
        this.videoRegister = new VideoRegister(database);
        this.borrowVideo = new BorrowVideo(database);

        this.memberRegistrationPanel = this.memberRegister.memberRegister;
        this.videoRegPanel = this.videoRegister.VideoRegister;
        this.videoBorrowPanel = borrowVideo.BorrowVideo;
        this.financePanel = financeManager.FinanceManager;

        this.memberRegistrationBackButton = memberRegister.backButton;
        this.videoRegistrationBackButton = videoRegister.backButton;
        this.borrowVideoHomeButton = borrowVideo.homeButton;
        this.financesHomeBackButton = financeManager.HomeButton;
    }

    public void add_eventListener(){
        this.borrowVideoButton.addActionListener( e -> {
            int res = JOptionPane.showConfirmDialog(this.videoBorrowPanel, LandingPageStrings.landingPageExitConfirmationText);
            if(res == 0){
                this.borrowVideo.authenticateUser();
                this.replace_panels(this.landingPage, this.videoBorrowPanel);
            }
        });
        this.addMemberButton.addActionListener( e -> {
            this.replace_panels(this.memberRegistrationPanel, this.landingPage);
        });

        this.memberRegistrationBackButton.addActionListener(e -> {
            this.replace_panels(this.landingPage, this.memberRegistrationPanel);
        });

        this.addVideoButton.addActionListener( e -> {
            this.replace_panels(this.videoRegPanel, this.landingPage);
        });

        this.videoRegistrationBackButton.addActionListener(e -> {
            this.replace_panels(this.landingPage, this.videoRegPanel);
        });

        this.borrowVideoButton.addActionListener( e -> {
            this.replace_panels(this.videoBorrowPanel, this.landingPage);
        });

        this.checkFinances.addActionListener( e -> {
            this.replace_panels(this.financePanel, this.landingPage);
        });
        this.financesHomeBackButton.addActionListener(e -> {
            this.replace_panels(this.landingPage, this.financePanel);
        });
    }

    private void replace_panels(JPanel new_panel, JPanel old_panel){
        this.hostApp.remove(old_panel);
        this.hostApp.repaint();
        this.hostApp.add(new_panel);
        this.hostApp.revalidate();
        this.hostApp.setVisible(true);
    }
}
