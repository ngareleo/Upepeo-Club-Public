package Errors;

public class ReturningUnBorrowedVideoException extends  IllegalOperationException{
    public ReturningUnBorrowedVideoException() {
        super("Attempted to fine un-borrowed book");
    }
}
