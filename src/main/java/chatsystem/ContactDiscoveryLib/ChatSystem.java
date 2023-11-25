package chatsystem.ContactDiscoveryLib;

import chatsystem.Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

import static java.lang.System.exit;

//Nos Headers sont sur 4 caracs
//NB on aurait pu optimiser et ne pas utiliser de ID vu que le pseudo est unique ( du coup dans notre cas on utilise les deux pour l'instant )


public class ChatSystem { //instance de chat sur une machine

    //Attribut
    private ContactsManager cm;
    private Contact monContact;
    private DatagramSocket socketBroadcast;
    private ListeningThread LT;
    private UpdateContactsThread UCT;
    private DataOutputStream out;
    private DataInputStream in;
    private InetAddress address;
    private int port;
    private boolean pseudoAccepted;
    private boolean IDAccepted;


    //Constructeur
    public ChatSystem(String ip, int port){
        this.cm = new ContactsManager();
        this.monContact = new Contact();
        cm.setMonContact(this.monContact);
        this.port = port;
        initSocket(ip, port);
    }
    //Methods

    //Getter and setter
    public Contact getMonContact() {
        return monContact;
    }
    public void setMonContact(String pseudo, int id) {
        this.monContact.setPseudo(pseudo);
        this.monContact.setId(id);
    }

    public int getPort() {
        return port;
    }

    public ContactsManager getCm() {
        return cm;
    }

    //OTHER methods

    public void start(String pseudoAsked){
        System.out.println("Lancement du chatsystem : " + pseudoAsked + "?");
        startListening();
        startUpdateContacts();
        int id = chooseID();
        this.cm.setIdMax(id);
        String pseudo = choosePseudo(pseudoAsked);
        if (pseudoAccepted) {
            this.monContact = new Contact(pseudo, id);
            LT.setName("LT Thread - " + this.monContact.getPseudo());
            UCT.setName("UCT Thread - " + this.monContact.getPseudo());
            cm.setMonContact(this.monContact);
        }
        //System.out.println("Creation contact : " + this.monContact);
        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */

    }
    public void initSocket(String addr, int port){
        try {
            this.socketBroadcast = new DatagramSocket(port);
            this.address = InetAddress.getByName(addr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private int chooseID(){
        IDAccepted = false;
        int ite = 0;
        int id = -1;
        while (!IDAccepted && ite < 10) {
            ite++;
            id = cm.getIdMax() + 1;
            cm.setIdMax(id);
            IDAccepted = true;
            for (int p : Main.portList) { // TODO: Changer ce mécanisme dégueulasse quand on passera au cas réel sur des IPs
                if (p == this.port) continue;
                send_DEID(p, id);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(IDAccepted){
            System.out.println("Id accepté et unique : " + id);
            return id;
        }else { return -1; }
        
    }



    private String choosePseudo(String pseudo){
        pseudoAccepted = true;
        for(int p : Main.portList){ // TODO: Changer ce mécanisme dégueulasse quand on passera au cas réel sur des IPs
            send_DEPS(p, pseudo);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!pseudoAccepted){
            System.out.println("Pseudo " + pseudo + " non disponible, fermeture du chat");
            closeChat(); // Fermeture du chat, solution temporaire
        } else {
            System.out.println("Pseudo accepté et unique: " + pseudo);
        }

        return pseudo;
    }





    public void send_INCO(DatagramPacket rep) {

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

    public void send_DECO(int port_dest) {//on test en localhost, donc on passe le port en arg
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

    public void send_DEPS(int port_dest, String pseudo){
        byte[] buf = new byte[256];
        String msg_to_send = "DEPS" + ":" + pseudo;
        buf = msg_to_send.getBytes();

        DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, port_dest);
        try {
            this.socketBroadcast.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send_REPS(DatagramPacket rep) {

            byte[] buf = new byte[256];
            String msg_to_send = "REPS";
            buf = msg_to_send.getBytes();
            rep.setData(buf);
            //DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, this.port);
            try {
                this.socketBroadcast.send(rep);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public void send_DEID(int port_dest, int id_voulu){
        byte[] buf = new byte[256];
        String msg_to_send = "DEID"+ ":" + id_voulu;
        buf = msg_to_send.getBytes();

        DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, port_dest);
        try {
            this.socketBroadcast.send(outPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void send_REID(DatagramPacket rep,int id) {

        byte[] buf = new byte[256];
        String msg_to_send = "REID" + ":" + id;
        buf = msg_to_send.getBytes();
        rep.setData(buf);
        DatagramPacket outPacket = new DatagramPacket(buf, buf.length, this.address, this.port);
        try {
            this.socketBroadcast.send(rep);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DatagramPacket listen(){
        byte[] buf = new byte[256];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        //System.out.println(new String(packet.getData(), 0, packet.getLength()));
        try {
            socketBroadcast.receive(packet);
            //System.out.println("j'ai receive un paquet");
        } catch (IOException e) {

            System.out.println("Socket fermé pendant une opération de listen (pas grave)");
            return null; //Bug étrange lorsqu'il y a l'exception au mauvais moment le packet se corromp
        }
        return packet;
    }

    public void startListening(){
        System.out.println("Lancement du Thread de listening");
        this.LT = new ListeningThread();
        LT.start();
        LT.setName("LT Thread - " + this.monContact.getPseudo());
    }

    public void startUpdateContacts(){
        System.out.println("Lancement du Thread d'update des contacts");
        this.UCT = new UpdateContactsThread();
        UCT.start();
        UCT.setName("UCT Thread - " + this.monContact.getPseudo());
    }

    public void afficherListeContacts(){
        System.out.println("Je suis " + monContact.getPseudo() + " et ma liste de contact est :");
        //System.out.println("[");
        this.cm.afficherListe();
        //System.out.println("]");
    }

    public void closeChat(){
        System.out.println("Tentative d'interruption du thread de listening de " + monContact.getPseudo());
        LT.interrupt();
        System.out.println("Tentative d'interruption du Thread d'update de " + monContact.getPseudo());
        UCT.interrupt();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.socketBroadcast.close();
        //System.out.println("le socket a été fermé");

        //System.exit(1);
    }


    public class ListeningThread extends Thread {
        HeaderDatagram header ;
        @Override
        public void run() {
            DatagramPacket rep;
            //System.out.println("je run un Thread de listen");
            while (!this.isInterrupted()) {
                //System.out.println("debut while");
                rep = listen(); //réécrire la rep et la renvoyer
                //System.out.println("pdu reçu");
                if (rep != null) {
                    String msg = new String(rep.getData(), 0, rep.getLength());

                    switch (DatagramManager.getHeader(msg)) {
                        case INCO: // On reçoit une info de contact, il faut l'ajouter à notre liste de contacts

                            cm.updateContact(DatagramManager.INCO_to_Contact(msg));
                            //afficherListeContacts();
                            //System.out.println("Envoie Contact");

                            break;
                        case DECO: //On reçoit une demande de contact, on souhaite renvoyer notre contact au destinaire
                            //System.out.println("j'ai reçu DECO");
                            if ((monContact != null && (monContact.getId() != -1))) {
                                send_INCO(rep);
                                //System.out.println("je suis " + monContact.getId() + " et j'envoie mon contact");
                            }
                            //System.out.println("j'ai envoyé INCO");
                            break;
                        case REPS: // Si le pseudo demandé est refusé par un contact
                            pseudoAccepted = false;
                            break;
                        case DEPS: //
                            String his_pseudo = DatagramManager.XXPS_to_pseudo(msg);
                            if (cm.search_contact_by_pseudo(his_pseudo) != null || (monContact != null && his_pseudo == monContact.getPseudo())) { //Si le pseudo existe déjà
                                send_REPS(rep);
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )

                            break;
                        case DEID: //si l'ID existe il refuse avec un id_conseiller ( le max + 1 de sa liste ) si l'ID n'existe pas, il renvoie rien
                            int his_id = DatagramManager.XXID_to_id(msg);
                            if (his_id <= cm.getIdMax()) { //Si l'id existe déjà
                                send_REID(rep, his_id + 1); //on renvoie refus et on donne l'id conseillé
                                cm.setIdMax(his_id + 1); //et on met à jour l'id_max
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )
                            break;

                        case REID: //Si on reçoit un refus alors notre ID n'est pas accepté et c'est la fonction start() qui relancera une requete si besoin
                            int suggest_id = DatagramManager.XXID_to_id(msg);
                            IDAccepted = false;
                            cm.setIdMax(suggest_id);
                            break;
                        case NULL:
                            break; //On skip le paquet
                        default:
                            System.out.println("Header inconnu : " + msg);

                    }

                }
            }
            System.out.println("[IMPORTANT] Confirmation de l'arrêt du Thread de listening de " + monContact.getPseudo());
        }
    }

    public class UpdateContactsThread extends Thread {
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                for (int p : Main.portList) { // TODO: Changer ce mécanisme déguelasse quand on passera au cas réel sur un intranet
                    if (p == port) continue;
                    send_DECO(p);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    this.interrupt();
                    //System.out.println("Confirmation de l'arrêt du Thread d'update de " + monContact.getPseudo());
                    //throw new RuntimeException(e);
                }
                //System.out.println("THREAD rly stopped ?");
                cm.decreaseTTL();
                afficherListeContacts();
            }
            System.out.println("[IMPORTANT] Confirmation de l'arrêt du Thread d'Update de " + monContact.getPseudo());
        }
    }

}
