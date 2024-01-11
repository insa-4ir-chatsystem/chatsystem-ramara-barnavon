package chatsystem.database;

import java.time.LocalDateTime;

public record ChatMessage(int id, int sender, String content, LocalDateTime timestamp) {


    @Override
    public String content() {
        return content;
    }
}