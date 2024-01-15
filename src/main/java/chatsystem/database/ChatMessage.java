package chatsystem.database;

import java.time.LocalDateTime;

public record ChatMessage(int id, int senderId, int receiverId, String content, LocalDateTime timestamp) {

    @Override
    public String toString() {
        return timestamp + "\n" + content;
    }
}