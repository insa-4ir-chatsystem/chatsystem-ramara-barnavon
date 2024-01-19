package chatsystem;

import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.ContactDiscoveryLib.ContactsManager;
import chatsystem.ContactDiscoveryLib.UpdateContactListThread;
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

/** This class manages all the components required to create a chat system */
public class ChatSystem {

    /** ---------------------------------- Attributes ------------------------------------------- */
    private ContactsManager contactsManager;
    private ChatHistoryManager chatHistoryManager;
    private UpdateContactListThread updateContactListThread;
    private boolean pseudoAccepted;
    private boolean changePseudoAccepted;
    private boolean IDAccepted;
    private UDP_Server udpServer;
    private TCP_Server tcpServer;
    private InetAddress broadcastAddress;
    private InetAddress ip;


    private static final Logger LOGGER = LogManager.getLogger(ChatSystem.class);
    public static final int PORT_UDP = 42069; // arbitrary ports to use on every machine
    public static final int PORT_TCP = 42070;


    /** -------------------------------------------- Methods ---------------------------------------- */

    /** Constructor */
    public ChatSystem(){
        this.contactsManager = new ContactsManager();
        this.chatHistoryManager = new ChatHistoryManager();
        try {
            setAddresses();
            LOGGER.info("Adresse IP locale : " + this.ip);
            initServerUDP(PORT_UDP);
            initServerTCP(PORT_TCP);
            //initServerTCP(port);
        } catch (Exception e) { // impossible to recover from this exception
            LOGGER.error("Unable to create UDP_Server with ip: " + ip + " and port: " + PORT_UDP + "(" + e + ")");
            System.exit(1);
        }
        LOGGER.info("Chatsystem Created");
    }



    /** Getters/Setters */
    public Contact getMonContact() {
        return contactsManager.getMonContact();
    }

    public ContactsManager getContactsManager() {
        return contactsManager;
    }

    public ChatHistoryManager getChatHistoryManager() {
        return chatHistoryManager;
    }

    public TCP_Server getTcpServer() {
        return this.tcpServer;
    }

    public void setMonContact(String pseudo, int id) {
        this.getMonContact().setPseudo(pseudo);
        this.getMonContact().setId(id);
    }

    /** sets the broadcast and IP address of the first network interface and assigns it to the attribute */
    public void setAddresses(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Choose an active interface which is not the loopback
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();

                        // Checks if address is IPv4 and not loopback
                        if (inetAddress instanceof java.net.Inet4Address && !inetAddress.isLoopbackAddress()) {
                            System.out.println("Adresse IPv4 de la première interface réseau : " + inetAddress.getHostAddress());

                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                                this.ip = interfaceAddress.getAddress();
                                this.broadcastAddress = interfaceAddress.getBroadcast();
                                InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                                if (broadcastAddress != null) {
                                    System.out.println("Adresse de broadcast : " + broadcastAddress.getHostAddress());
                                }
                            }

                            return; //returns first address found
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /** Other Methods */

    /** Starts an instance of ChatSystem */

    public synchronized void start(){
        /** ================================ TCP ============================================ */

        startServerTCP();

        /** ================================= DataBase ====================================== */

        try {
            chatHistoryManager.deleteChatHistoryTable();
            chatHistoryManager.createChatHistoryTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /** ================================ UDP ============================================ */

        startServerUDP();
        addUdpServerObserver();
        startUpdateContacts();

        int id = chooseID();
        this.contactsManager.setIdMax(id);
        while(id == -1){ // waits for the ID to be accepted by others
            ;
        }
        getMonContact().setId(id);

    }

    private void addUdpServerObserver(){
        udpServer.addObserver((received, port) -> {
            try {

                if (received != null) {
                    String msg = received.content();
                    InetAddress origin = received.origin();

                    switch (DatagramManager.getHeader(msg)) { //TODO : y'a plein de nom sos forme "nom_de_X" à mettre en "nomDeX"
                        case INCO: // On reçoit une info de contact, il faut l'ajouter à notre liste de contacts
                            contactsManager.updateContact(DatagramManager.INCOToContact(msg, origin.getHostAddress()));
                            break;

                        case DECO: //On reçoit une demande de contact, on souhaite renvoyer notre contact au destinaire
                            if (getMonContact() != null && (getMonContact().getId() != -1)) {
                                UDP_Client.send_INCO(received.origin(), DatagramManager.getPort(msg), getMonContact());
                            }
                            break;

                        case REPS: // Si le pseudo demandé est refusé par un contact
                            pseudoAccepted = false;
                            break;

                        case DEPS: //
                            String his_pseudo = DatagramManager.XXPSToPseudo(msg);
                            LOGGER.debug("Mon pseudo est : " + this.getMonContact().getPseudo() + " pseudo demandé : " + his_pseudo);
                            if (contactsManager.searchContactByPseudo(his_pseudo) != null || (this.getMonContact() != null && his_pseudo.equals(getMonContact().getPseudo()))) { //Si le pseudo existe déjà
                                UDP_Client.send_REPS(received.origin(), DatagramManager.getPort(msg));
                                LOGGER.debug("Mon pseudo est : " + this.getMonContact().getPseudo() + " Refus du pseudo " + his_pseudo);
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )

                            break;

                        case DEID: //si l'ID existe il refuse avec un id_conseillé ( le max + 1 de sa liste ) si l'ID n'existe pas, il renvoie rien
                            int his_id = DatagramManager.XXIDToId(msg);
                            if (his_id <= contactsManager.getIdMax()) { //Si l'id existe déjà
                                UDP_Client.send_REID(received.origin(), DatagramManager.getPort(msg), his_id + 1); //on renvoie refus et on donne l'id conseillé
                                //cm.setIdMax(his_id + 1);
                            } //else on fait rien il ( il supposera que oui tant que personne lui dit non )
                            break;

                        case REID: //Si on reçoit un refus alors notre ID n'est pas accepté et c'est la fonction start() qui relancera une requete si besoin
                            int suggest_id = DatagramManager.XXIDToId(msg);
                            IDAccepted = false;
                            contactsManager.setIdMax(suggest_id);
                            break;
                        case CHPS: //
                            String hisNewPseudo = DatagramManager.XXPSToPseudo(msg);
                            Integer hisOldID = DatagramManager.XXPSToID(msg);
                            if (contactsManager.searchContactByPseudo(hisNewPseudo) != null || (getMonContact() != null && hisNewPseudo.equals(getMonContact().getPseudo()))) { //Si le pseudo existe déjà
                                UDP_Client.send_RECH(received.origin(), DatagramManager.getPort(msg));
                                /** ( l'emetteur supposera que c'est bon tant que personne lui refuse son nouveau pseudo )*/
                            }

                            break;
                        case RECH: // Si le pseudo demandé est refusé par un contact
                            changePseudoAccepted = false;
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
            id = contactsManager.getIdMax();
            contactsManager.setIdMax(id);

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
        getMonContact().setPseudo(pseudo);

    }
    public synchronized void changePseudo(String pseudo) throws PseudoRejectedException {
        changePseudoAccepted = true;

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
        if(!changePseudoAccepted){
            LOGGER.info("Pseudo " + pseudo + " non disponible, veuillez reesayer avec un pseudo différent");
            throw new PseudoRejectedException(pseudo);
        } else {
            this.getMonContact().setPseudo(pseudo);
            updateContactListThread.setName("UCT Thread - " + this.getMonContact().getPseudo());
            contactsManager.setMonContact(this.getMonContact());
            LOGGER.debug("Chatsystem " + getMonContact().getPseudo() + " changed correctly");
            LOGGER.info("Pseudo accepté et unique: " + pseudo);
        }


    }




    public void startServerUDP(){
        LOGGER.trace("Lancement du Serveur UDP");
        udpServer.start();
    }
    public void startServerTCP(){
        LOGGER.trace("Lancement du Serveur TCP");
        tcpServer.start();
    }

    public void closeUdpServer(){
        udpServer.close();
    }

    public void closeTcpServer() throws IOException {
            tcpServer.close();
    }

    public void startUpdateContacts(){
        LOGGER.trace("Lancement du Thread d'update des contacts");
        this.updateContactListThread = new UpdateContactListThread(this.broadcastAddress, this.contactsManager);
        updateContactListThread.start();
        updateContactListThread.setName("UCT Thread - " + this.getMonContact().getPseudo());
    }

    public void afficherListeContacts(){
        LOGGER.info("Je suis " + getMonContact().getPseudo() + " et ma liste de contact est :");
        //System.out.println("[");
        this.contactsManager.printContactList();
        //System.out.println("]");
    }

    public void closeChat(){
        LOGGER.trace("Tentative d'interruption du serveur UDP :  " + getMonContact().getPseudo());
        closeUdpServer();
        try {
            closeTcpServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (updateContactListThread != null){
            LOGGER.trace("Tentative d'interruption du Thread d'update de " + getMonContact().getPseudo());
            updateContactListThread.interrupt();
        }
    }

    public boolean isStarted(){
        return udpServer.isAlive();
    }

    public UpdateContactListThread getUpdateContactListThread() {
        return this.updateContactListThread;
    }


}
