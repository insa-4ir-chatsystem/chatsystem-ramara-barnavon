package chatsystem.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TCP_Server extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(TCP_Server.class);
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    //private final List<TCP_Server.Observer> observers = new ArrayList<>();

    /**
     * Interface that observe of the TCP server must implement.
     */


    public TCP_Server(int port) throws SocketException, UnknownHostException,IOException {
           serverSocket = new ServerSocket(port);
    }

    public void run() {
        while(!this.isInterrupted()) {

            try {
                clientSocket = serverSocket.accept(); // TODO: créer un thread pour toutes les connections entrantes

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = in.readLine();
                LOGGER.debug(message);



            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }


    }
    public void close() throws IOException{
        /** close everything */

            in.close();
            clientSocket.close();
            serverSocket.close();
            this.interrupt();
    }

}
