package chatsystem.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TCP_Server extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(UDP_Server.class);
    private final Socket socket;
    private final List<UDP_Server.Observer> observers = new ArrayList<>();

    /**
     * Interface that observers of the UDP server must implement.
     */
    public interface Observer {
        /**
         * Method that is called each time a message is received.
         */
        void handle(UDP_Message received, int port);
    }

    public TCP_Server(int port, String ip) throws SocketException, UnknownHostException {
        try {
            socket = new Socket(InetAddress.getByName(ip), port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        while(!this.isInterrupted()) {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buf = new byte[1024];
                int bytesRead = inputStream.read(buf);
                String message = new String(buf, 0, bytesRead);
                int port = socket.getPort();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

}
