package chatsystem.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/** UDP server that (once started) listens indefinitely on a given port. */
public class UDP_Server extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(UDP_Server.class);
    private final DatagramSocket socket;
    private final List<Observer> observers = new ArrayList<>();

    /** Interface that observers of the UDP server must implement. */
    public interface Observer {
        /** Method that is called each time a message is received. */
        void handle(UDP_Message received);
    }



    public UDP_Server(int port, String ip) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port, InetAddress.getByName(ip));
    }

    /** Adds a new observer to the class, for which the handle method will be called for each incoming message. */
    public void addObserver(Observer obs) {
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }

    /** Listens for incoming packet and return it */
    public DatagramPacket listen() throws IOException {
        byte[] buf = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        //System.out.println(new String(packet.getData(), 0, packet.getLength()));
        socket.receive(packet);
        return packet;
    }

    public void close(){
        socket.close();
        this.interrupt();
    }



    @Override
    public void run() {
        while(!this.isInterrupted()) {
            try {
                Thread.sleep(100); //TODO get rid of this but not now
                DatagramPacket packet = listen();

                // extract and print message
                String received = new String(packet.getData(), 0, packet.getLength());
                UDP_Message message = new UDP_Message(received, packet.getAddress());

                LOGGER.trace("Received on port " + socket.getLocalPort() + ": " + message.content() + " from " + message.origin());

                synchronized (this.observers) {
                    for (Observer obs : this.observers) {
                        obs.handle(message);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Receive error: " + e.getMessage());
            }
        }
    }
}