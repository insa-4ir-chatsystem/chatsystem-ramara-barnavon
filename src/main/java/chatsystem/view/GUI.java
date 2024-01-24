package chatsystem.view;


import chatsystem.controller.ChatSystem;
import chatsystem.model.contact_discovery.Contact;
import chatsystem.model.contact_discovery.ContactsManager;
import chatsystem.database.ChatMessage;
import chatsystem.exceptions.PseudoRejectedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.*;

/** This class creates all the Objects to be displayed and adds Action Listeners to them */
public class GUI {

    private static final Logger LOGGER = LogManager.getLogger(GUI.class);
    private final ChatSystem chatSystem;
    private Contact currentContact;

    public GUI(ChatSystem chatSystem) {
        this.chatSystem = chatSystem;
    }

    private void createAndShowGUI() {

        // Create and set up the window.
        JFrame frame = new JFrame("Clavard'App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ViewManager viewManager = new ViewManager();



        /** Creating pages */

        /** ---------------------- Logging in page ------------------------ */

        JPanel Login = viewManager.createPanelView(); // First page to be displayed to choose a pseudo
        Login.setLayout(new BoxLayout(Login, BoxLayout.Y_AXIS));

            JLabel loginTitle = new JLabel("Welcome to Clavard'App\n Please choose your pseudo ", JLabel.CENTER);
            loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel loginInfo = new JLabel("");
            loginInfo.setForeground(Color.RED);

            JPanel pseudoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //For correct component sizing
            JTextField pseudoField1 = new JTextField(30);

            pseudoPanel.add(pseudoField1);
            pseudoField1.setPreferredSize(new Dimension(100, 30));
            JButton buttonStartChatting = new JButton("Start chatting");

        Login.add(loginTitle);
        Login.add(pseudoPanel);
        Login.add(loginInfo);
        Login.add(buttonStartChatting);


        /** ---------------------- Chatting page ------------------------ */


        JPanel Chatting = viewManager.createPanelView();
        Chatting.setLayout(new BorderLayout());

            JLabel chattingTitle = new JLabel("Start chatting with your contacts", JLabel.CENTER);

            // Create a split pane for contact list and chat history
            JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);

                JPanel contactListPanel = new JPanel(new BorderLayout());

                    JLabel contactListTitle = new JLabel();
                    JPanel contactListInnerPanel = new JPanel();
                    contactListInnerPanel.setLayout(new BoxLayout(contactListInnerPanel, BoxLayout.Y_AXIS));
                    JScrollPane contactScrollPane = new JScrollPane(contactListInnerPanel);

                    JPanel contactInputPanel = new JPanel(new FlowLayout());

                        JTextField pseudoFieldd = new JTextField(20);
                        JButton changePseudoButton = new JButton("Change Pseudonym");

                    contactInputPanel.add(pseudoFieldd);
                    contactInputPanel.add(changePseudoButton);

                contactListPanel.add(contactListTitle, BorderLayout.NORTH);
                contactListPanel.add(contactScrollPane, BorderLayout.CENTER);
                contactListPanel.add(contactInputPanel, BorderLayout.SOUTH);
                contactListPanel.setMinimumSize(new Dimension(450, 400));

                ChatHistory chatHistory = new ChatHistory();

            centerSplitPane.setLeftComponent(contactListPanel);
            centerSplitPane.setRightComponent(chatHistory);



            JPanel messageInputPanel = new JPanel(new BorderLayout());

                JLabel infoChangePseudo = new JLabel("");
                infoChangePseudo.setForeground(Color.RED);

                JTextField messageField = new JTextField(20);
                JButton sendMessageButton = new JButton("Send");

            messageInputPanel.add(infoChangePseudo, BorderLayout.NORTH);
            messageInputPanel.add(messageField, BorderLayout.CENTER);
            messageInputPanel.add(sendMessageButton, BorderLayout.EAST);




        // Add all the components to the main panel
        Chatting.add(chattingTitle, BorderLayout.NORTH);
        Chatting.add(centerSplitPane, BorderLayout.CENTER);
        Chatting.add(messageInputPanel, BorderLayout.SOUTH);




        viewManager.setViewOfFrame(frame, Login);
        frame.setSize(900, 600);
        frame.setResizable(false);





        /** ======================    Adding observers to ContactManager    =============================== */

        /** Observer responsible for updating the contact list */
        this.chatSystem.getContactsManager().addObserver(new ContactsManager.Observer() {

            /** Adds a contact to the list whenever a contact is discovered on the network */
            @Override
            public void addContact(Contact contact) {
                ContactItem contactItem = new ContactItem(contact);
                contactListInnerPanel.add(contactItem);
                contactItem.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        currentContact = contact;
                        chattingTitle.setText("Chatting with " + currentContact.getPseudo());
                        try {
                            ArrayList<ChatMessage> messList = chatSystem.getChatHistoryManager().getHistoryOf(currentContact.getId(), chatSystem.getMonContact().getId());
                            chatHistory.flushHistory();
                            chatHistory.loadHistory(messList, chatSystem.getMonContact().getId());
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                contactListInnerPanel.revalidate();
                contactListInnerPanel.repaint();
            }

            /** Updates a contact's pseudo or online mark whenever it changes */
            @Override
            public void updateContact(Contact contact) {
                Component[] ContactComponentList = contactListInnerPanel.getComponents();
                for(Component cc : ContactComponentList){
                    if (cc instanceof ContactItem){
                        ContactItem ci = (ContactItem) cc;
                        if(contact.equals(currentContact)){
                            currentContact = contact;
                            chattingTitle.setText("Chatting with " + currentContact.getPseudo());
                        }
                        if(ci.getContact().equals(contact)){
                            ci.setContact(contact);
                            ci.updateContactItem();
                            break;
                        }
                    }
                }


                contactListInnerPanel.revalidate();
                contactListInnerPanel.repaint();
            }
        });


        /** ======================    Adding observers to TcpServer    =============================== */

        /** Adds a new message in the chat history when received */
        this.chatSystem.getTcpServer().addObserver((received, ipSender) -> {
            int otherID = this.chatSystem.getContactsManager().searchConnectedContactByIP(ipSender.getHostAddress()).getId();
            int myId = this.chatSystem.getMonContact().getId();
            try {
                this.chatSystem.getChatHistoryManager().insertMessage(otherID, myId, received);
                if(this.currentContact != null && otherID == this.currentContact.getId()){
                    chatHistory.flushHistory();
                    chatHistory.loadHistory(chatSystem.getChatHistoryManager().getHistoryOf(otherID, myId), myId);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        /** ======================    Action Listeners Implementation    ================================ */



        /** button to choose pseudo and start chatting */
        buttonStartChatting.addActionListener(e -> {
            String askedPseudo = pseudoField1.getText();
            try {
                if(!askedPseudo.isEmpty()){
                    chatSystem.choosePseudo(askedPseudo);
                    chatSystem.getMonContact().setPseudo(askedPseudo);
                    chatSystem.getUpdateContactListThread().setName("UCT Thread - " + askedPseudo);
                    chatSystem.getContactsManager().setMonContact(chatSystem.getMonContact());
                    LOGGER.trace("Chatsystem " + askedPseudo + " started correctly");
                    contactListTitle.setText("Connected as " + chatSystem.getMonContact().getPseudo());
                    loginInfo.setText("");
                    viewManager.setViewOfFrame(frame, Chatting);
                }else{
                    loginInfo.setText("Please enter a pseudo");
                }
            } catch (PseudoRejectedException ex){
                loginInfo.setText("Someone already have this pseudo, please choose another one");
                pseudoField1.setText("");
            }

        });

        /** button to change pseudo */
        changePseudoButton.addActionListener(e -> {
            String askedPseudo = pseudoFieldd.getText();
            try {
                if(!askedPseudo.isEmpty()){
                    chatSystem.changePseudo(askedPseudo);
                    chatSystem.getMonContact().setPseudo(askedPseudo);
                    chatSystem.getUpdateContactListThread().setName("UCT Thread - " + askedPseudo);
                    chatSystem.getContactsManager().setMonContact(chatSystem.getMonContact());
                    LOGGER.trace("Chatsystem " + askedPseudo + " changed correctly");
                    pseudoFieldd.setText("");
                    infoChangePseudo.setText("");
                    contactListTitle.setText("Connected as " + chatSystem.getMonContact().getPseudo());
                }else{
                    infoChangePseudo.setText("Please enter a pseudo");
                }
            } catch (PseudoRejectedException ex){
                infoChangePseudo.setText("Pseudo not available");
                pseudoFieldd.setText("");
            }

        });

        /** Button to send a message to selected contact */
        sendMessageButton.addActionListener(e -> {
            String message = messageField.getText();
            messageField.setText("");
            try {
                if (message.isEmpty()) {
                    infoChangePseudo.setText("Please enter a message");
                } else if (this.currentContact == null) {
                    infoChangePseudo.setText("Please choose a contact to chat with");
                } else if (!this.currentContact.isOnline()) {
                    infoChangePseudo.setText("Your contact is offline !");
                } else {
                    int myID = this.chatSystem.getMonContact().getId();
                    int otherID = this.currentContact.getId();
                    this.currentContact.sendMessageTCP(message, this.chatSystem.PORT_TCP);
                    this.chatSystem.getChatHistoryManager().insertMessage(myID, otherID, message);
                    chatHistory.addSentMessage(new ChatMessage(myID, otherID, message, LocalDateTime.now()));
                }
            }catch(Exception ex){
                throw new RuntimeException(ex);
            }
        });


        /** Closes the ChatSystem when the window is closed */
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chatSystem.closeChat();
                System.exit(0);
            }
        });

        frame.setVisible(true);

    }


    /** Starts the ChatSystem and shows the GUI */
    public void start() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                createAndShowGUI();
                chatSystem.start();
                LOGGER.info("Showing GUI");

            }
        });

    }

}
