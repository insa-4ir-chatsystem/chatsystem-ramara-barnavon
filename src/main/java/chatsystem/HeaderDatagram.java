package chatsystem;

public enum HeaderDatagram {
    INCO, //info contact
    DECO //demande contact


    ;

    public static HeaderDatagram getHeader(String mess){
        HeaderDatagram header = null;
        try {
            header = HeaderDatagram.valueOf(mess.substring(0, 4));
        }catch (IllegalArgumentException e) {
            System.out.println("Invalid header of message : "+mess);

        }
        return header;
    }
}
