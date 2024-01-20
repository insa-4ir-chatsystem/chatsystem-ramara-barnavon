package chatsystem.exceptions;

public class PseudoRejectedException extends Exception{

    public PseudoRejectedException(String pseudo) {
        super("Pseudo Rejected : " + pseudo);
    }

}
