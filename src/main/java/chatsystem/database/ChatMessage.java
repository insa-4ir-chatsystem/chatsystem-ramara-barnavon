package chatsystem.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ChatMessage(int id, int senderId, int receiverId, String content, LocalDateTime timestamp) {

    @Override
    public String toString() {
        return timestamp + "\n" + content;
    }
    public String dateFormatted(){
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}