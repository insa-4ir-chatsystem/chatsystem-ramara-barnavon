package chatsystem.ContactDiscoveryLib;


/** This class handle different message received */
public class DatagramManager {

    /** INCO is the header to be handled here */
    public static Contact INCOToContact(String mess){

        String[] parties = mess.split(":");
        String pseudo = parties[1];
        int id = Integer.parseInt(parties[2]);

        return new Contact(pseudo, id);
    }

    /** DEID and REID are the header to be handled here */
    public static int XXIDToId(String mess) {
        String[] parties = mess.split(":");
        int id = Integer.parseInt(parties[1]);
        return id;

    }
    public static String XXPSToPseudo(String mess) {
        String[] parties = mess.split(":");
        String pseudo = parties[1];
        return pseudo;
    }
    /*public static int REID_to_id(String mess) {
        String[] parties = mess.split(":");
        int id = Integer.parseInt(parties[1]);
        return id;
    }*/



    public static HeaderDatagram getHeader(String mess){
        if (mess != null) {
            HeaderDatagram header = null;
            try {
                String[] parties = mess.split(":");
                header = HeaderDatagram.valueOf(parties[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid header of message : " + mess);

            }
            return header;
        } else {
            return HeaderDatagram.NULL;
        }
    }

}
