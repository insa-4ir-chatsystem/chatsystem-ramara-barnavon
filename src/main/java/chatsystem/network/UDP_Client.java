package chatsystem.network;

import chatsystem.ChatSystem;
import chatsystem.ContactDiscoveryLib.Contact;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/** Class providing utility methods to send UDP messages. */
public class UDP_Client {


    private static final Logger LOGGER = LogManager.getLogger(UDP_Client.class);

    /** Sends a UDP message on the given address and port. */
    public static void send(InetAddress addr, int port, String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buff = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, addr, port);
        socket.send(packet);
        LOGGER.trace("Packet sent : " + message + " to address " + addr );
        socket.close();
    }

    public static void send_INCO(InetAddress addr, int localPort, int port, Contact contact) throws IOException{
        String msg_to_send = "INCO" + ":" + contact.getPseudo() + ":" + contact.getId() + ":" + localPort;
            send(addr, port, msg_to_send);
    }

    public static void send_DECO(InetAddress addr, int localPort, int port) throws IOException{
        String msg_to_send = "DECO" + ":" + localPort;
            send(addr, port, msg_to_send);
    }

    public static void send_DEPS(InetAddress addr, int localPort, int port, String pseudo) throws IOException{
        String msg_to_send = "DEPS" + ":" + pseudo + ":" + localPort;
            send(addr, port, msg_to_send);
    }

    public static void send_REPS(InetAddress addr, int localPort, int destPort) throws IOException{
        String msg_to_send = "REPS:" + localPort;
        send(addr, destPort, msg_to_send);
    }

    public static void send_DEID(InetAddress addr, int localPort, int destPort, int id_voulu) throws IOException{
        String msg_to_send = "DEID"+ ":" + id_voulu + ":" + localPort;
        send(addr, destPort, msg_to_send);
    }


    public static void send_REID(InetAddress addr, int localPort, int destPort, int id) throws IOException{
        String msg_to_send = "REID"  + ":" + id + ":" + localPort;
        send(addr, destPort, msg_to_send);
    }
    public static void send_CHPS(InetAddress addr, int localPort, int destPort, String pseudo, int id) throws IOException{
        String msg_to_send = "CHPS"  + ":" + pseudo+ ":" + localPort + ":" + id;
        send(addr, destPort, msg_to_send);
    }
    public static void send_RECH(InetAddress addr, int localPort, int destPort) throws IOException{
        String msg_to_send = "RECH:" + localPort;
        send(addr, destPort, msg_to_send);
    }

    public static void sendLocalhost(int port, String message) throws IOException {
        UDP_Client.send(InetAddress.getLocalHost(), port, message);
    }
}