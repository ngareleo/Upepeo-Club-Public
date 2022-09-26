import Connections.Database;
import Panels.VideoRegistrationPanel.LandingPanel.LandingPage;
import Strings.MainAppStrings;
import javax.swing.*;
import java.io.File;
import java.util.logging.Logger;

public class App extends JFrame {
    private final JPanel landingPanel;
    private static Logger logger;

    App(Database database, Logger logger) {
        App.logger = logger;
        LandingPage landingPage = new LandingPage(this, App.logger);
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

        if(!new File(dbFileLocation).exists()) {
            new Setup(jdbcUrl);
        }

        Logger logger = Logger.getLogger(App.class.getName());
        Database database = new Database(dbFileLocation, logger);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new App(database, logger);
    }
}
