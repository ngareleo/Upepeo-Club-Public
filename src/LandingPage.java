import javax.swing.*;

public class LandingPage {
    public JPanel landingPage;
    public JButton addVideoButton;
    public JButton borrowVideoButton;
    public JButton addMemberButton;
    public JButton checkFinances;

    JPanel member_registration;
    JPanel video_reg_panel;
    JPanel finance_panel;
    JPanel video_borrow_panel;

    MemberRegister memberRegister;
    VideoRegister videoRegister;
    BorrowVideo borrowVideo;
    FinanceManager financeManager;

    JButton mem_reg_back_button, video_reg_back_btn, borrow_vid_home_btn, finances_home_btn;

    private final JFrame hostApp;

    LandingPage(JFrame hostApp) {
        this.hostApp = hostApp;
    }
    public void initializeTabs(Database database) {
        this.memberRegister = new MemberRegister(database);
        this.financeManager = new FinanceManager(database);
        this.videoRegister = new VideoRegister(database);
        this.borrowVideo = new BorrowVideo(database);

        this.member_registration = this.memberRegister.memberRegister;
        this.video_reg_panel = this.videoRegister.VideoRegister;
        this.video_borrow_panel = borrowVideo.BorrowVideo;
        this.finance_panel = financeManager.FinanceManager;

        this.mem_reg_back_button = memberRegister.backButton;
        this.video_reg_back_btn = videoRegister.backButton;
        this.borrow_vid_home_btn = borrowVideo.homeButton;
        this.finances_home_btn = financeManager.HomeButton;
    }

    void add_eventListener(){
        this.borrowVideoButton.addActionListener( e -> {
            int res = JOptionPane.showConfirmDialog(this.video_borrow_panel, "Are you sure you want to exit");
            if(res == 0){
                this.borrowVideo.authenticateUser();
                this.replace_panels(this.landingPage, this.video_borrow_panel);
            }
        });
        this.addMemberButton.addActionListener( e -> {
            this.replace_panels(this.member_registration, this.landingPage);
        });

        this.mem_reg_back_button.addActionListener( e -> {
            this.replace_panels(this.landingPage, this.member_registration);
        });

        this.addVideoButton.addActionListener( e -> {
            this.replace_panels(this.video_reg_panel, this.landingPage);
        });

        this.video_reg_back_btn.addActionListener( e -> {
            this.replace_panels(this.landingPage, this.video_reg_panel);
        });

        this.borrowVideoButton.addActionListener( e -> {
            this.replace_panels(this.video_borrow_panel, this.landingPage);
        });

        this.checkFinances.addActionListener( e -> {
            this.replace_panels(this.finance_panel, this.landingPage);
        });
        this.finances_home_btn.addActionListener( e -> {
            this.replace_panels(this.landingPage, this.finance_panel);
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
