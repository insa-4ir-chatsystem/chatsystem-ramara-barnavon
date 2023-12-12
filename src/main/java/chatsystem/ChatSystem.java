package chatsystem;

import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.ContactDiscoveryLib.ContactsManager;
import chatsystem.ContactDiscoveryLib.DatagramManager;
import chatsystem.network.UDP_Client;
import chatsystem.network.UDP_Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

//Nos Headers sont sur 4 caracs
//NB on aurait pu optimiser et ne pas utiliser de ID vu que le pseudo est unique ( du coup dans notre cas on utilise les deux pour l'instant )

/** This class contain every action that a user can do */ //TODO:j'ai supposé qu'on avait enlevé d'ici : server side, client side, et handle des mess
public class ChatSystem { //instance de chat sur une machine


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


    /** Constructor */
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
        LOGGER.info("Chatsystem Created");
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

    /** Starts an instance of ChatSystem */

    public void start(String pseudoAsked){
        LOGGER.info("Lancement du chatsystem : " + pseudoAsked);
        startServer();

        udpServer.addObserver(received -> {
            try {

                if (received != null) {
                    String msg = received.content();

                    switch (DatagramManager.getHeader(msg)) {
                        case INCO: // On reçoit une info de contact, il faut l'ajouter à notre liste de contacts
                            cm.updateContact(DatagramManager.INCOToContact(msg));
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
                            String his_pseudo = DatagramManager.XXPSToPseudo(msg);

                            if (cm.searchContactByPseudo(his_pseudo) != null || (monContact != null && his_pseudo == monContact.getPseudo())) { //Si le pseudo existe déjà
                                UDP_Client.send_REPS(received.origin(), port);
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )

                            break;

                        case DEID: //si l'ID existe il refuse avec un id_conseillé ( le max + 1 de sa liste ) si l'ID n'existe pas, il renvoie rien
                            int his_id = DatagramManager.XXIDToId(msg);
                            if (his_id <= cm.getIdMax()) { //Si l'id existe déjà
                                UDP_Client.send_REID(received.origin(), port, his_id + 1); //on renvoie refus et on donne l'id conseillé
                                cm.setIdMax(his_id + 1);
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )
                            break;

                        case REID: //Si on reçoit un refus alors notre ID n'est pas accepté et c'est la fonction start() qui relancera une requete si besoin
                            int suggest_id = DatagramManager.XXIDToId(msg);
                            IDAccepted = false;
                            cm.setIdMax(suggest_id);
                            break;

                        case NULL:
                            break; //On skip le paquet

                        default:
                            LOGGER.error("Header inconnu : " + msg);

                    }

                }
            }catch (IOException e){
                LOGGER.error(e.getMessage());
            }
        });

        //udpServer.addObserver(received -> LOGGER.info("Received from udpServer: " + received));

        startUpdateContacts();
        int id = chooseID();
        this.cm.setIdMax(id);//probleme avec idMax, ça lui répond 3 alors que son camarade à 4
        while(id == -1){
            ;
        }
        String pseudo = choosePseudo(pseudoAsked);
        if (pseudoAccepted) {
            this.monContact = new Contact(pseudo, id);
            UCT.setName("UCT Thread - " + this.monContact.getPseudo());
            cm.setMonContact(this.monContact);
            LOGGER.debug("Chatsystem " + monContact.getPseudo() + " started correctly");
        }else{
            closeChat();
            LOGGER.debug("Chatsystem not started");
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
        LOGGER.trace("Local UDP server initialized");
    }
    private int chooseID(){
        IDAccepted = false;
        int ite = 0;
        int id = -1;
        while (!IDAccepted && ite < 20) {
            ite++;
            IDAccepted = true;//Celui qui lui dit refus va être quelqu'un qui est plus haut donc il est censé lui renvoyer un idmax encore + haut
            if(ite == 20) IDAccepted = false;
            id = cm.getIdMax();
            cm.setIdMax(id);
            for (int p : Main.portList) { // TODO: Changer ce mécanisme dégueulasse quand on passera au cas réel sur des IPs
                if (p == this.port) continue;
                try { // TODO: good idea try catch here ?
                    UDP_Client.send_DEID(broadcastAddress, p, id);
                    LOGGER.info("Je suis "+ Thread.currentThread().getName() + " et je demande mon id = " + id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(IDAccepted){
            LOGGER.info("Id accepté et unique : " + id + " =================================");
            return id;
        }else { return -1; }
        
    }



    private String choosePseudo(String pseudo){
        pseudoAccepted = true;
        for(int p : Main.portList){ // TODO: Changer ce mécanisme dégueulasse quand on passera au cas réel sur des IPs
            if(p == this.port) continue;
            try {// TODO: good idea try catch here ?
                UDP_Client.send_DEPS(broadcastAddress, p, pseudo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try { // wait for others to respond
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!pseudoAccepted){
            LOGGER.error("Pseudo " + pseudo + " non disponible, fermeture du chat ----------------------- ");
            closeChat(); // Fermeture du chat, solution temporaire
        } else {
            LOGGER.info("Pseudo accepté et unique: " + pseudo +"------------------------------------------");
        }

        return pseudo;
    }
    private String changePseudo(String pseudo){
        pseudoAccepted = true;
        for(int p : Main.portList){
            if(p == this.port) continue;
            try {
                UDP_Client.send_DEPS(broadcastAddress, p, pseudo);
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!pseudoAccepted){
            LOGGER.error("Pseudo " + pseudo + " non disponible, fermeture du chat ----------------------- ");
            closeChat(); // Fermeture du chat, solution temporaire
        } else {
            LOGGER.info("Pseudo accepté et unique: " + pseudo +"------------------------------------------");
        }

        return pseudo;
    }









    public void startServer(){
        LOGGER.trace("Lancement du Serveur UDP");
        udpServer.start();
    }

    public void stopServer(){
        udpServer.close();
    }

    public void startUpdateContacts(){
        LOGGER.trace("Lancement du Thread d'update des contacts");
        this.UCT = new UpdateContactsThread();
        UCT.start();
        UCT.setName("UCT Thread - " + this.monContact.getPseudo());
    }

    public void afficherListeContacts(){
        LOGGER.info("Je suis " + monContact.getPseudo() + " et ma liste de contact est :");
        //System.out.println("[");
        this.cm.afficherListe();
        //System.out.println("]");
    }

    public void closeChat(){
        LOGGER.trace("Tentative d'interruption du thread de listening de " + monContact.getPseudo());
        stopServer();
        if (UCT != null){
            LOGGER.trace("Tentative d'interruption du Thread d'update de " + monContact.getPseudo());
            UCT.interrupt();
        }
    }

    public boolean isOpen(){
        return udpServer.isAlive();
    }


    public class UpdateContactsThread extends Thread {
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                for (int p : Main.portList) { // TODO: Changer ce mécanisme déguelasse quand on passera au cas réel sur un intranet
                    if (p == port) continue;
                    try {// TODO: good idea try catch here ?
                        UDP_Client.send_DECO(broadcastAddress, p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
                //afficherListeContacts();
            }
            LOGGER.trace("[IMPORTANT] Confirmation de l'arrêt du Thread d'Update de " + monContact.getPseudo());
        }
    }

}
