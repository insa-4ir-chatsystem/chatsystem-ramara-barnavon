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

/** Thread qui doit être lancé pour chaque session de chat actif avec quelqu'un du rezo */
public class TCP_Session extends Thread {
    /** Attribute */
    private static final Logger LOGGER = LogManager.getLogger(TCP_Server.class);
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private final List<TCP_Session.Observer> observers = new ArrayList<>();
    /** Constructor */
    public TCP_Session(Socket clientSocket, BufferedReader in,PrintWriter out) {
        this.clientSocket = clientSocket;
        this.in = in;
        this.out = out;
    }

    /** Methods */
    public interface Observer {
        /** Method that is called each time a message is received. */
        void handle(String received);
    }
    public void addObserver(TCP_Session.Observer obs) {
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }
    public String listen() throws IOException{
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message = in.readLine();
        LOGGER.debug(message);

        return message;
    }
    public void close() throws IOException{
        /** close everything */

        in.close();
        clientSocket.close();
        this.interrupt();
    }

    public void run() {
        while(!this.isInterrupted()) {
            try {
                String message = listen();

                /** String Handler */
                synchronized (this.observers) {
                    for (TCP_Session.Observer obs : this.observers) {
                        obs.handle(message);
                    }
                }

            } catch (IOException e) {
                LOGGER.error("Receive error: " + e.getMessage());
                this.interrupt();
            }
        }
    }
}
