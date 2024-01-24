package chatsystem.network;

import java.net.InetAddress;
import java.util.Objects;

public record UDP_Message(String content, InetAddress origin) {

}