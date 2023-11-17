package chatsystem.ContactDiscoveryLib;

import java.util.Objects;

public class Contact {
    private String pseudo;
    private int id;
    private int TTL; // time to live before removing from Contact list

    public Contact(String pseudo, int id){
        this.id = id;
        this.pseudo = pseudo;
    }

    public Contact(String pseudo, int id, int TTL){
        this.id = id;
        this.pseudo = pseudo;
        this.TTL = TTL;
    }

    //méthode
    public String getPseudo() {
        return this.pseudo;
    }
    public int getId() {
        return this.id;
    }


    public int getTTL(){
        return this.TTL;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void decrementTTL(){
        this.TTL--;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Contact autre = (Contact) obj;
        return autre.id == this.id;
    }

    public boolean samePseudo(Contact contact){
        return contact.getPseudo().equals(this.pseudo);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "pseudo='" + pseudo + '\'' +
                ", id=" + id +
                '}';
    }
}

