package chatsystem.network;

import java.net.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** Thread that is launch for every active user that contact us with TCP  */
public class TCP_Session extends Thread {
    /** Attribute */
    private static final Logger LOGGER = LogManager.getLogger(TCP_Session.class);
    private Socket clientSocket;
    private BufferedReader in;
    private final List<TCP_Session.Observer> observers = new ArrayList<>();
    private final InetAddress ipSender;

    /** Constructor */
    public TCP_Session(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.ipSender = clientSocket.getInetAddress();

    }

    /** Methods */
    public interface Observer {
        /** Method that is called each time a message is received. */
        void handle(String received, InetAddress ipSender);
    }
    public void addObserver(TCP_Session.Observer obs) {
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }
    /** */

    public String listen() throws IOException{
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message = in.readLine();

        LOGGER.debug(message);

        return message;
    }
    public void close() throws IOException{


        in.close();
        clientSocket.close();
        this.interrupt();
    }

    public void run() {
        while(!this.isInterrupted()) {
            try {
                String message = listen();
                if (message != null) {
                    LOGGER.debug(message);
                } else {
                    LOGGER.debug("Client disconnected, ip = " + ipSender);
                    break;
                }
                synchronized (this.observers) {
                    for (TCP_Session.Observer obs : this.observers) {
                        obs.handle(message, this.ipSender);
                    }
                }


            } catch (IOException e) {
                LOGGER.error("Receive error: " + e.getMessage());
                this.interrupt();
            }
        }
    }
}
