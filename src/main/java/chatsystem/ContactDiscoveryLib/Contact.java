package chatsystem.ContactDiscoveryLib;

import chatsystem.network.TCP_Client;

import java.io.IOException;
import java.net.InetAddress;

/** this class contain the identity of a User in the network */
public class Contact {
    private String pseudo;
    private String ip;
    private int id;
    private TCP_Client tcp_client;
    /** time to live before removing from Contact list */
    private int TTL;
    private boolean online;
    public static final String NO_PSEUDO = "M.Anonyme (n'a pas encore obtenu de pseudo)";

    /** Constructors */
    public Contact() {
        this.id = -1;
        this.pseudo = NO_PSEUDO;
        this.online = false;
    }
    public Contact(String pseudo, String ip, int id){
        this.pseudo = pseudo;
        this.ip = ip;
        this.id = id;
    }

    public Contact(String pseudo, int id, int TTL, boolean online){
        this.id = id;
        this.pseudo = pseudo;
        this.TTL = TTL;
        this.online = online;
    }

    /** Getters and setters */

    public String getPseudo() {
        return this.pseudo;
    }
    public String getIp() {
        return ip;
    }

    public int getId() {
        return this.id;
    }


    public int getTTL(){
        return this.TTL;
    }
    public boolean isOnline(){
        return this.online;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setOnline(boolean online){
        this.online = online;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setId(int id) {
        this.id = id;
    }


    /** Methods */
    public void decrementTTL(){
        this.TTL--;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Contact autre = (Contact) obj;
        return autre.id == this.id;
    }

    public boolean samePseudo(Contact contact){
        return contact.getPseudo().equals(this.pseudo);
    }


    public void sendMessageTCP(String message, int port) throws IOException {
        if (tcp_client != null) {
            tcp_client.sendMessage(message);
        } else {
            tcp_client = new TCP_Client();
            tcp_client.startConnection(this.ip, port);
            tcp_client.sendMessage(message);

        }

    }


    @Override
    public String toString() {
        String Online;
        if(this.online){
            Online = "Online";
        }else{
            Online = "Offline";
        }
        return "Contact{" +
                "pseudo='" + pseudo + '\'' +
                ", id=" + id +
                ", ttl="+ TTL +
                "     " + Online +
                '}';
    }
}

