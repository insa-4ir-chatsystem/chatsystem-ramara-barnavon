package chatsystem.network;

import chatsystem.ContactDiscoveryLib.Contact;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/** Class providing utility methods to send UDP messages. */
public class UDP_Client {

    /** Sends a UDP message on the given address and port. */
    public static void send(InetAddress addr, int port, String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buff = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, addr, port);
        socket.send(packet);
        socket.close();
    }

    public static void send_INCO(InetAddress addr, int port, Contact contact) throws IOException{
        String msg_to_send = "INCO" + ":" + contact.getPseudo() + ":" + contact.getId();
            send(addr, port, msg_to_send);
    }

    public static void send_DECO(InetAddress addr, int port) throws IOException{
        String msg_to_send = "DECO";
            send(addr, port, msg_to_send);
    }

    public static void send_DEPS(InetAddress addr, int port, String pseudo) throws IOException{
        String msg_to_send = "DEPS" + ":" + pseudo;
            send(addr, port, msg_to_send);
    }

    public static void send_REPS(InetAddress addr, int port) throws IOException{
        String msg_to_send = "REPS";
        send(addr, port, msg_to_send);
    }

    public static void send_DEID(InetAddress addr, int port, int id_voulu) throws IOException{
        String msg_to_send = "DEID"+ ":" + id_voulu;
        send(addr, port, msg_to_send);
    }


    public static void send_REID(InetAddress addr, int port, int id) throws IOException{
        String msg_to_send = "REID" + ":" + id;
        send(addr, port, msg_to_send);
    }

    public static void sendLocalhost(int port, String message) throws IOException {
        UDP_Client.send(InetAddress.getLocalHost(), port, message);
    }
}