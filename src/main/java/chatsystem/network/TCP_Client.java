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


public class TCP_Client {
    private Socket clientSocket;
    private PrintWriter out;


    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(String msg) throws IOException{
        out.println(msg);


    }

    public void stopConnection() throws IOException {

        out.close();
        clientSocket.close();
    }

}
