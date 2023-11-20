package chatsystem.ContactDiscoveryLib;

public class DatagramManager {


    public static Contact INCO_to_Contact(String mess){

        String[] parties = mess.split(":");
        String pseudo = parties[1];
        int id = Integer.parseInt(parties[2]);

        return new Contact(pseudo, id);
    }
    public static int XXID_to_id(String mess) {
        String[] parties = mess.split(":");
        int id = Integer.parseInt(parties[1]);
        return id;

    }
    public static String XXPS_to_pseudo(String mess) {
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
