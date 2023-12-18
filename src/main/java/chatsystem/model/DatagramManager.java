package chatsystem.model;


import chatsystem.ContactDiscoveryLib.Contact;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/** This class handle different message received */
public class DatagramManager {
    private static final Logger LOGGER = LogManager.getLogger(DatagramManager.class);
    /** INCO is the header to be handled here */
    public static Contact INCOToContact(String mess){

        String[] parties = mess.split(":");
        String pseudo = parties[1];
        int id = Integer.parseInt(parties[2]);
        Contact contact = new Contact(pseudo,id);
        LOGGER.debug("INCO de " + contact + "envoyé");
        return contact;
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
    public static Integer XXPSToID(String mess) {
        String[] parties = mess.split(":");
        Integer id = Integer.valueOf(parties[3]);
        return id;
    }
    /*public static int REID_to_id(String mess) {
        String[] parties = mess.split(":");
        int id = Integer.parseInt(parties[1]);
        return id;
    }*/

    /** Gets the origin port of the incoming message */

    public static int getPort(String mess){
        String[] parties = mess.split(":");
        String port = parties[parties.length-1];
        return Integer.parseInt(port);
    }



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
