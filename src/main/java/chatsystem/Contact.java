package chatsystem;

public class Contact {
    private String pseudo;
    private int id;

    //constructeur à faire

    public Contact(String pseudo, int id){
        this.id = id;
        this.pseudo = pseudo;
    }

    //méthode
    public String getPseudo() {
        return this.pseudo;
    }
    public int getId() {
        return this.id;
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

    @Override
    public String toString() {
        return "Contact{" +
                "pseudo='" + pseudo + '\'' +
                ", id=" + id +
                '}';
    }
}

