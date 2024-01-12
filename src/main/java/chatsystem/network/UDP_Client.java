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
        socket.setBroadcast(true);
        byte[] buff = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, addr, port);
        socket.send(packet);
        if(!(message.startsWith("INCO") || message.startsWith("DECO"))){
            LOGGER.debug("Packet sent : " + message + " to address " + addr );
        }
        socket.close();
    }

    public static void send_INCO(InetAddress addr, int port, Contact contact) throws IOException{
        String msg_to_send = "INCO" + ":" + contact.getPseudo() + ":" + contact.getId() + ":" + port;
            send(addr, port, msg_to_send);
    }

    public static void send_DECO(InetAddress addr, int port) throws IOException{
        String msg_to_send = "DECO" + ":" + port;
            send(addr, port, msg_to_send);
    }

    public static void send_DEPS(InetAddress addr, int port, String pseudo) throws IOException{
        String msg_to_send = "DEPS" + ":" + pseudo + ":" + port;
            send(addr, port, msg_to_send);
    }

    public static void send_REPS(InetAddress addr, int port) throws IOException{
        String msg_to_send = "REPS:" + port;
        send(addr, port, msg_to_send);
    }

    public static void send_DEID(InetAddress addr, int port, int id_voulu) throws IOException{
        String msg_to_send = "DEID"+ ":" + id_voulu + ":" + port;
        send(addr, port, msg_to_send);
    }


    public static void send_REID(InetAddress addr, int destPort, int id) throws IOException{
        String msg_to_send = "REID"  + ":" + id + ":" + destPort;
        send(addr, destPort, msg_to_send);
    }
    public static void send_CHPS(InetAddress addr, int destPort, String pseudo, int id) throws IOException{
        String msg_to_send = "CHPS"  + ":" + pseudo+ ":" + id + ":" + destPort;
        send(addr, destPort, msg_to_send);
    }
    public static void send_RECH(InetAddress addr, int destPort) throws IOException{
        String msg_to_send = "RECH:" + destPort;
        send(addr, destPort, msg_to_send);
    }

    public static void sendLocalhost(int port, String message) throws IOException {
        UDP_Client.send(InetAddress.getLocalHost(), port, message);
    }
}