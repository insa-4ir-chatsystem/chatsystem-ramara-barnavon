package chatsystem;

public class DatagramManager {


    public static Contact INCO_to_Contact(String mess){

        String[] parties = mess.split(":");
        String pseudo = parties[1];
        int id = Integer.parseInt(parties[2]);

        return new Contact(pseudo, id);
    }




    public static HeaderDatagram getHeader(String mess){
        HeaderDatagram header = null;
        try {
            String[] parties = mess.split(":");
            header = HeaderDatagram.valueOf(parties[0]);
        }catch (IllegalArgumentException e) {
            System.out.println("Invalid header of message : " + mess);

        }
        return header;
    }

}
