package Entities;

import Tools.Types;

public class Member {
    private final String firstName;
    private final String surname;
    private final String phoneNumber;
    private final String nationalID;
    private final String residentialAddress;
    private final String membershipID;
    private double billBalance = 0;
    private String dateAdded = null;
    private Types.Genders gender;

    public Member(String firstName, String surname, String phoneNumber, String nationalID, String residentialAddress, String membershipID, String dateAdded, double billBalance){
        this.firstName = firstName;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.nationalID = nationalID;
        this.residentialAddress = residentialAddress;
        this.membershipID = membershipID;
        this.billBalance = billBalance;
        this.dateAdded = dateAdded;
    }

    // From form data
    public Member(String firstName, String surname, String phoneNumber, String nationalID, String residentialAddress, String membershipID){
        this.firstName = firstName;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.nationalID = nationalID;
        this.residentialAddress = residentialAddress;
        this.membershipID = membershipID;
    }

    Member(String firstName, String surname, String phoneNumber, String nationalID, String residentialAddress, String membershipID, String dateAdded, double billBalance, Types.Genders gender){
        this.firstName = firstName;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.nationalID = nationalID;
        this.residentialAddress = residentialAddress;
        this.membershipID = membershipID;
        this.billBalance = billBalance;
        this.dateAdded = dateAdded;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getSurname() {
        return surname;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getNationalID() {
        return nationalID;
    }
    public String getResidentialAddress() {
        return residentialAddress;
    }
    public String getMembershipID() {
        return membershipID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Member && ((Member) obj).membershipID.equals(this.membershipID))
            return true;
        return super.equals(obj);
    }

    public boolean canBeRegistered() {
        return this.firstName != null &&
                this.surname != null &&
                this.phoneNumber != null &&
                this.residentialAddress != null &&
                this.nationalID != null &&
                this.membershipID != null;
    }

    public double getBillBalance() {
        return this.billBalance;
    }
}
