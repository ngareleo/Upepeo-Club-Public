import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class App extends JFrame {
    LandingPage landingPage;

    App(Database database) {
        landingPage = new LandingPage(this);
        JPanel landing_panel = landingPage.landingPage;
        landingPage.initializeTabs(database);
        initial_setup(landing_panel);
        landingPage.add_eventListener();
        final_setup();
    }


    void initial_setup(JPanel landing_panel){
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
        String dbFileLocation = "./res/upepeo.sqlite";
        String jdbcUrl = String.format("jdbc:sqlite:%s", dbFileLocation);
        File dbFile = new File(dbFileLocation);
        if(!dbFile.exists()) {
            new Setup(jdbcUrl);
        }
        Database database = new Database(dbFileLocation);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new App(database);
    }
}
