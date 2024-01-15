package chatsystem.network;

import java.net.InetAddress;

public record TCP_Message(String content, InetAddress origin) {
}
