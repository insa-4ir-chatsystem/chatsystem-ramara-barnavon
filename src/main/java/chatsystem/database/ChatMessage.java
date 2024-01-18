package chatsystem.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public record ChatMessage(int senderId, int receiverId, String content, LocalDateTime timestamp) implements Comparable<ChatMessage>{

    @Override
    public String toString() {
        return timestamp + "\n" + content;
    }
    public String dateFormatted(){
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    @Override
    public int compareTo(ChatMessage other) {
        // Compare based on timestamps
        return Comparator.comparing(ChatMessage::timestamp).compare(this, other);
    }
}