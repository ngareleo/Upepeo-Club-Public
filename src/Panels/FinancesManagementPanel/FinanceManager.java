package Panels.FinancesManagementPanel;

import Connections.Database;
import Entities.Member;
import Entities.MemberHandler;
import Entities.Store;

import javax.swing.*;
import java.util.logging.Logger;

public class FinanceManager {
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
    private final  JLabel[] errors = {
            userError,
            dataBaseError
    };
    private final MemberHandler memberHandler;
    private final Store store;

    public FinanceManager(Database database, Logger logger){
        this.memberHandler = new MemberHandler(database, logger);
        this.store = new Store(database);
        prepareFinanceTab();
        addEventListeners();
    }

    private void addEventListeners() {
        clearBillButton.addActionListener(e -> {
            clearBillPanel.setVisible(true);
            mainPanel.setVisible(false);
        });

        exitButton.addActionListener( e -> {
            clearBillPanel.setVisible(false);
            mainPanel.setVisible(true);
        });

        clearBillButton_m.addActionListener( e -> {
            setErrorsInvisible();
            String membershipNumber = this.getMembershipNumberFromForm();
            if (membershipNumber == null) return;

            Member clearingMember = this.memberHandler.getMemberInformation(membershipNumber);
            double totalUserBill = clearingMember.getBillBalance();
            if (totalUserBill == 0) {/*show user there is no need to clear*/}
            boolean billHasBeenClearedSuccessfully = this.memberHandler.clearMemberBill(totalUserBill, clearingMember);
            if (!billHasBeenClearedSuccessfully) {
                dataBaseError.setVisible(true);
                dataBaseError.setText(FinanceManagementStrings.internalError);
            }
            JOptionPane.showMessageDialog(FinanceManager, FinanceManagementStrings.billClearedText);
        });

        generateReportButton.addActionListener( e -> {});

        refreshButton.addActionListener( e -> {});
    }

    private void populateFinanceTab(){
        String roundedTotalAmount = String.valueOf(this.store.getTotalCash());
        String roundedAdminAmount = String.valueOf(this.store.getAdministrationFunds());
        String roundedSalariesAmount = String.valueOf(this.store.getSalariesFunds());
        String roundedMiscellaneousAmount = String.valueOf(this.store.getMiscellaneousFunds());
        String roundedDevelopmentsAmount = String.valueOf(this.store.getDevelopmentFunds());

        this.totalAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, roundedTotalAmount));
        this.adminAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, roundedAdminAmount));
        this.salariesAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, roundedSalariesAmount));
        this.miscAmounts.setText(String.format(FinanceManagementStrings.kshMoneyFormat, roundedMiscellaneousAmount));
        this.devAmount.setText(String.format(FinanceManagementStrings.kshMoneyFormat, roundedDevelopmentsAmount));
    }

    private String getMembershipNumberFromForm() {
        String membershipNumber = this.membershipNumber.getText().trim();
        if(membershipNumber.equals(FinanceManagementStrings.NO_TEXT)){
            userError.setText(FinanceManagementStrings.memberShipNumberRequiredText);
            userError.setVisible(true);
            return null;
        }
        return membershipNumber;
    }

    private void prepareFinanceTab(){
        this.clearBillPanel.setVisible(false);
        setErrorsInvisible();
        populateFinanceTab();
    }

    private void setErrorsInvisible(){for(JLabel error: errors) error.setVisible(false);}
}
