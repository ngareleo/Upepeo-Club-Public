import javax.swing.*;
import java.io.IOException;

//TODO : Add more video information
//TODO : Add directors, video-length, year-of-production

public class App extends JFrame {


    LandingPage landingPage = new LandingPage();
    MemberRegister memberRegister = new MemberRegister();
    JPanel member_registration = memberRegister.memberRegister;
    VideoRegister videoRegister = new VideoRegister();
    JPanel video_reg_panel = videoRegister.VideoRegister;
    BorrowVideo borrowVideo = new BorrowVideo();
    JPanel video_borrow_panel = borrowVideo.BorrowVideo;
    FinanceManager financeManager = new FinanceManager();
    JPanel finance_panel = financeManager.FinanceManager;


    JButton add_video_btn = landingPage.addVideoButton,
    add_member_btn = landingPage.addMemberButton,
    borrow_btn = landingPage.borrowVideoButton,
    finance_btn = landingPage.checkFinances;

    JPanel landing_panel = landingPage.landingPage;
    JButton mem_reg_back_button = memberRegister.backButton,
            video_reg_back_btn = videoRegister.backButton;
    JButton borrow_vid_home_btn = borrowVideo.homeButton,
            finances_home_btn = financeManager.HomeButton;

    App() throws IOException {
        initial_setup();
        add_eventListener();
        final_setup();
    }
    void initial_setup(){
        setTitle("Upepeo Club");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(700, 600);
        add(landing_panel);
    }

    void final_setup(){
        revalidate();
        setVisible(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new App();

    }

    void add_eventListener(){
        borrow_vid_home_btn.addActionListener( e -> {
            int res = JOptionPane.showConfirmDialog(this.video_borrow_panel, "Are you sure you want to exit");
            if(res == 0){
                this.borrowVideo.authenticateUser();
                this.replace_panels(this.landing_panel, this.video_borrow_panel);
            }
        });
        this.add_member_btn.addActionListener( e -> {
            replace_panels(this.member_registration, this.landing_panel);
        });

        this.mem_reg_back_button.addActionListener( e -> {
            replace_panels(this.landing_panel, this.member_registration);
        });
        this.add_video_btn.addActionListener( e -> {
            replace_panels(this.video_reg_panel, this.landing_panel);
        });

        this.video_reg_back_btn.addActionListener( e -> {
            replace_panels(this.landing_panel, this.video_reg_panel);
        });

        this.borrow_btn.addActionListener( e -> {
            replace_panels(this.video_borrow_panel, this.landing_panel);
        });

        this.finance_btn.addActionListener( e -> {
            replace_panels(this.finance_panel, this.landing_panel);
        });
        this.finances_home_btn.addActionListener( e -> {
            replace_panels(this.landing_panel, this.finance_panel);
        });
    }

    private void replace_panels(JPanel new_panel, JPanel old_panel){
        remove(old_panel);
        repaint();
        add(new_panel);
        final_setup();
    }
}
