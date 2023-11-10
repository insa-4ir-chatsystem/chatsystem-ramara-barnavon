package chatsystem;

import java.util.ArrayList;

public class ContactsManager { // verif de la liste de contacts (expirations) toutes les 20s minimum
    ArrayList<Contact> ContactList;

    //contructeur
    public ContactsManager(){
        this.ContactList = new ArrayList<>();
    }


    //méthodes

    public void addContact(Contact c) {
        if (ContactList.contains(c)) {
            System.out.println("Déjà présent dans la liste, abandon de l'ajout");
        } else {
            ContactList.add(c);
        }


    }
    public void afficherListe () {
        for (Contact contact : this.ContactList) {
            System.out.println(contact);
        }
    }
}
