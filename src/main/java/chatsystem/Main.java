package chatsystem;

import chatsystem.ContactDiscoveryLib.ChatSystem;
import chatsystem.ContactDiscoveryLib.Contact;

import java.util.ArrayList;

public class Main {

    public static ArrayList<Integer> portList;
    public static void main(String[] args) {


        /*
        Contact leJ = new Contact("jules", 69);
        Contact leM = new Contact("Ping-Win", 420);
        Contact leZ = new Contact("Zemmour", 667);
        Contact leK = new Contact("Kbo", 71);
        */



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

        ChatSystem ChatJ = new ChatSystem(ip, portJ);
        ChatSystem ChatM = new ChatSystem(ip, portM);
        ChatSystem ChatZ = new ChatSystem(ip, portZ);
        ChatSystem ChatK = new ChatSystem(ip, portK);

        ChatJ.start("juju");
        ChatM.start("matos");
        //ChatZ.start("zorro");
        //ChatK.start("matis");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ChatM.closeChat();

    }
}
