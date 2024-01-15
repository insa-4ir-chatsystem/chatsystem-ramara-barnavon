package chatsystem.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** This class is used to manage the DB containing all the messages */
public class ChatHistoryManager {
    private static final String DB_URL = "jdbc:sqlite:history/chat_history.db";

    /** Creates a Table to store all the messages */
    public void createChatHistoryTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS chat_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender INT," +
                "receiver INT," +
                "message TEXT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /** Deletes the Table containing the messages */
    public void deleteChatHistoryTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS chat_history";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /** Inserts a message in the table with the sender ID */
    public void insertMessage(int sender, int receiver, String message) throws SQLException {
        String sql = "INSERT INTO chat_history(sender, receiver, message) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, sender);
            query.setInt(2, receiver);
            query.setString(3, message);
            query.executeUpdate();
        }
    }

    /** Gets all the messages sent by a contact, searching by its ID */
    public ArrayList<ChatMessage> getHistoryOf(int sender) throws SQLException {
        String sql = "SELECT * FROM chat_history WHERE sender = ?";
        ArrayList<ChatMessage> Qresult = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, sender);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                int Mid = resultSet.getInt("id");
                int senderId = resultSet.getInt("sender");
                int receiverId = resultSet.getInt("receiver");
                String content = resultSet.getString("message");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

                Qresult.add(new ChatMessage(Mid, senderId, receiverId, content, timestamp));
            }
        }
        return Qresult;
    }

    /** Gets all the messages sent with id senderID to id receiverID */
    public ArrayList<ChatMessage> getSentTo(int senderID, int receiverID) throws SQLException {
        String sql = "SELECT * FROM chat_history WHERE sender = ? AND receiver = ?";
        ArrayList<ChatMessage> Qresult = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, senderID);
            query.setInt(1, receiverID);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                int Mid = resultSet.getInt("id");
                int senderId = resultSet.getInt("sender");
                int receiverId = resultSet.getInt("receiver");
                String content = resultSet.getString("message");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

                Qresult.add(new ChatMessage(Mid, senderId, receiverId, content, timestamp));
            }
        }
        return Qresult;
    }


}
