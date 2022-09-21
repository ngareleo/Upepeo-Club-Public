import Connections.Database;
import LandingPanel.LandingPage;
import Strings.MainAppStrings;
import javax.swing.*;
import java.io.File;

public class App extends JFrame {

    private final JPanel landingPanel;

    App(Database database) {
        LandingPage landingPage = new LandingPage(this);
        landingPanel = landingPage.landingPage;
        landingPage.initializeTabs(database);
        initial_setup();
        landingPage.add_eventListener();
        final_setup();
    }

    void initial_setup(){
        setTitle(MainAppStrings.appTitle);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(700, 600);
        add(landingPanel);
    }

    void final_setup(){
        revalidate();
        setVisible(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
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
