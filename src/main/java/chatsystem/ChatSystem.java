package chatsystem;

import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.ContactDiscoveryLib.ContactsManager;
import chatsystem.ContactDiscoveryLib.DatagramManager;
import chatsystem.network.UDP_Client;
import chatsystem.network.UDP_Message;
import chatsystem.network.UDP_Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

//Nos Headers sont sur 4 caracs
//NB on aurait pu optimiser et ne pas utiliser de ID vu que le pseudo est unique ( du coup dans notre cas on utilise les deux pour l'instant )


public class ChatSystem { //instance de chat sur une machine

    //Attribut
    private ContactsManager cm;
    private Contact monContact;
    private UpdateContactsThread UCT;
    private DataOutputStream out;
    private DataInputStream in;
    private String ip;
    private int port;
    private boolean pseudoAccepted;
    private boolean IDAccepted;
    private UDP_Server udpServer;
    private InetAddress broadcastAddress;

    private static final Logger LOGGER = LogManager.getLogger(ChatSystem.class);


    //Constructeur
    public ChatSystem(String ip, int port){
        this.cm = new ContactsManager();
        this.monContact = new Contact();
        cm.setMonContact(this.monContact);
        this.port = port;
        this.ip = ip;
        try {
            this.broadcastAddress = InetAddress.getByName("localhost"); // TODO: Solution temporaire à changer
            initServer(ip, port);
        } catch (Exception e) { // impossible to recover from this exception
            LOGGER.error("Unable to create UDP_Server with ip: " + ip + " and port: " + port);
            System.exit(1);
        }
    }

    /** Getters and setters */
    public Contact getMonContact() {
        return monContact;
    }

    public int getPort() {
        return port;
    }

    public ContactsManager getCm() {
        return cm;
    }


    public void setMonContact(String pseudo, int id) {
        this.monContact.setPseudo(pseudo);
        this.monContact.setId(id);
    }

    /** Other Methods */

    public void start(String pseudoAsked){
        LOGGER.info("Lancement du chatsystem : " + pseudoAsked);
        startServer();

        udpServer.addObserver(new UDP_Server.Observer() {
            @Override
            public void handle(UDP_Message received) {
                try {

                    if (received != null) {
                        String msg = received.content();

                        switch (DatagramManager.getHeader(msg)) {
                            case INCO: // On reçoit une info de contact, il faut l'ajouter à notre liste de contacts
                                cm.updateContact(DatagramManager.INCO_to_Contact(msg));
                                break;

                            case DECO: //On reçoit une demande de contact, on souhaite renvoyer notre contact au destinaire
                                if ((monContact != null && (monContact.getId() != -1))) {
                                    UDP_Client.send_INCO(received.origin(), port, monContact);
                                }
                                break;

                            case REPS: // Si le pseudo demandé est refusé par un contact
                                pseudoAccepted = false;
                                break;

                            case DEPS: //
                                String his_pseudo = DatagramManager.XXPS_to_pseudo(msg);

                                if (cm.search_contact_by_pseudo(his_pseudo) != null || (monContact != null && his_pseudo == monContact.getPseudo())) { //Si le pseudo existe déjà
                                    UDP_Client.send_REPS(received.origin(), port);
                                } //else on fait rien il ( il supposera que oui tant que personne lui dit non )

                                break;

                            case DEID: //si l'ID existe il refuse avec un id_conseillé ( le max + 1 de sa liste ) si l'ID n'existe pas, il renvoie rien
                                int his_id = DatagramManager.XXID_to_id(msg);
                                if (his_id <= cm.getIdMax()) { //Si l'id existe déjà
                                    UDP_Client.send_REID(received.origin(), port, his_id + 1); //on renvoie refus et on donne l'id conseillé
                                    cm.setIdMax(his_id + 1);
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
                }catch (IOException e){
                    LOGGER.error(e.getMessage());
                }
            }
        });






        udpServer.addObserver(new UDP_Server.Observer() {
            @Override
            public void handle(UDP_Message received) {
                System.out.println("Received from udpServer: " + received);
            }
        });

        startUpdateContacts();
        int id = chooseID();
        this.cm.setIdMax(id);
        String pseudo = choosePseudo(pseudoAsked);
        if (pseudoAccepted) {
            this.monContact = new Contact(pseudo, id);
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
    public void initServer(String addr, int port) throws SocketException, UnknownHostException {
        this.udpServer = new UDP_Server(port, addr);
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
                UDP_Client.send_DEID(broadcastAddress, p, id);
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
            UDP_Client.send_DEPS(broadcastAddress, p, pseudo);
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









    public void startServer(){
        LOGGER.info("Lancement du Serveur UDP");
        udpServer.start();
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
        udpServer.close();
        System.out.println("Tentative d'interruption du Thread d'update de " + monContact.getPseudo());
        UCT.interrupt();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public class UpdateContactsThread extends Thread {
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                for (int p : Main.portList) { // TODO: Changer ce mécanisme déguelasse quand on passera au cas réel sur un intranet
                    if (p == port) continue;
                    UDP_Client.send_DECO(broadcastAddress, p);
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
