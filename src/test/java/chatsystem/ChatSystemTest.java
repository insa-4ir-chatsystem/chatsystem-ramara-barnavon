package chatsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import java.util.ArrayList;


public class ChatSystemTest {

    private ChatSystem chatSystem1;
    private ChatSystem chatSystem2;
    private ChatSystem chatSystem3;
    private ChatSystem chatSystem4;
    private ChatSystem chatSystem5;
    private ChatSystem chatSystem6;
    private static final Logger LOGGER = LogManager.getLogger(ChatSystemTest.class);

    @BeforeEach
    public void setUp() {
        // Initialisation d'instances de ChatSystem avec des valeurs de test
        chatSystem1 = new ChatSystem("127.0.0.1", 8080);
        chatSystem2 = new ChatSystem("127.0.0.1", 8081);
        chatSystem3 = new ChatSystem("127.0.0.1", 8082);
        /*
        chatSystem4 = new ChatSystem("127.0.0.1", 8083);
        chatSystem5 = new ChatSystem("127.0.0.1", 8084);
        chatSystem6 = new ChatSystem("127.0.0.1", 8085);

         */
        Main.portList = new ArrayList<Integer>();
        Main.portList.add(8080);
        Main.portList.add(8081);
        Main.portList.add(8082);
        /*
        Main.portList.add(8083);
        Main.portList.add(8084);
        Main.portList.add(8085);

         */
        //chatSystem1.start("chat1");
        //chatSystem2.start("chat2");
    }

    @AfterEach
    public void reset(){ //TODO:Mieux reset pour que les tests puissent s'enchainer
        chatSystem1.closeChat();
        chatSystem2.closeChat();
        chatSystem3.closeChat();
        /*
        chatSystem4.closeChat();
        chatSystem5.closeChat();
        chatSystem6.closeChat();

         */

        System.out.println("reset");
    }

    @Test
    public void testChooseID() {
        chatSystem1.start("chat1");
        chatSystem2.start("chat2");
        chatSystem3.start("chat3");

        assertNotEquals(chatSystem1.getMonContact().getId(), chatSystem2.getMonContact().getId());
        assertNotEquals(chatSystem3.getMonContact().getId(), chatSystem2.getMonContact().getId());
        assertNotEquals(chatSystem1.getMonContact().getId(), chatSystem3.getMonContact().getId());
    }

    @Test
    public void testChoosePseudo() {
        chatSystem1.start("chat1");
        chatSystem2.start("chat2");
        chatSystem3.start("chat2"); // same pseudo as chatSystem2, should fail


        //chatSystem3.start("chat2");
        //Stabilisation du système avant les tests
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.debug("Interrompu dans un sleep de "+ Thread.currentThread().getName());
        }
        //TODO:faut inverser ( assertEquals (expected,actual) ) et pas l'inverse
        //Passed
        assertEquals("chat1", chatSystem1.getMonContact().getPseudo());
        assertEquals("chat2", chatSystem2.getMonContact().getPseudo());
        assertFalse(chatSystem3.isOpen());
    }

    @Test
    public void testContactGathering() {
        chatSystem1.start("chat1");
        chatSystem2.start("chat2");
        chatSystem3.start("chat3");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.debug("Interrompu dans un sleep de "+ Thread.currentThread().getName());
        }



        assertTrue(chatSystem1.getCm().getContactList().contains(chatSystem2.getMonContact()));
        assertTrue(chatSystem1.getCm().getContactList().contains(chatSystem3.getMonContact()));

        assertTrue(chatSystem2.getCm().getContactList().contains(chatSystem1.getMonContact()));
        assertTrue(chatSystem2.getCm().getContactList().contains(chatSystem3.getMonContact()));

        assertTrue(chatSystem3.getCm().getContactList().contains(chatSystem1.getMonContact()));
        assertTrue(chatSystem3.getCm().getContactList().contains(chatSystem2.getMonContact()));

        chatSystem3.closeChat();
    }
    @Test
    public void testChangePseudo() {
        chatSystem1.start("chat1");
        chatSystem2.start("chat2");
        chatSystem3.start("chat3");

        try {
            Thread.sleep(1000);
            chatSystem1.changePseudo("chatChanger");//TODO:assigner le pseudo changé
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.debug("Interrompu dans un sleep de "+ Thread.currentThread().getName());
        }

        //Il faut tester que le nom de chat1 à changer en chatChanger
        assertEquals("chatChanger", chatSystem1.getMonContact().getPseudo());
        //Il faut tester que le nom c'est mis à jour chez les autres

    }

}
