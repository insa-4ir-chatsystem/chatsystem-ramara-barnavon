package chatsystem.ContactDiscoveryLib;

import java.util.ArrayList;

public class ContactsManager { // verif de la liste de contacts (expirations) toutes les 20s minimum
    private ArrayList<Contact> ContactList;
    private int idMax;


    //contructeur
    public ContactsManager(){
        this.ContactList = new ArrayList<>();
        this.idMax = 1;
    }

    public void setIdMax(int idMax) {
        this.idMax = idMax;
    }

    public int getIdMax() {
        return idMax;
    }

    //méthodes



    public void updateContact(Contact c) {
        if (ContactList.contains(c)) {
            System.out.println("Déjà présent dans la liste, mise à jour du TTL");
            Contact contact = search_contact_by_id(c.getId());
            contact.setTTL(4);
        } else {
            ContactList.add(c);
            this.idMax = Math.max(this.idMax, c.getId());
        }
    }

    public Contact search_contact_by_id(int contact){
        for(Contact c : this.ContactList){
            if (c.getId() == (contact)){
                return c;
            }
        }
        return null;
    }

    public Contact search_contact_by_pseudo(String contact){
        for(Contact c : this.ContactList){
            if (c.getPseudo().equals(contact)){
                return c;
            }
        }
        return null;
    }

    public void decreaseTTL(){
        for(Contact c : this.ContactList){
            c.decrementTTL();
            if(c.getTTL() == 0){
                ContactList.remove(c);
            }
        }
    }

    public void afficherListe() {
        for (Contact contact : this.ContactList) {
            System.out.println(contact);
        }
    }
}
