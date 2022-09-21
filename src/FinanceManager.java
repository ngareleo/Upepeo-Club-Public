import javax.swing.*;

public class FinanceManager {
    private final Database db;
    private JButton refreshButton;
    private JButton generateReportButton;
    public JPanel FinanceManager;
    private JButton clearBillButton;
    private JTextField membershipNumber;
    private JButton clearBillButton_m;
    private JLabel userError;
    private JLabel dataBaseError;
    private JPanel clearBillPanel;
    public JButton HomeButton;
    private JLabel adminAmount;
    private JLabel salariesAmount;
    private JLabel miscAmounts;
    private JPanel mainPanel;
    private JLabel totalAmount;
    private JLabel devAmount;
    private JButton exitButton;
    final private JLabel[] errors = {userError, dataBaseError};

    FinanceManager(Database database){
        this.db = database;
        prepareFinanceTab();
        clearBillButton.addActionListener(e -> {
            clearBillPanel.setVisible(true);
            mainPanel.setVisible(false);
        });

        exitButton.addActionListener( e -> {
            clearBillPanel.setVisible(false);
            mainPanel.setVisible(true);
        });

        clearBillButton_m.addActionListener( e -> {
            int amount = db.getBill(membershipNumber.getText().trim());
            setErrorsInvisible();
            if(membershipNumber.getText().trim().equals("")){
                userError.setText("Membership number is required");
                userError.setVisible(true);
            }
            if(db.carryTransaction(amount, 3) == -1){
                dataBaseError.setVisible(true);
                dataBaseError.setText("Internal error occurred");
            }else{
                // clear the bill
                int res = db.payBill(membershipNumber.getText().trim());
                if(res == -1){
                    dataBaseError.setVisible(true);
                    dataBaseError.setText("Internal error occurred");
                    return;
                }
                JOptionPane.showMessageDialog(FinanceManager, "Bill was cleared");
            }
        });
        generateReportButton.addActionListener( e -> {
            // TODO: Add action
        });
    }

    private void prepareFinanceTab(){
        this.clearBillPanel.setVisible(false);
        setErrorsInvisible();
        populate_finance_tab();
    }
    private void setErrorsInvisible(){for(JLabel error: errors) error.setVisible(false);}

    private void populate_finance_tab(){
        int total_amount = db.getTotalAmount();
        int admin_amount = (int) (total_amount * 0.27),
                salaries_amount = (int) (total_amount * 0.54),
                expenses_amount = (int) (total_amount * 0.07);
        int remaining = total_amount - (admin_amount + salaries_amount + expenses_amount);
        totalAmount.setText(String.format("Ksh %d.00", total_amount));
        adminAmount.setText(String.format("Ksh %d.00", admin_amount));
        salariesAmount.setText(String.format("Ksh %d.00", salaries_amount));
        miscAmounts.setText(String.format("Ksh %d.00", expenses_amount));
        devAmount.setText(String.format("Ksh %d.00", remaining));
    }
}
