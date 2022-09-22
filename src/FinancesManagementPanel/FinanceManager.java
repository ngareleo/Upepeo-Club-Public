package FinancesManagementPanel;

import Connections.Database;
import Tools.Types.*;

import javax.swing.*;
import java.util.logging.Logger;

public class FinanceManager {
    private final Database database;
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

    public FinanceManager(Database database, Logger logger){
        this.database = database;
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
            int amount = this.database.getBill(membershipNumber.getText().trim());
            setErrorsInvisible();
            if(membershipNumber.getText().trim().equals("")){
                userError.setText(FinanceManagementStrings.memberShipNumberRequiredText);
                userError.setVisible(true);
            }
            if(this.database.carryTransaction(amount, 3) == QueryProgress.ERROR){
                dataBaseError.setVisible(true);
                dataBaseError.setText(FinanceManagementStrings.internalError);
            }else{
                QueryProgress queryProgress = this.database.payBill(membershipNumber.getText().trim());
                if(queryProgress == QueryProgress.ERROR){
                    dataBaseError.setVisible(true);
                    dataBaseError.setText(FinanceManagementStrings.internalError);
                    return;
                }
                JOptionPane.showMessageDialog(FinanceManager, FinanceManagementStrings.billClearedText);
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
        int total_amount = database.getTotalAmount();
        int admin_amount = (int) (total_amount * 0.27),
                salaries_amount = (int) (total_amount * 0.54),
                expenses_amount = (int) (total_amount * 0.07);
        int remaining = total_amount - (admin_amount + salaries_amount + expenses_amount);
        totalAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, total_amount));
        adminAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, admin_amount));
        salariesAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, salaries_amount));
        miscAmounts.setText(String.format(FinanceManagementStrings.kshMoneyFormat, expenses_amount));
        devAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, remaining));
    }
}
