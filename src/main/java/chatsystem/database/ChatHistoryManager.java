package chatsystem.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatHistoryManager {// TODO : bien gérer les exceptions
    private static final String DB_URL = "jdbc:sqlite:./history/chat_history.db";

    public void createChatHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS chat_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender INT," +
                "message TEXT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteChatHistoryTable(){
        String sql = "DROP TABLE IF EXISTS chat_history";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertMessage(int sender, String message) {
        String sql = "INSERT INTO chat_history(sender, message) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, sender);
            query.setString(2, message);
            query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<ChatMessage> getHistoryOf(int sender) {
        String sql = "SELECT * FROM chat_history WHERE id = ?";
        ArrayList<ChatMessage> Qresult = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, sender);
            ResultSet resultSet = query.executeQuery(sql);
            while (resultSet.next()) {
                int Mid = resultSet.getInt("id");
                int senderId = resultSet.getInt("sender");
                String content = resultSet.getString("message");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

                Qresult.add(new ChatMessage(Mid, senderId, content, timestamp));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Qresult;
    }


}
