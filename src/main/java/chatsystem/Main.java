package chatsystem;

import chatsystem.ContactDiscoveryLib.ChatSystem;
import chatsystem.ContactDiscoveryLib.Contact;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class Main {

    public static ArrayList<Integer> portList;
    public static void main(String[] args) {

        //Liste des ports qu'on prévoit en avance d'utiliser
        String ip = "localhost";
        int portJ = 2023;
        int portK = 2024;
        int portZ = 2025;
        int portM = 1789;
        int portDouble = 2026;

        portList = new ArrayList<Integer>();
        portList.add(portK);
        portList.add(portZ);
        portList.add(portJ);
        portList.add(portM);
        portList.add(portDouble);

        ChatSystem ChatJ = new ChatSystem(ip, portJ);
        ChatSystem ChatM = new ChatSystem(ip, portM);
        ChatSystem ChatZ = new ChatSystem(ip, portZ);
        ChatSystem ChatK = new ChatSystem(ip, portK);
        ChatSystem ChatDouble = new ChatSystem(ip, portDouble);

        System.out.println("Début de la démonstration");
        System.out.println("Lancement des chatsystems");


        ChatJ.start("juju");
        ChatM.start("matis");
        ChatZ.start("zorro");
        //ChatK.start("matos");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("[TEST] Vérification de la mise à jour de la liste lors d'une deconnexion ");
        //TODO:Ne montrer que les paroles de zorro pour mieux comprendre ptete? ( inutile si on implémente IPs dans le futur )
        ChatM.closeChat();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("[TEST] Vérification de l'unicité du pseudo");
        ChatDouble.start("juju");

    }
}
