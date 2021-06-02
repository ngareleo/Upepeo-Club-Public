import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Setup {

    // TODO: setup the DB (Upepeo.db)
    String categories_setup =
            "CREATE TABLE categories (" +
                    "category_id INTEGER NOT NULL," +
                    "category_name VARCHAR(32) NOT NULL," +
                    "borrowing_rate INTEGER NOT NULL DEFAULT 0," +
                    "fine_rate INTEGER NOT NULL DEFAULT 0," +
                    "PRIMARY KEY (category_id)," +
                    "UNIQUE(category_id)," +
                    "UNIQUE(category_name));";
    String categories_data =
            "INSERT INTO " +
                    "categories (category_name, borrowing_rate, fine_rate) " +
            "VALUES" +
                    "('Comedy', 70, 42)," +
                    "('Science Fiction', 80, 52)," +
                    "('Documentary', 55, 45)," +
                    "('Action', 50, 30)," +
                    "('Horror', 60, 24)," +
                    "('Thriller', 60, 36)," +
                    "('Drama', 55, 45)," +
                    "('Cartoon', 80, 48);";

    // TODO: configure the Database.class
    String video_status_setup =
            "CREATE TABLE video_status (" +
                    "status_id INTEGER NOT NULL," +
                    "status_name VARCHAR(32) NOT NULL," +
                    "PRIMARY KEY (status_id)," +
                    "UNIQUE (status_id)," +
                    "UNIQUE (status_name));";
    String video_status_data  =
            "INSERT INTO " +
                "video_status (status_name) " +
            "VALUES " +
                    "('available')," +
                    "('withdrawn')," +
                    "('lost')," +
                    "('borrowed');";
    String transaction_type_setup =
            "CREATE TABLE transaction_type (" +
                    "transaction_id INTEGER NOT NULL," +
                    "name VARCHAR(32)," +
                    "PRIMARY KEY (transaction_id)," +
                    "UNIQUE (transaction_id)," +
                    "UNIQUE (name));";
    String transaction_type_data =
            "INSERT INTO " +
                    "transaction_type (name)" +
            "VALUES " +
                    "('member_registration')," +
                    "('borrowing fee')," +
                    "('fine fee');";

    String finance_setup =
            "CREATE TABLE finance (" +
                    "entry_id INTEGER NOT NULL," +
                    "amount INTEGER NOT NULL," +
                    "date_added DATE DEFAULT CURRENT_DATE," +
                    "transaction_type INTEGER NOT NULL," +
                    "PRIMARY KEY (entry_id)," +
                    "FOREIGN KEY (transaction_type) REFERENCES transaction_type(transaction_id)," +
                    "UNIQUE (entry_id));";
    String finance_data  =
            "INSERT INTO " +
                        "finance (amount, transaction_type) " +
            "VALUES " +
                        "(2000, 1);";

    String members_setup =
            "CREATE TABLE members (" +
                    "user_id INTEGER NOT NULL," +
                    "membership_number VARCHAR(64) NOT NULL," +
                    "first_name VARCHAR(32) NOT NULL," +
                    "surname VARCHAR(32) NOT NULL, " +
                    "phone_number VARCHAR(16) NOT NULL," +
                    "residential_address VARCHAR(32), " +
                    "date_added DATE DEFAULT CURRENT_DATE," +
                    "national_id VARCHAR(16) NOT NULL, " +
                    "bill_balance INTEGER," +
                    "gender VARCHAR(16)," +
                    "PRIMARY KEY (user_id)," +
                    "UNIQUE (user_id)," +
                    "UNIQUE (phone_number)," +
                    "UNIQUE (national_id)," +
                    "UNIQUE (membership_number));";

    String member_data =
            "INSERT INTO " +
                    "members (membership_number, first_name, surname, phone_number, national_id) " +
            "VALUES " +
                    "('0000', 'Admin', 'Admin', '00', '00'); ";

    String directors_setup  =
            "CREATE TABLE director (" +
                    "director_id INTEGER NOT NULL," +
                    "director_name VARCHAR(64) NOT NULL," +
                    "PRIMARY KEY (director_id)," +
                    "UNIQUE (director_id));";

    String directors_data =
            "INSERT INTO " +
                    "director (director_name)" +
            "VALUES " +
                    "('Anthony Russo')," +
                    "('Peter Farrelly')," +
                    "('Josh Gordon')," +
                    "('Max Barbakow')," +
                    "('David Leitch')," +
                    "('Jason Moore')," +
                    "('Paul Weitz')," +
                    "('Joel Coen')," +
                    "('Christopher Nolan')," +
                    "('John Kraninski')," +
                    "('James Gunn')," +
                    "('Alex Garland')," +
                    "('Lana Wachowski')," +
                    "('Jason Ferguson')," +
                    "('Jeff Orlowski')," +
                    "('Charles Ferguson')," +
                    "('Evan Spiliotopolous')," +
                    "('Andy Muschietii');";

    String videos_setup =
            "CREATE TABLE videos (" +
                    "video_id INTEGER NOT NULL," +
                    "video_name VARCHAR(32) NOT NULL," +
                    "video_category INTEGER NOT NULL," +
                    "director INTEGER NOT NULL, " +
                    "year_of_production INTEGER NOT NULL," +
                    "video_length INTEGER NOT NULL," +
                    "borrower INTEGER," +
                    "video_status INTEGER NOT NULL DEFAULT 1," +
                    "date_added DATE DEFAULT CURRENT_DATE," +
                    "date_last_borrowed DATE," +
                    "latest_report TEXT(128)," +
                    "PRIMARY KEY (video_id)," +
                    "FOREIGN KEY (video_category) REFERENCES categories (category_id)," +
                    "FOREIGN KEY (director) REFERENCES director (director_id)," +
                    "FOREIGN KEY (borrower) REFERENCES members(user_id)," +
                    "FOREIGN KEY (video_status) REFERENCES video_status(status_id)," +
                    "UNIQUE(video_id));";
    String videos_data =
            "INSERT INTO " +
                    "videos (video_name, video_category, director, year_of_production, video_length) " +
            "VALUES " +
                    "('Dumb and Dumber', 1, 2, 1994, 107)," +
                    "('Avengers Endgame', 4, 1, 2019, 181)," +
                    "('Office Christmas Party', 3, 1, 2016, 105)," +
                    "('Palm Springs', 1, 4, 2020, 90)," +
                    "('Deadpool', 1, 5, 2017, 119)," +
                    "('Pitch Perfect', 1, 6, 2014, 112)," +
                    "('American Pie', 1, 7, 1999, 95)," +
                    "('The Big Lebowski', 1, 8, 1998, 117)," +
                    "('Tenet', 2, 9, 2020, 150)," +
                    "('A quiet place', 2, 10, 2018, 90)," +
                    "('Interstellar', 2, 9, 2014, 169)," +
                    "('Inception', 2, 9, 2014, 118)," +
                    "('Guardians of the Galaxy', 2, 11, 2014, 121)," +
                    "('Ex machina', 2, 12, 2014, 108)," +
                    "('The Matrix', 2, 13, 1999, 136)," +
                    "('Sir Alex Ferguson: Never Give In', 3, 14, 2021, 130)," +
                    "('The Social Dilemma', 3, 15, 2020, 94)," +
                    "('Inside Job', 3, 16, 2010, 82)," +
                    "('The Unholy', 5, 17, 2021, 99)," +
                    "('It', 5, 18, 2017, 135);";

    private Connection conn = null;
    String url = "jdbc:sqlite:./res/sample.db";

    Setup(){
        System.out.println(this.url);
        openConnection();
        System.out.println("#################################################");
        System.out.println("#                                               #");
        System.out.println("#         Initial Connection established        #");
        System.out.println("#                                               #");
        System.out.println("#################################################\n");
        closeConnection();
        System.out.println("#################################################");
        System.out.println("#                                               #");
        System.out.println("#           Initial Connection closed           #");
        System.out.println("#                                               #");
        System.out.println("#################################################\n");
    }
    private void openConnection(){
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void closeConnection(){
        try {
            if (this.conn != null) {
                this.conn.close();
            }else{
                System.out.println("Connection was closed");
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createDB(){
        this.openConnection();
        try {
            Statement statement = this.conn.createStatement();
            statement.execute(this.categories_setup);
            System.out.println("CATEGORIES table created successfully");
            statement.execute(this.categories_data);
            System.out.println("Data added to categories table successfully");
            statement.execute(this.video_status_setup);
            System.out.println("VIDEO_STATUS table created successfully");
            statement.execute(this.video_status_data);
            System.out.println("Data added to video_status table successfully");
            statement.execute(this.transaction_type_setup);
            System.out.println("TRANSACTION_TYPE table created successfully");
            statement.execute(this.transaction_type_data);
            System.out.println("Data added to transaction_type table successfully");
            statement.execute(this.finance_setup);
            System.out.println("FINANCE table created successfully");;
            statement.execute(this.finance_data);
            System.out.println("Data added to finance table successfully");
            statement.execute(this.members_setup);
            System.out.println("MEMBERS table created successfully");
            statement.execute(this.member_data);
            System.out.println("Data added to member table successfully");
            statement.execute(this.directors_setup);
            System.out.println("DIRECTORS table created successfully");
            statement.execute(this.directors_data);
            System.out.println("Data added to directors table successfully");
            statement.execute(this.videos_setup);
            System.out.println("VIDEOS table created successfully");
            statement.execute(this.videos_data);
            System.out.println("Data added to videos table successfully");

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            closeConnection();
        }
    }
    public static void main(String[] args) throws IOException {
        File dbFile = new File("./res/sample.db");
        if(dbFile.exists()){
            System.out.println("Database file located");
            return;
        }
        dbFile.createNewFile();
        Setup setup = new Setup();
        setup.createDB();
        System.out.println("Database file created");
    }
}