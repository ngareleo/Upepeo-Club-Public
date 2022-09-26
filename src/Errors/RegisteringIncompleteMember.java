package Errors;

public class RegisteringIncompleteMember extends IllegalOperationException{

    public RegisteringIncompleteMember() {
        super("Attempted to register a member object that is incomplete");
    }
}
