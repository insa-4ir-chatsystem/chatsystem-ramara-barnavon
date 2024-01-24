package chatsystem;

import chatsystem.controller.ChatSystem;
import chatsystem.view.GUI;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Main {

    // TODO: Display ID in addition to the pseudo ?
    // Some synchronized keywords might be useless in the ChatSystem

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) {

        Configurator.setRootLevel(Level.ERROR);

        ChatSystem ChatJ = new ChatSystem();
        GUI gui = new GUI(ChatJ);
        gui.start();

        LOGGER.info("Début de la démonstration");

    }
}
