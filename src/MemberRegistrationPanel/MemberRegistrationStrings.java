package MemberRegistrationPanel;

import Strings.Strings;

public class MemberRegistrationStrings extends Strings {
    public static final String dataSubmissionErrorText = "Error occurred while submitting data";
    public static final String memberRegistrationSuccessText = "Member added successfully";
    public static final String IDInUseText = "ID number is already in use";
    public static final String invalidIdText = "Enter a valid ID number (digits only)";
    public static final String invalidIDLengthErrorText = "ID number is invalid (between 7 - 8 digits)";
    public static final String phoneNumberInUseErrorText = "Phone number is already in use";
    public static final String invalidPhoneNumberFormatText = "Phone Number is invalid (format 07********)";
    public static final String invalidPhoneNumberContentText = "Phone number can contain digits only";
    public static final String invalidCharactersErrorText = "Enter characters [A - Z] only";
    public static final String[] fieldErrors = {
            "First name is required",
            "Surname is required",
            "Phone number is required",
            "National ID is required",
            "Occupation is required",
            "Residential Address is required",
    };
}
