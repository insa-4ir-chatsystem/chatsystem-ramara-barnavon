package chatsystem.ContactDiscoveryLib;

import java.util.ArrayList;

public class ContactsManager { // verif de la liste de contacts (expirations) toutes les 20s minimum
    ArrayList<Contact> ContactList;


    //contructeur
    public ContactsManager(){
        this.ContactList = new ArrayList<>();
    }


    //méthodes

    public void updateContact(Contact c) {
        if (ContactList.contains(c)) {
            System.out.println("Déjà présent dans la liste, mise à jour du TTL");
            Contact contact = search_contact_by_id(c);
            contact.setTTL(4);
        } else {
            ContactList.add(c);
        }
    }

    public Contact search_contact_by_id(Contact contact){
        for(Contact c : this.ContactList){
            if (c.equals(contact)){
                return c;
            }
        }
        return null;
    }

    public Contact search_contact_by_name(Contact contact){
        for(Contact c : this.ContactList){
            if (c.samePseudo(contact)){
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
