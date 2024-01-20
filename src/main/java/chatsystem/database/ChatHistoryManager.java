package chatsystem.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

/** This class is used to manage the DB containing all the messages */
public class ChatHistoryManager {
    private static final String DB_PATH = "jdbc:sqlite:history/chat_history.db"; // Local Database

    /** Creates a Table to store all the messages */
    public void createChatHistoryTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS chat_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender INT," +
                "receiver INT," +
                "message TEXT," +
                "timestamp DATETIME DEFAULT (datetime('now', '+1 hour'))" + // UTC+1
                ")";

        Connection connection = DriverManager.getConnection(DB_PATH);
        Statement statement = connection.createStatement();
        statement.execute(query);
    }

    /** Deletes the Table containing the messages */
    public void deleteChatHistoryTable() throws SQLException {
        String query = "DROP TABLE IF EXISTS chat_history";

        Connection connection = DriverManager.getConnection(DB_PATH);
        Statement statement = connection.createStatement();
        statement.execute(query);
    }

    /** Inserts a message in the table with the sender ID */
    public void insertMessage(int sender, int receiver, String message) throws SQLException {
        String query = "INSERT INTO chat_history(sender, receiver, message) VALUES(?, ?, ?)";

        Connection connection = DriverManager.getConnection(DB_PATH);
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, sender);
        statement.setInt(2, receiver);
        statement.setString(3, message);
        statement.executeUpdate();
    }

    /** Gets all the messages exchanged between two contacts, ordered by timestamp */
    public ArrayList<ChatMessage> getHistoryOf(int contact1, int contact2) throws SQLException {
        ArrayList<ChatMessage> part1 = getSentTo(contact1, contact2);
        ArrayList<ChatMessage> part2 = getSentTo(contact2, contact1);
        ArrayList<ChatMessage> all = new ArrayList<>(part1);
        all.addAll(part2);
        Collections.sort(all);

        return all;
    }

    /** Gets all the messages sent with id senderID to id receiverID */
    public ArrayList<ChatMessage> getSentTo(int senderID, int receiverID) throws SQLException {
        String query = "SELECT * FROM chat_history WHERE sender = ? AND receiver = ?";
        ArrayList<ChatMessage> Qresult = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_PATH);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, senderID);
            statement.setInt(2, receiverID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int senderId = resultSet.getInt("sender");
                int receiverId = resultSet.getInt("receiver");
                String content = resultSet.getString("message");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

                Qresult.add(new ChatMessage(senderId, receiverId, content, timestamp));
            }
        }
        return Qresult;
    }


}
