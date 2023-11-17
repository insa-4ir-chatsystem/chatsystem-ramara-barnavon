package chatsystem;

import chatsystem.ContactDiscoveryLib.ChatSystem;
import chatsystem.ContactDiscoveryLib.Contact;

import java.util.ArrayList;

public class Main {

    public static ArrayList<Integer> portList;
    public static void main(String[] args) {

        //TODO: Créer un contact à traver une méthode permettant de verif l'unicité
        Contact leJ = new Contact("jules", 69);
        Contact leM = new Contact("Ping-Win", 420);
        Contact leZ = new Contact("Zemmour", 667);
        Contact leK = new Contact("Kbo", 71);



        String ip = "localhost";
        int portJ = 2023;
        int portK = 2024;
        int portZ = 2025;
        int portM = 1789;

        portList = new ArrayList<Integer>();
        portList.add(portK);
        portList.add(portZ);
        portList.add(portJ);
        portList.add(portM);

        ChatSystem ChatJ = new ChatSystem(leJ, ip, portJ);
        ChatSystem ChatM = new ChatSystem(leM,ip, portM);
        ChatSystem ChatZ = new ChatSystem(leZ, ip, portZ);
        ChatSystem ChatK = new ChatSystem(leK,ip, portK);

        ChatJ.startListening();
        ChatM.startListening();
        ChatZ.startListening();
        ChatK.startListening();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(Integer I : portList){
            ChatJ.demande_liste_contact(I);
            ChatM.demande_liste_contact(I);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ChatJ.afficherListeContacts();

        ChatM.afficherListeContacts();

    }
}
