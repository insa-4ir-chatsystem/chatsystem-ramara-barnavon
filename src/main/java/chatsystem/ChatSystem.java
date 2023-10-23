package chatsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

//Nos Headers sont sur 4 caracs


public class ChatSystem { //instance de chat sur une machine
    ArrayList<Contact> ContactList;
    Contact monContact;
    DatagramSocket socketBroadcast;
    private DataOutputStream out;
    private DataInputStream in;
    private InetAddress address;
    private int port;


    public void initSocket(String addr){
        try {
            this.socketBroadcast = new DatagramSocket();
            this.address = InetAddress.getByName(addr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ChatSystem(Contact contact, String ip, int port){
        this.monContact = contact;
        initSocket(ip);
        this.port = port;
    }

    public void demande_liste_contact () {
        byte[] buf = new byte[256];
        String msg_to_send = "recup_liste";
        buf = msg_to_send.getBytes();

        DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, this.port);
        try {
            this.socketBroadcast.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void send_contact_broadcast() {

        byte[] buf = new byte[256];
        String msg_to_send = monContact.getPseudo() + ":" + monContact.getId();
        buf = msg_to_send.getBytes();
        DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, this.port);
        try {
            this.socketBroadcast.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public String listen(){
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        String received = "";
        try {
            socketBroadcast.receive(packet);
            received = new String(packet.getData(), 0, packet.getLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return received;
    }
    //TODO:if mess="recup_liste" then send to sender Contact ( mise en forme string - à parse du coter server - )
    public class ListeningThread extends Thread {
        HeaderDatagram header ;
        @Override
        public void run() {
            String rep;
            while(true){
                rep = listen();
                switch(HeaderDatagram.getHeader(rep)){
                    case INCO:
                        System.out.println("");//TODO
                        break;
                    case DECO:
                        System.out.println("");//TODO
                        break;
                    default:
                        System.out.println("Header inconnu");







                }
                if(rep.equals("recup_liste")){
                    send_contact_broadcast();
                } else if (rep.equals("")) {
                    ;
                }else{

                }
            }
        }
    }

}
