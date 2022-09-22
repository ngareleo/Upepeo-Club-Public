package MemberRegistrationPanel;

import Connections.Database;
import javax.swing.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MemberRegister {
    private final Database database;
    public JPanel memberRegister;
    private JTextField firstName;
    private JButton submitButton;
    private JLabel submitError;
    private JLabel resAddressError;
    private JLabel IDError;
    private JLabel phoneError;
    private JLabel surnameError;
    private JLabel fnError;
    private JTextField surname;
    private JTextField phoneNumber;
    private JTextField nationalID;
    private JTextField resAddress;
    private JButton clearButton;
    private JTextField occupation;
    private JLabel occupationError;
    public JButton backButton;
    private JPanel formPanel;
    private final JLabel[] errors = {fnError, surnameError, phoneError,
            IDError, occupationError, resAddressError, submitError};
    private final JTextField[] fields = {
            firstName, surname, phoneNumber, nationalID, resAddress, occupation};

    public MemberRegister(Database database, Logger logger){
        this.database = database;
        setAllErrorsInvisible();
        addEventListeners();
    }

    private void setAllErrorsInvisible(){
        for(JLabel error: errors)
            error.setVisible(false);
    }

    private void addEventListeners(){
        submitButton.addActionListener( e -> {
            boolean allCorrect = true;
            setAllErrorsInvisible();
            if(!allFilled())return;

            String fnValue = firstName.getText().trim();
            String surnameValue = surname.getText().trim();
            String phoneNumberValue = phoneNumber.getText().trim();
            String IDValue = nationalID.getText().trim();
            String occupationValue = occupation.getText().trim();
            String resAddressValue = resAddress.getText().trim();
            String membershipNumber = generate_membershipNumber();

            allCorrect = checkNames(fnValue, surnameValue) && phoneNumberCheck(phoneNumberValue) && idNumberCheck(IDValue);
            if(!allCorrect)return;
            String[] final_data = {fnValue, surnameValue, phoneNumberValue, resAddressValue, IDValue, membershipNumber};
            submitData(final_data);
        });

        clearButton.addActionListener( e -> {
            clearAll();
        });
    }

    private void submitData(String[] data){
        int res = this.database.addMember(data);
        if(res != 1){
           submitError.setText(MemberRegistrationStrings.dataSubmissionErrorText);
           submitError.setVisible(true);
        }else{
            clearAll();
            database.carryTransaction(2000, 1);
            JOptionPane.showMessageDialog(memberRegister, MemberRegistrationStrings.memberRegistrationSuccessText);
        }
    }
    private boolean checkNames(String firstName, String surname){
        if(!(Pattern.matches( "[a-zA-Z]+", firstName))) {
            fnError.setVisible(true);
            fnError.setText(MemberRegistrationStrings.invalidCharactersErrorText);
            return false;
        }

        if(!(Pattern.matches( "[a-zA-Z]+", surname))) {
            surnameError.setVisible(true);
            surnameError.setText(MemberRegistrationStrings.invalidCharactersErrorText);
            return false;
        }
        return true;
    }

    private boolean phoneNumberCheck(String phoneNumber){
        //check if matches correct syntax
        if (!Pattern.matches("[0-9]+", phoneNumber)){
            phoneError.setVisible(true);
            phoneError.setText(MemberRegistrationStrings.invalidPhoneNumberContentText);
            return false;
        }
        if (!(phoneNumber.startsWith("07"))){
            phoneError.setVisible(true);
            phoneError.setText(MemberRegistrationStrings.invalidPhoneNumberFormatText);
            return false;
        }
        if(!(phoneNumber.length() == 10)){
            phoneError.setVisible(true);
            phoneError.setText(MemberRegistrationStrings.invalidPhoneNumberFormatText);
            return false;

        }

        if (!database.isUnique(phoneNumber, "members", "phone_number")){
            phoneError.setVisible(true);
            phoneError.setText(MemberRegistrationStrings.phoneNumberInUseErrorText);
            return false;
        }
        return true;
    }

    private boolean idNumberCheck(String in){
        if(!(in.length() == 7 || in.length() == 8)){
            IDError.setVisible(true);
            IDError.setText(MemberRegistrationStrings.invalidIDLengthErrorText);
            return false;
        }

        try{
            Integer i = Integer.valueOf(in);
        }catch (Exception u){
            IDError.setVisible(true);
            IDError.setText(MemberRegistrationStrings.invalidIdText);
            return false;
        }

        if (!database.isUnique(in, "members", "national_id")){
            IDError.setVisible(true);
            IDError.setText(MemberRegistrationStrings.IDInUseText);
            return false;
        }
        return true;
    }

    private String generate_membershipNumber(){
        //we append the timestamp on top of the
        Timestamp now = Timestamp.from(Instant.now());
        return String.valueOf(now.getTime());
    }

    private boolean allFilled(){
        int i = 0;
        boolean allCorrect = true;
        for(JTextField field: this.fields){
            if(field.getText().trim().equals("")){
                allCorrect = false;
                errors[i].setVisible(true);
                errors[i].setText(MemberRegistrationStrings.fieldErrors[i]);
            }
            i++;
        }
        return allCorrect;
    }

    private void clearAll(){
        for(JTextField field: fields)
            field.setText("");
    }
}
