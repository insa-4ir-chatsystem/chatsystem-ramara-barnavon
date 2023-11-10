package chatsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

//Nos Headers sont sur 4 caracs


public class ChatSystem { //instance de chat sur une machine
    ContactsManager cm;
    Contact monContact;
    DatagramSocket socketBroadcast;
    private DataOutputStream out;
    private DataInputStream in;
    private InetAddress address;
    private int port;

    public void initSocket_Broadcast() {
    }

    public void initSocket(String addr, int port){
        try {
            this.socketBroadcast = new DatagramSocket(port);
            this.address = InetAddress.getByName(addr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ChatSystem(Contact contact, String ip, int port){
        this.monContact = contact; //TODO à enlever pour verif unicité des pseudos
        this.port = port;
        this.cm = new ContactsManager();
        initSocket(ip, port);
    }

    public void demande_liste_contact (int port_dest) {//on test en localhost, donc on passe le port en arg
        byte[] buf = new byte[256];
        String msg_to_send = "DECO";
        buf = msg_to_send.getBytes();

        DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, port_dest);
        try {
            this.socketBroadcast.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void send_contact(DatagramPacket rep) {

        byte[] buf = new byte[256];
        String msg_to_send = "INCO" + ":" + monContact.getPseudo() + ":" + monContact.getId();
        buf = msg_to_send.getBytes();
        rep.setData(buf);
        //DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, this.port);
        try {
            this.socketBroadcast.send(rep);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public DatagramPacket listen(){
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socketBroadcast.receive(packet);
            //System.out.println("j'ai receive un paquet");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return packet;
    }

    public void startListening(){
        ListeningThread LT = new ListeningThread();
        LT.start();
    }
    //TODO:if mess="recup_liste" then send to sender Contact ( mise en forme string - à parse du coter server - )
    public class ListeningThread extends Thread {
        HeaderDatagram header ;
        @Override
        public void run() {
            DatagramPacket rep;
            //System.out.println("je run un Thread de listen");
            while(true){
                //System.out.println("debut while");
                rep = listen(); //réécrire la rep et la renvoyer
                //System.out.println("pdu reçu");
                String msg = new String(rep.getData(), 0, rep.getLength());
                switch(DatagramManager.getHeader(msg)){
                    case INCO: // On reçoit une info de contact, il faut l'ajouter à notre liste de contacts

                        cm.addContact(DatagramManager.INCO_to_Contact(msg));
                        //System.out.println("Envoie Contact");

                        break;
                    case DECO: //On reçoit une demande de contact, on souhaite renvoyer notre contact au destinaire
                        //System.out.println("j'ai reçu DECO");
                        send_contact(rep);
                        //System.out.println("j'ai envoyé INCO");
                        break;
                    default:
                        //System.out.println("Header inconnu");







                }

            }
        }
    }

}
