package chatsystem.ContactDiscoveryLib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
/** This class is used to handle the local list of contact in the network */
public class ContactsManager { // verif de la liste de contacts (expirations) toutes les 20s minimum
    private ArrayList<Contact> contactList;
    private Contact monContact;
    private int idMax;
    private static final Logger LOGGER = LogManager.getLogger(ContactsManager.class);


    /** Constructor */
    public ContactsManager(){
        this.contactList = new ArrayList<>();
        this.idMax = 1;
    }


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
        if (c.getPseudo() != Contact.NO_PSEUDO) {
            if (contactList.contains(c)) { //contains marche que avec l'id
                LOGGER.debug("Déjà présent dans la liste, mise à jour du TTL et du pseudo");
                Contact contact = searchContactByID(c.getId());
                contact.setPseudo(c.getPseudo());
                contact.setTTL(4);
                contact.setOnline(true);
            } else {
                c.setTTL(4);
                c.setOnline(true);
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

}
