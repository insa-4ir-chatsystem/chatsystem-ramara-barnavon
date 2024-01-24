package chatsystem.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/** A ChatMessage represents a message sent on the network plus the timestamp and IDs*/
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