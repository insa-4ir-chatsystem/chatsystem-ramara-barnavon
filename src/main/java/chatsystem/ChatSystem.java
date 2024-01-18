package chatsystem;

import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.ContactDiscoveryLib.ContactsManager;
import chatsystem.database.ChatHistoryManager;
import chatsystem.exceptions.PseudoRejectedException;
import chatsystem.model.DatagramManager;
import chatsystem.network.TCP_Server;
import chatsystem.network.UDP_Client;
import chatsystem.network.UDP_Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.Enumeration;

//Nos Headers sont sur 4 caracs
//NB on aurait pu optimiser et ne pas utiliser de ID vu que le pseudo est unique ( du coup dans notre cas on utilise les deux pour l'instant )

/** This class contain every action that a user can do */ //TODO:j'ai supposé qu'on avait enlevé d'ici : server side, client side, et handle des mess
public class ChatSystem { //instance de chat sur une machine


    private ContactsManager cm;
    private ChatHistoryManager chatHistoryManager;
    private Contact monContact;
    private UpdateContactsThread UCT;
    private int portUDP;
    private int portTCPServeur;
    private boolean pseudoAccepted;
    private boolean cgmPseudoAccepted;
    private boolean IDAccepted;
    private UDP_Server udpServer;
    private UDP_Server udpBroadcastServer;
    private TCP_Server tcpServer;
    private InetAddress broadcastAddress;
    private InetAddress ip;

    private static final Logger LOGGER = LogManager.getLogger(ChatSystem.class);
    private final int PORT_UDP = 42069; // arbitrary ports to use on every machine
    public final int PORT_TCP_SERVEUR = 42070;


    //TODO : Voir TODO main :)

    /** Constructor */
    public ChatSystem(){ //TODO: int portTCP;
        this.cm = new ContactsManager();
        this.chatHistoryManager = new ChatHistoryManager();
        this.monContact = new Contact();
        cm.setMonContact(this.monContact);
        this.portUDP = PORT_UDP;
        this.portTCPServeur = PORT_TCP_SERVEUR;
        try {
            setAddresses();
            LOGGER.info("Adresse IP locale : " + this.ip);
            initServerUDP(this.portUDP);
            initServerTCP(this.portTCPServeur);
            //initServerTCP(port);
        } catch (Exception e) { // impossible to recover from this exception
            LOGGER.error("Unable to create UDP_Server with ip: " + ip + " and port: " + portUDP + "(" + e + ")");
            System.exit(1);
        }
        LOGGER.info("Chatsystem Created");
    }



    /** Getters and setters */
    public Contact getMonContact() {
        return monContact;
    }

    public int getPort() {
        return portUDP;
    }


    public ContactsManager getCm() {
        return cm;
    }

    public ChatHistoryManager getChatHistoryManager() {
        return chatHistoryManager;
    }

    public TCP_Server getTcpServer() {
        return this.tcpServer;
    }
    /** gets the broadcast address of the first network interface and assigns it to the attribute */
    public void setAddresses() throws SocketException{
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Vérifie si l'interface n'est pas de type bouclage et si elle est activée
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();

                        // Vérifie si c'est une adresse IPv4 et non une adresse loopback
                        if (inetAddress instanceof java.net.Inet4Address && !inetAddress.isLoopbackAddress()) {
                            System.out.println("Adresse IPv4 de la première interface réseau : " + inetAddress.getHostAddress());

                            // Obtient l'adresse de broadcast associée à l'interface
                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                                this.ip = interfaceAddress.getAddress();
                                this.broadcastAddress = interfaceAddress.getBroadcast();
                                InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                                if (broadcastAddress != null) {
                                    System.out.println("Adresse de broadcast : " + broadcastAddress.getHostAddress());
                                }
                            }

                            return; // Arrête l'exécution après avoir trouvé la première adresse IPv4
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setMonContact(String pseudo, int id) {
        this.monContact.setPseudo(pseudo);
        this.monContact.setId(id);
    }

    /** Other Methods */

    /** Starts an instance of ChatSystem */

    public synchronized void start(){
        /** =================================UDP============================================= */
        startServerUDP();



        udpServer.addObserver((received, port) -> {
            try {

                if (received != null) {
                    String msg = received.content();
                    InetAddress origin = received.origin();

                    switch (DatagramManager.getHeader(msg)) { //TODO : y'a plein de nom sos forme "nom_de_X" à mettre en "nomDeX"
                        case INCO: // On reçoit une info de contact, il faut l'ajouter à notre liste de contacts
                            cm.updateContact(DatagramManager.INCOToContact(msg, origin.getHostAddress()));
                            break;

                        case DECO: //On reçoit une demande de contact, on souhaite renvoyer notre contact au destinaire
                            if ((monContact != null && (monContact.getId() != -1))) {
                                UDP_Client.send_INCO(received.origin(), DatagramManager.getPort(msg), monContact);
                            }
                            break;

                        case REPS: // Si le pseudo demandé est refusé par un contact
                            pseudoAccepted = false;
                            break;

                        case DEPS: //
                            String his_pseudo = DatagramManager.XXPSToPseudo(msg);
                            LOGGER.debug("Mon pseudo est : " + this.monContact.getPseudo() + " pseudo demandé : " + his_pseudo);
                            if (cm.searchContactByPseudo(his_pseudo) != null || (this.monContact != null && his_pseudo.equals(monContact.getPseudo()))) { //Si le pseudo existe déjà
                                UDP_Client.send_REPS(received.origin(), DatagramManager.getPort(msg));
                                LOGGER.debug("Mon pseudo est : " + this.monContact.getPseudo() + " Refus du pseudo " + his_pseudo);
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )

                            break;

                        case DEID: //si l'ID existe il refuse avec un id_conseillé ( le max + 1 de sa liste ) si l'ID n'existe pas, il renvoie rien
                            int his_id = DatagramManager.XXIDToId(msg);
                            if (his_id <= cm.getIdMax()) { //Si l'id existe déjà
                                UDP_Client.send_REID(received.origin(), DatagramManager.getPort(msg), his_id + 1); //on renvoie refus et on donne l'id conseillé
                                //cm.setIdMax(his_id + 1);
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )
                            break;

                        case REID: //Si on reçoit un refus alors notre ID n'est pas accepté et c'est la fonction start() qui relancera une requete si besoin
                            int suggest_id = DatagramManager.XXIDToId(msg);
                            IDAccepted = false;
                            cm.setIdMax(suggest_id);
                            break;
                        case CHPS: //
                            String hisNewPseudo = DatagramManager.XXPSToPseudo(msg);
                            Integer hisOldID = DatagramManager.XXPSToID(msg);
                            if (cm.searchContactByPseudo(hisNewPseudo) != null || (monContact != null && hisNewPseudo.equals(monContact.getPseudo()))) { //Si le pseudo existe déjà
                                UDP_Client.send_RECH(received.origin(), DatagramManager.getPort(msg));
                                /** ( l'emetteur supposera que c'est bon tant que personne lui refuse son nouveau pseudo )*/
                            }

                            break;
                        case RECH: // Si le pseudo demandé est refusé par un contact
                            cgmPseudoAccepted = false;
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
        /** =================================TCP============================================= */
        //TODO: REGROUPER tous les méthodes UDPs ensemble, et toute les mé
        startServerTCP();
        tcpServer.addObserver((received, ipSender) -> {
            int idSender = cm.searchContactByIP(ipSender.getHostAddress()).getId();
            try {
                LOGGER.debug("J'insère le message"+  received  + "dans la DB");
                chatHistoryManager.insertMessage(idSender,this.monContact.getId(),received);
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }

        });




        startUpdateContacts();
        int id = chooseID();
        this.cm.setIdMax(id);//probleme avec idMax, ça lui répond 3 alors que son camarade à 4
        while(id == -1){
            ;
        }
        monContact.setId(id);

    }


    public void initServerUDP(int port) throws SocketException, UnknownHostException {
        this.udpServer = new UDP_Server(port, this.ip);
        LOGGER.trace("Local UDP server initialized on port " + port);
    }
    public void initServerTCP(int port) throws SocketException, UnknownHostException, IOException {
        this.tcpServer = new TCP_Server(port);
        LOGGER.trace("Local TCP server initialized");
    }
    private int chooseID(){
        IDAccepted = false;
        int ite = 0;
        int id = -1;
        while (!IDAccepted && ite < 20) {
            ite++;
            IDAccepted = true;
            if(ite == 20) IDAccepted = false;
            id = cm.getIdMax();
            cm.setIdMax(id);

                try { // TODO: good idea try catch here ?
                    UDP_Client.send_DEID(broadcastAddress,  PORT_UDP, id);
                    LOGGER.debug(" broadcast address : " + broadcastAddress);
                    LOGGER.debug(" local address : " + this.ip);
                    LOGGER.info("Je suis "+ Thread.currentThread().getName() + " et je demande mon id = " + id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(IDAccepted){
            LOGGER.info("Id accepté et unique : " + id );
            return id;
        }else { return -1; } //TODO: mettre une exception ici
        
    }

    public synchronized void choosePseudo (String pseudo) throws PseudoRejectedException {
        pseudoAccepted = true;
       /** for(int p : Main.portList){ // TODO: Changer ce mécanisme dégueulasse quand on passera au cas réel sur des IPs
            if(p == this.portUDP) continue;*/
            try {// TODO: SE POSER LA QUESTION DE SI UNE EXCEPTION DOIT INTERROMPRE NOTRE PROGRAMME
                UDP_Client.send_DEPS(broadcastAddress,  PORT_UDP, pseudo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        try { // wait for others to respond
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!pseudoAccepted) {
            throw new PseudoRejectedException(pseudo);
        }
        monContact.setPseudo(pseudo);

    }
    public synchronized void changePseudo(String pseudo) throws PseudoRejectedException {
        cgmPseudoAccepted = true;

            try {
                UDP_Client.send_CHPS(broadcastAddress,  PORT_UDP, pseudo, this.getMonContact().getId());
            }catch (IOException e) {
                throw new RuntimeException(e);
            }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!cgmPseudoAccepted){
            LOGGER.info("Pseudo " + pseudo + " non disponible, veuillez reesayer avec un pseudo différent");
            throw new PseudoRejectedException(pseudo);
        } else {
            this.monContact.setPseudo(pseudo);
            UCT.setName("UCT Thread - " + this.monContact.getPseudo());
            cm.setMonContact(this.monContact);
            LOGGER.debug("Chatsystem " + monContact.getPseudo() + " changed correctly");
            LOGGER.info("Pseudo accepté et unique: " + pseudo);
        }


    }
    /**public synchronized void start*/




    public void startServerUDP(){
        LOGGER.trace("Lancement du Serveur UDP");
        udpServer.start();
    }
    public void startServerTCP(){
        LOGGER.trace("Lancement du Serveur TCP");
        tcpServer.start();
    }

    public void stopServer() throws IOException{

        udpServer.close();
        /**tcpServer.close();*/
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

    public void closeChat() throws IOException{
        LOGGER.trace("Tentative d'interruption du serveur UDP :  " + monContact.getPseudo());
        stopServer();
        if (UCT != null){
            LOGGER.trace("Tentative d'interruption du Thread d'update de " + monContact.getPseudo());
            UCT.interrupt();
        }
    }

    public boolean isStarted(){
        return udpServer.isAlive();
    }

    public UpdateContactsThread getUCT() {
        return UCT;
    }


    public class UpdateContactsThread extends Thread {
        @Override
        public void run() {
            while (!this.isInterrupted()) {

                    try {// TODO: good idea try catch here ?
                        UDP_Client.send_DECO(broadcastAddress, PORT_UDP);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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
