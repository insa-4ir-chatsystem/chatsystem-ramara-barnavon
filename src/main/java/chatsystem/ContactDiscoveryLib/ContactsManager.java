package chatsystem.ContactDiscoveryLib;

import chatsystem.ChatSystem;
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
            if (contactList.contains(c)) {
                //System.out.println("Déjà présent dans la liste, mise à jour du TTL");
                Contact contact = searchContactById(c.getId());
                contact.setTTL(4);
            } else {
                c.setTTL(4);
                contactList.add(c);
                //System.out.println("Add to contact list : " + c);
                this.idMax = Math.max(this.idMax, c.getId());
            }
        }
    }

    public synchronized Contact searchContactById(int contact){
        for(Contact c : this.contactList){
            if (c.getId() == (contact)){
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
        while (iterator.hasNext()) {
            Contact c = iterator.next();
            c.decrementTTL();
            if (c.getTTL() <= 0) {
                iterator.remove();
                LOGGER.info("{" + c.getPseudo() + "}  TTl expiré, contact retiré : " + c); //TODO: ptete que ce serait sympa de savoir c'est sur la liste de qui qu'il est retiré
                //System.out.println("M");
            }
        }
    }


    public synchronized void afficherListe() {
        if (!contactList.isEmpty()) {
            for (Contact contact : this.contactList) {
                LOGGER.info("    {" + monContact.getPseudo() + "}    " + contact);
            }
        }else {
            LOGGER.info("Liste de contacts de " + monContact + " est vide");
        }
    }
}
