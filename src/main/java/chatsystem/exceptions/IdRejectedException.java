package chatsystem.exceptions;

public class IdRejectedException extends Exception{

    public IdRejectedException(int id) {
        super("ID Rejected : " + id);
    }
}
