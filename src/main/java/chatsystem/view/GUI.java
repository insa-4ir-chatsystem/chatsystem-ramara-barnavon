package chatsystem.view;


import chatsystem.ChatSystem;
import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.ContactDiscoveryLib.ContactsManager;
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

public class GUI {

    //TODO:
    // Create one ChatView per contact
    // Update a ChatView everytime we receive/send a message
    // Convert local implementation to LAN

    private static final Logger LOGGER = LogManager.getLogger(GUI.class);
    private final ChatSystem CS;
    private Contact currentContact;

    public GUI(ChatSystem cs) {
        this.CS = cs;
    }

    private void createAndShowGUI() {

        //final int[] UID = {-1};

        //TODO : Mettrre a jour le GUI quand On reçoit des messages
        ViewManager MainVM = new ViewManager();
        ViewManager ChatVM = new ViewManager(); // To display the right Chat instance

        // Create and set up the window.
        JFrame frame = new JFrame("Clavard'App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** Creation and initialization of the main views */
        JPanel Login = MainVM.createPanelView(); // First page to be displayed to choose a pseudo
        JPanel Chatting = MainVM.createPanelView(); // Page to chat with contacts

        Login.setLayout(new BoxLayout(Login, BoxLayout.Y_AXIS));
        Chatting.setLayout(new BorderLayout());



        /** Creation of ActionListeners to switch the view */
        ChangeView ShowLogin = new ChangeView(frame, MainVM, Login);
        ChangeView ShowChatting = new ChangeView(frame, MainVM, Chatting);





        /** Creating pages */

        /** ---------------------- Logging in page ------------------------ */

        JLabel loginTitle = new JLabel("Welcome to Clavard'App\n Please choose your pseudo ", JLabel.CENTER);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel loginInfo = new JLabel("");
        loginInfo.setForeground(Color.RED);

        JPanel pseudoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField pseudoField1 = new JTextField(30);

        pseudoPanel.add(pseudoField1);
        pseudoField1.setPreferredSize(new Dimension(100, 30));
        JButton buttonStartChatting = new JButton("Start chatting");



        Login.add(loginTitle);
        Login.add(pseudoPanel);
        Login.add(loginInfo);
        Login.add(buttonStartChatting);

        /*
        Login.add(loginTitle, BorderLayout.NORTH);
        Login.add(pseudoPanel, BorderLayout.CENTER);
        Login.add(buttonStartChatting, BorderLayout.SOUTH);
        */

        /** ---------------------- Chatting page ------------------------ */

        JLabel chattingTitle = new JLabel("Start chatting with your contacts", JLabel.CENTER);

        JPanel contactListPanel = new JPanel(new BorderLayout());
        JPanel EmptyChatHistoryPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel contactInputPanel = new JPanel(new FlowLayout()); // Panel for contact input

        JPanel contactListInnerPanel = new JPanel();
        contactListInnerPanel.setLayout(new BoxLayout(contactListInnerPanel, BoxLayout.Y_AXIS));

        // Add components to the contact list panel
        JScrollPane contactScrollPane = new JScrollPane(contactListInnerPanel);

        JLabel contactListTitle = new JLabel();
        contactListPanel.add(contactListTitle, BorderLayout.NORTH);
        contactListPanel.add(contactScrollPane, BorderLayout.CENTER);

        /** fake contacts for testing purposes
        ContactItem itemTest = new ContactItem(new Contact("pseudoTest", 12));
        contactListInnerPanel.add(itemTest);
        ContactItem itemTest2 = new ContactItem(new Contact("pseudoTest2", 22));
        contactListInnerPanel.add(itemTest2);
        ContactItem itemTest3 = new ContactItem(new Contact("pseudoTest2", 23));
        contactListInnerPanel.add(itemTest3);
        itemTest2.setOffline();
         */

        /** fake ChatHistory for testing purposes
        ChatHistory ChatHistory1 = new ChatHistory();
        ChatHistory1.addMessage(new ChatMessage(0, 1, "message 1", LocalDateTime.now()));
        ChatHistory1.addMessage(new ChatMessage(1, 0, "message 2", LocalDateTime.now()));
        ChatHistory1.addMessage(new ChatMessage(1, 0, "messagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessagemessage 2", LocalDateTime.now()));
        */
        /** //////////////////////////////////////////////////////////////////////////////////////////////////////////*/



        /** //////////////////////////////////////////////////////////////////////////////////////////////////////////*/

        ChatHistory CH = new ChatHistory();

        // Add components to the chat history panel
        JTextArea chatHistory = new JTextArea();
        JScrollPane chatScrollPane = new JScrollPane(chatHistory);
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);
        chatHistory.setWrapStyleWord(true); // go to next line at the end of the word
        EmptyChatHistoryPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Add components to the input panel for sending messages
        JTextField messageField = new JTextField(20);
        JButton sendMessageButton = new JButton("Send");
        JLabel infoChangePseudo = new JLabel("");
        infoChangePseudo.setForeground(Color.RED);
        inputPanel.add(infoChangePseudo, BorderLayout.NORTH);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendMessageButton, BorderLayout.EAST);

        // Add components to the input panel for changing pseudonym
        JTextField pseudoFieldd = new JTextField(20);
        JButton changePseudoButton = new JButton("Change Pseudonym");
        contactInputPanel.add(pseudoFieldd);
        contactInputPanel.add(changePseudoButton);
        contactListPanel.add(contactInputPanel, BorderLayout.SOUTH);
        contactListPanel.setMinimumSize(new Dimension(450, 400));

        // Create a split pane for contact list and chat history
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true, contactListPanel, CH);
        //splitPane.setResizeWeight(0.3); // Adjust the divider location
        // Add the split pane and input panel to the frame
        Chatting.add(chattingTitle, BorderLayout.NORTH);
        Chatting.add(splitPane, BorderLayout.CENTER);
        Chatting.add(inputPanel, BorderLayout.SOUTH);

        //Chatting.setVisible(true);
        //Login.setVisible(true);

        MainVM.setViewOfFrame(frame, Login);

        //frame.add(Login);
        frame.setSize(800, 600);
        frame.setResizable(false);

        //MainVM.setViewOfFrame(frame, Sign)




        /** ======================    Adding observers to ContactManager    =============================== */


        LOGGER.debug("BEFORE Adding observer to CM");
        this.CS.getCm().addObserver(new ContactsManager.Observer() {
            @Override
            public void addContact(Contact contact) {
                ContactItem contactItem = new ContactItem(contact);
                contactListInnerPanel.add(contactItem);
                LOGGER.debug("INTO ADD OBSERVER");
                contactItem.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        currentContact = contact;
                        try {
                            ArrayList<ChatMessage> messList = CS.getChatHistoryManager().getHistoryOf(currentContact.getId(), CS.getMonContact().getId());
                            CH.flushHistory();
                            CH.loadHistory(messList, CS.getMonContact().getId());
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });


                contactListInnerPanel.revalidate();
                contactListInnerPanel.repaint();
            }

            @Override
            public void updateContact(Contact contact) {
                Component[] ContactComponentList = contactListInnerPanel.getComponents();
                for(Component cc : ContactComponentList){
                    if (cc instanceof ContactItem){
                        ContactItem ci = (ContactItem) cc;

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
        LOGGER.debug("AFTER Adding observer to CM");

        /** ======================    Adding observers to TcpServer    =============================== */

        this.CS.getTcpServer().addObserver((received, ipSender) -> {
            int otherID = this.CS.getCm().searchContactByIP(ipSender.getHostAddress()).getId();
            int myId = this.CS.getMonContact().getId();
            try {
                this.CS.getChatHistoryManager().insertMessage(otherID, myId, received);
                if(this.currentContact != null && otherID == this.currentContact.getId()){
                    CH.addReceivedMessage(new ChatMessage(otherID, myId, received, LocalDateTime.now()));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        /** ======================    Action Listeners Implementation    ================================ */

        /** button to choose pseudo and start chatting */

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CS.closeTcpServer();
                System.exit(0);
            }
        });

        buttonStartChatting.addActionListener(e -> {
            String askedPseudo = pseudoField1.getText();
            try {
                if(!askedPseudo.isEmpty()){
                    CS.choosePseudo(askedPseudo);
                    CS.getMonContact().setPseudo(askedPseudo);
                    CS.getUCT().setName("UCT Thread - " + askedPseudo);
                    CS.getCm().setMonContact(CS.getMonContact());
                    LOGGER.trace("Chatsystem " + askedPseudo + " started correctly");
                    contactListTitle.setText("Connected as " + CS.getMonContact().getPseudo());
                    loginInfo.setText("");
                    MainVM.setViewOfFrame(frame, Chatting);
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
                    CS.changePseudo(askedPseudo);
                    CS.getMonContact().setPseudo(askedPseudo);
                    CS.getUCT().setName("UCT Thread - " + askedPseudo);
                    CS.getCm().setMonContact(CS.getMonContact());
                    LOGGER.trace("Chatsystem " + askedPseudo + " changed correctly");
                    pseudoFieldd.setText("");
                    infoChangePseudo.setText("");
                    contactListTitle.setText("Connected as " + CS.getMonContact().getPseudo());
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
                } else {
                    int myID = this.CS.getMonContact().getId();
                    int otherID = this.currentContact.getId();
                    this.currentContact.sendMessageTCP(message, this.CS.PORT_TCP_SERVEUR);
                    this.CS.getChatHistoryManager().insertMessage(myID, otherID, message);
                    CH.addSentMessage(new ChatMessage(myID, otherID, message, LocalDateTime.now()));
                }
            }catch(Exception ex){
                throw new RuntimeException(ex);
            }
            // TODO: get sender id and receiver's
        });

        frame.setVisible(true);

    }


    public void start() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                createAndShowGUI();
                CS.start();
                LOGGER.info("Showing gui");
                // TODO:
                //  Boucle d'update de tous les composants graphiques ?
                //  Ex : si un contact n'est plus en ligne, changer sa pastille

            }
        });

    }


    /** A few ActionListeners definitions */

    /** This ActionListener changes the view of the current JFrame */
    public static class ChangeView implements ActionListener {
        private JPanel View;
        private ViewManager VM;
        private JFrame frame;

        public ChangeView(JFrame frame, ViewManager VM, JPanel View) {
            this.View = View;
            this.VM = VM;
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            VM.setViewOfFrame(frame, View);
        }

    }

    public static class ChangeActiveChat implements ActionListener {
        private JPanel View;
        private ViewManager VM;
        private JFrame frame;

        public ChangeActiveChat(JFrame frame, ViewManager VM, JPanel View) {
            this.View = View;
            this.VM = VM;
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            VM.setViewOfFrame(frame, View);
        }

    }






}
