package chatsystem.ContactDiscoveryLib;

import java.util.ArrayList;
import java.util.Iterator;

public class ContactsManager { // verif de la liste de contacts (expirations) toutes les 20s minimum
    private ArrayList<Contact> ContactList;
    private Contact monContact;
    private int idMax;


    //contructeur
    public ContactsManager(){
        this.ContactList = new ArrayList<>();
        this.idMax = 1;
    }

    public void setMonContact(Contact monContact) {
        this.monContact = monContact;
    }

    public void setIdMax(int idMax) {
        this.idMax = Math.max(this.idMax, idMax);
    }

    public int getIdMax() {
        return idMax;
    }

    //méthodes



    public synchronized void updateContact(Contact c) {
        if (ContactList.contains(c)) {
            //System.out.println("Déjà présent dans la liste, mise à jour du TTL");
            Contact contact = search_contact_by_id(c.getId());
            contact.setTTL(4);
        } else {
            c.setTTL(4);
            ContactList.add(c);
            //System.out.println("Add to contact list : " + c);
            this.idMax = Math.max(this.idMax, c.getId());
        }
    }

    public synchronized Contact search_contact_by_id(int contact){
        for(Contact c : this.ContactList){
            if (c.getId() == (contact)){
                return c;
            }
        }
        return null;
    }

    public synchronized Contact search_contact_by_pseudo(String contact){
        for(Contact c : this.ContactList){
            if (c.getPseudo().equals(contact)){
                return c;
            }
        }
        return null;
    }

    public synchronized void decreaseTTL() {
        Iterator<Contact> iterator = this.ContactList.iterator();
        while (iterator.hasNext()) {
            Contact c = iterator.next();
            c.decrementTTL();
            if (c.getTTL() <= 0) {
                iterator.remove();
                System.out.println("{" + monContact.getPseudo() + "}  TTl expiré, contact retiré : " + c); //TODO: ptete que ce serait sympa de savoir c'est sur la liste de qui qu'il est retiré
                //System.out.println("M");
            }
        }
    }


    public synchronized void afficherListe() {
        if (!ContactList.isEmpty()) {
            for (Contact contact : this.ContactList) {
                System.out.println("    {" + monContact.getPseudo() + "}    " + contact);
            }
        }else {
            System.out.println(" vide");
        }
    }
}
