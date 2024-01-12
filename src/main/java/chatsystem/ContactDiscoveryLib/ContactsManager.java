package chatsystem.ContactDiscoveryLib;

import chatsystem.network.UDP_Message;
import chatsystem.network.UDP_Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** This class is used to handle the local list of contact in the network */
public class ContactsManager { // verif de la liste de contacts (expirations) toutes les 20s minimum
    private ArrayList<Contact> contactList;
    private Contact monContact;
    private int idMax;
    private final List<ContactsManager.Observer> observers = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(ContactsManager.class);


    /** Interface that observers the ContactList  */
    public interface Observer {
        /** Method that is called each time a contact is created. */
        void addContact(Contact contact);
        /** Method that is called each time a contact is updated. */
        void updateContact(Contact contact);
    }

    /** Adds a new observer to the class, for which the handle method will be called for each incoming message. */
    public void addObserver(ContactsManager.Observer obs) {
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }


    /** Constructor */
    public ContactsManager(){
        this.contactList = new ArrayList<>();
        this.idMax = 1;
    }

    // TODO: add observers to this class to update the contact list in the gui

    /** Getter/Setter */
    public void setMonContact(Contact monContact) {
        this.monContact = monContact;
    }

    public void setIdMax(int idMax) {
        this.idMax = Math.max(this.idMax, idMax);
    }

    public int getIdMax() {
        return idMax;
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    /** Methods */


    public synchronized void updateContact(Contact c) {
        if (!c.getPseudo().equals(Contact.NO_PSEUDO)) {
            if (contactList.contains(c)) { //contains marche que avec l'id
                LOGGER.debug("Déjà présent dans la liste, mise à jour du TTL et du pseudo");
                Contact contact = searchContactByID(c.getId());
                contact.setPseudo(c.getPseudo());
                contact.setTTL(4);
                contact.setOnline(true);
                notifyUpdateContactObservers(contact);
            } else {
                c.setTTL(4);
                c.setOnline(true);
                notifyAddContactObservers(c);
                contactList.add(c);
                LOGGER.debug("Add to contact list : " + c);
                this.idMax = Math.max(this.idMax, c.getId());
            }
        }
    }

    public synchronized Contact searchContactByID(int id){
        for(Contact c : this.contactList){
            if (c.getId() == (id)){
                return c;
            }
        }
        return null;
    }

    public synchronized Contact searchContactByPseudo(String contact){
        for(Contact c : this.contactList){
            if (c.getPseudo().equals(contact)){
                return c;
            }
        }
        return null;
    }

    public synchronized void decreaseTTL() {
        Iterator<Contact> iterator = this.contactList.iterator();
        //LOGGER.info("List of contact to be ttldecreased : " + contactList);
        while (iterator.hasNext()) { //strange because needed before
            Contact c = iterator.next();
            if(!c.isOnline()){
                continue;
            }
            c.decrementTTL();
            if (c.getTTL() <= 0) {
                c.setOnline(false);
                notifyUpdateContactObservers(c);
                LOGGER.debug("{" + c.getPseudo() + "}  TTl expiré, contact retiré : " + c);
            }
        }
    }


    public synchronized void afficherListe() {
        if (!contactList.isEmpty()) {
            for (Contact contact : this.contactList) {
                LOGGER.debug("    {" + monContact.getPseudo() + "}    " + contact);
            }
        }else {
            LOGGER.debug("Liste de contacts de " + monContact + " est vide");
        }
    }

    // Notify all observers that a contact is added
    private void notifyAddContactObservers(Contact contact) {
        synchronized (this.observers) {
            for (Observer observer : this.observers) {
                observer.addContact(contact);
            }
        }
    }

    // Notify all observers that a contact is updated
    private void notifyUpdateContactObservers(Contact contact) {
        synchronized (this.observers) {
            for (Observer observer : this.observers) {
                observer.updateContact(contact);
            }
        }
    }

}
