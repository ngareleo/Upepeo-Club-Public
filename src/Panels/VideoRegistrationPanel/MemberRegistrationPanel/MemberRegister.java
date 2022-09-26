package Panels.VideoRegistrationPanel.MemberRegistrationPanel;

import Connections.Database;
import Entities.Member;
import Entities.MemberHandler;
import Errors.RegisteringIncompleteMember;
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
    private MemberHandler memberHandler;
    private final JLabel[] errorLabels = {
            fnError,
            surnameError,
            phoneError,
            IDError,
            occupationError,
            resAddressError,
            submitError
    };
    private final JTextField[] formFields = {
            firstName,
            surname,
            phoneNumber,
            nationalID,
            resAddress,
            occupation
    };

    public MemberRegister(Database database, Logger logger){
        this.database = database;
        setAllErrorsInvisible();
        addEventListeners();
    }

    private void addEventListeners(){

        submitButton.addActionListener( e -> {
            setAllErrorsInvisible();
            if(!allFilled())return;
            String firstNameFormValue = firstName.getText().trim();
            String surnameFormValue = surname.getText().trim();
            String phoneNumberFormValue = phoneNumber.getText().trim();
            String nationalIDFormValue = nationalID.getText().trim();
            String occupationFormValue = occupation.getText().trim();
            String resAddressFormValue = resAddress.getText().trim();
            String membershipNumber = generate_membershipNumber();

            boolean allCorrect = checkNames(firstNameFormValue, surnameFormValue) && phoneNumberCheck(phoneNumberFormValue) && idNumberCheck(nationalIDFormValue);
            if(!allCorrect) return;
            Member member = new Member(
                    firstNameFormValue,
                    surnameFormValue,
                    phoneNumberFormValue,
                    nationalIDFormValue,
                    resAddressFormValue,
                    membershipNumber
            );
            registerNewMember(member);
        });

        clearButton.addActionListener( e -> {
            clearAll();
        });
    }

    private void registerNewMember(Member member){
        boolean memberHasBeenRegistered = false;
        try {
            memberHasBeenRegistered = this.memberHandler.registerNewMember(member);
        } catch (RegisteringIncompleteMember registeringIncompleteMemberException) {
            showRegistrationError();
        }
        if (!memberHasBeenRegistered) {
            showRegistrationError();
        }else {
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
        if (database.entityExists(phoneNumber, "members", "phone_number")){
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

        if (database.entityExists(in, "members", "national_id")){
            IDError.setVisible(true);
            IDError.setText(MemberRegistrationStrings.IDInUseText);
            return false;
        }
        return true;
    }

    private String generate_membershipNumber(){
        Timestamp now = Timestamp.from(Instant.now());
        return String.valueOf(now.getTime());
    }

    private boolean allFilled(){
        int i = 0;
        boolean allCorrect = true;
        for(JTextField field: this.formFields){
            if(field.getText().trim().equals(MemberRegistrationStrings.NO_TEXT)){
                allCorrect = false;
                errorLabels[i].setVisible(true);
                errorLabels[i].setText(MemberRegistrationStrings.fieldErrors[i]);
            }
            i++;
        }
        return allCorrect;
    }

    private void clearAll(){
        for(JTextField field: formFields)
            field.setText(MemberRegistrationStrings.NO_TEXT);
    }

    private void setAllErrorsInvisible(){
        for(JLabel error: errorLabels)
            error.setVisible(false);
    }

    private void showRegistrationError() {
        submitError.setVisible(true);submitError.setText(MemberRegistrationStrings.dataSubmissionErrorText);
        submitError.setVisible(true);
    }
}
