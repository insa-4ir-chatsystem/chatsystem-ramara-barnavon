package chatsystem.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ChatHistoryManagerTest {
    private ChatHistoryManager chatHistoryManager;

    @BeforeEach
    void setUp() {
        chatHistoryManager = new ChatHistoryManager();

        try {
            chatHistoryManager.deleteChatHistoryTable();
            chatHistoryManager.createChatHistoryTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        try {
            chatHistoryManager.deleteChatHistoryTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testInsertMessage() {
        try {
            chatHistoryManager.insertMessage(1, 2, "Hello");
            ArrayList<ChatMessage> history = chatHistoryManager.getSentTo(1, 2);
            assertEquals(1, history.size());
            assertEquals("Hello", history.get(0).content());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testGetHistoryOf() {
        try{
            chatHistoryManager.insertMessage(1, 2,"Message1");
            chatHistoryManager.insertMessage(1, 2,"Message2");
            chatHistoryManager.insertMessage(2, 1,"Message3");

            ArrayList<ChatMessage> history1 = chatHistoryManager.getSentTo(1, 2);
            ArrayList<ChatMessage> history2 = chatHistoryManager.getSentTo(2, 1);

            assertEquals(2, history1.size());
            assertEquals(1, history2.size());
            assertEquals("Message1", history1.get(0).content());
            assertEquals("Message2", history1.get(1).content());
            assertEquals("Message3", history2.get(0).content());
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }

    @Test
    void testDeleteChatHistoryTable() {
        try{
            chatHistoryManager.insertMessage(1, 2,"Hello");
            chatHistoryManager.deleteChatHistoryTable();
            assertThrows(SQLException.class, () -> chatHistoryManager.getHistoryOf(1, 2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
