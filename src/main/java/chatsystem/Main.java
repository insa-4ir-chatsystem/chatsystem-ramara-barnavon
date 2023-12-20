package chatsystem;

import chatsystem.view.GUI;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.ArrayList;
//TODO : adapter les LOGGER INFO pour éviter un surchargement
public class Main {

    public static ArrayList<Integer> portList;
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        //TODO: push les diagrammes uml sur le depot
        Configurator.setRootLevel(Level.TRACE);
        //Liste des ports qu'on prévoit en avance d'utiliser
        String ip = "localhost";
        int portJ = 2023;
        int portK = 2024;
        int portZ = 2025;
        int portM = 1789;
        int portDouble = 2026;
        //TODO: JIRA

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



        GUI gui = new GUI(ChatJ);
        gui.start();

        LOGGER.info("Début de la démonstration");

        ChatJ.start();
        ChatM.start();
        //ChatZ.start("zorro");
        //ChatK.start("matos");

        try {
            Thread.sleep(000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("[TEST] Vérification de la mise à jour de la liste lors d'une deconnexion ");
        //TODO:Ne montrer que les paroles de zorro pour mieux comprendre ptete? ( inutile si on implémente IPs dans le futur )
        //ChatM.closeChat();
        LOGGER.info("[TEST] Vérification de l'unicité du pseudo");
        //ChatDouble.start("juju");
        ChatJ.closeChat();
        ChatM.closeChat();

    }
}
