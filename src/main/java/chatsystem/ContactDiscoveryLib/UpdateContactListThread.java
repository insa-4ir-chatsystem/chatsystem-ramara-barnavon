package chatsystem.ContactDiscoveryLib;

import chatsystem.network.UDP_Client;

import java.io.IOException;
import java.net.InetAddress;

/** This class is responsible for updating the contact list of the ChatSystem */
public class UpdateContactListThread extends Thread {

    private InetAddress broadcastAddress;
    private ContactsManager contactsManager;

    public UpdateContactListThread(InetAddress broadcastAddress, ContactsManager contactsManager) {
        this.broadcastAddress = broadcastAddress;
        this.contactsManager = contactsManager;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {

            try { // asks for Contact information
                UDP_Client.send_DECO(broadcastAddress, ChatSystem.PORT_UDP);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                this.interrupt();
            }
            contactsManager.decreaseTTL();
        }
    }
}
