package chatsystem.model.contact_discovery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/** This class is used to handle the local list of contact in the network */
public class ContactsManager {
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

    /** Adds a new observer to the class */
    public void addObserver(ContactsManager.Observer obs) {
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }


    /** Constructor */
    public ContactsManager(){
        this.monContact = new Contact();
        this.contactList = new ArrayList<>();
        this.idMax = 1;
    }

    /** Getters/Setters */
    public void setMonContact(Contact monContact) {
        this.monContact = monContact;
    }

    public void setIdMax(int idMax) {
        this.idMax = Math.max(this.idMax, idMax);
    }

    public Contact getMonContact() {
        return monContact;
    }

    public int getIdMax() {
        return idMax;
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }



    /** Methods */

    /** Processes the contact information we are receiving */
    public synchronized void updateContact(Contact c) {
        if (!c.getPseudo().equals(Contact.NO_PSEUDO)) {
            if (contactList.contains(c)) { // update contact info if needed
                LOGGER.trace("Déjà présent dans la liste, mise à jour du TTL et du pseudo");
                Contact contact = searchContactByID(c.getId());
                contact.setPseudo(c.getPseudo());
                contact.setIp(c.getIp());
                contact.setTTL(4);
                contact.setOnline(true);
                notifyUpdateContactObservers(contact);
            } else { // otherwise adds it to the list
                c.setTTL(4);
                c.setOnline(true);
                notifyAddContactObservers(c);
                contactList.add(c);
                LOGGER.debug("Add to contact list : " + c);
                this.idMax = Math.max(this.idMax, c.getId());
            }
        }
    }

    /** Searches a contact by its unique ID */
    public synchronized Contact searchContactByID(int id){
        for(Contact c : this.contactList){
            if (c.getId() == (id)){
                return c;
            }
        }
        return null;
    }

    /** Searches a contact by its unique pseudo */
    public synchronized Contact searchContactByPseudo(String contact){
        for(Contact c : this.contactList){
            if (c.getPseudo().equals(contact)){
                return c;
            }
        }
        return null;
    }

    /** Searches a contact by its unique IP address */
    public synchronized Contact searchContactByIP(String ip){
        for(Contact c : this.contactList){
            if (c.getIp().equals(ip)){
                return c;
            }
        }
        return null;
    }

    /** Searches a connected contact by its unique ID */
    public synchronized Contact searchConnectedContactByID(int id){
        for(Contact c : this.contactList){
            if (c.getId() == (id) && c.isOnline()){
                return c;
            }
        }
        return null;
    }

    /** Searches a contact by its unique pseudo */
    public synchronized Contact searchConnectedContactByPseudo(String contact){
        for(Contact c : this.contactList){
            if (c.getPseudo().equals(contact) && c.isOnline()){
                return c;
            }
        }
        return null;
    }

    /** Searches a contact by its unique IP address */
    public synchronized Contact searchConnectedContactByIP(String ip){
        for(Contact c : this.contactList){
            if (c.getIp().equals(ip) && c.isOnline()){
                return c;
            }
        }
        return null;
    }

    /** Decreases the TTL of all the contacts of the list */
    public synchronized void decreaseTTL() {
        for(Contact c : this.contactList){
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

    /** Displays the current list of contacts */
    public synchronized void printContactList() {
        if (!contactList.isEmpty()) {
            for (Contact contact : this.contactList) {
                LOGGER.debug("    {" + monContact.getPseudo() + "}    " + contact);
            }
        }else {
            LOGGER.debug("Liste de contacts de " + monContact + " est vide");
        }
    }

    /** Notify all observers that a contact is added */
    private void notifyAddContactObservers(Contact contact) {
        synchronized (this.observers) {
            for (Observer observer : this.observers) {
                observer.addContact(contact);
            }
        }
    }

    /** Notify all observers that a contact is updated */
    private void notifyUpdateContactObservers(Contact contact) {
        synchronized (this.observers) {
            for (Observer observer : this.observers) {
                observer.updateContact(contact);
            }
        }
    }

}
