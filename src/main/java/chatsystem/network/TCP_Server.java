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
    private final List<TCP_Server.Observer> observers = new ArrayList<>();


    //private final List<TCP_Server.Observer> observers = new ArrayList<>();

    /**
     * Interface that observe of the TCP server must implement.
     */


    public TCP_Server(int port) throws SocketException, UnknownHostException,IOException {
           serverSocket = new ServerSocket(port);
    }

    public interface Observer {
        /** Method that is called each time a message is received. */
        void handle(String received, InetAddress ipSender);
    }
    public void addObserver(TCP_Server.Observer obs) {
        synchronized (this.observers) {
            this.observers.add(obs);
        }
    }


    public void run() {
        while(!this.isInterrupted()) {

            try {
                clientSocket = serverSocket.accept();


                // TODO: créer un thread pour toutes les connections entrantes
                //Todo : Il faut avoir l'id du sender
                TCP_Session thread_session = new TCP_Session(clientSocket);
                /** Lorsque j'observe un de mes TCP_Session remonter un message, je le remonte moi-même à ChatSystem*/
                thread_session.addObserver((received, ipSender ) -> { synchronized (this.observers) {

                    for (TCP_Server.Observer obs : this.observers) {
                       obs.handle(received, ipSender);
                    }
                }});
                thread_session.start();




            } catch (IOException e) {
                if (e.getMessage().contains("Socket closed")){
                    LOGGER.debug("socket TCP fermé");
                }else{
                    throw new RuntimeException(e);
                }
            }


        }


    }
    public void close() throws IOException{
        //TODO:Faire en sorte de fermer tous les thread sessions sans les stocker
        /** close everything */
            serverSocket.close();
            this.interrupt();
    }

}
