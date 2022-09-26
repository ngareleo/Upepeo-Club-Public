package Connections;
public class Queries {
    protected static final String addMemberQuery = "INSERT INTO Members(first_name, surname, phone_number, Residential_address, date_added, national_id, membership_number) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')";
    protected static final String isUniqueCheckQuery = "SELECT * FROM %s WHERE %s='%s'";
    protected static final String addVideoQuery = "INSERT INTO videos (video_name, video_category, video_status, date_added) VALUES ( '%s', '%s', 1, '%s')";
    protected static final String getUserIDQuery = "SELECT * FROM members WHERE membership_number='%s'";
    protected static final String getVideoInformationQuery = "SELECT * FROM videos WHERE video_id='%s' OR video_name='%s'";
    protected static final String getMemberInformationQuery = "SELECT * FROM members WHERE membership_number='%s'";
    protected static final String borrowVideoQuery = "UPDATE videos SET borrower='%s', date_last_borrowed='%s', video_status=4 WHERE video_id=%d";
    protected static final String checkPendingVideosQuery = "SELECT * FROM videos WHERE video_status=4 AND borrower=%d";
    protected static final String totalAmountQuery = "SELECT * FROM finance";
    protected static final String carryoutTransactionQuery = "INSERT INTO finance (amount, date_added, transaction_type) VALUES (%f, '%s', %d)";
    protected static final String billQuery = "SELECT * FROM members WHERE membership_number='%s'";
    protected static final String payBillQuery = "UPDATE members SET bill_balance=0 WHERE membership_number='%s'";
    protected static final String addToBillMutation = "UPDATE members SET bill_balance=%d WHERE membership_number='%s'";
}
