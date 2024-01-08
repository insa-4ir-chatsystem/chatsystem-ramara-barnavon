package chatsystem.view;


import chatsystem.ChatSystem;
import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.exceptions.PseudoRejectedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GUI {

    //TODO:
    // Create one ChatView per contact
    // Update a ChatView everytime we receive/send a message
    // Convert local implementation to LAN

    private static final Logger LOGGER = LogManager.getLogger(GUI.class);
    private final ChatSystem CS;

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

        //Login.setLayout(new BoxLayout(Login, BoxLayout.PAGE_AXIS));
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

        ContactItem itemTest = new ContactItem(new Contact("pseudoTest", 12));
        contactListInnerPanel.add(itemTest);
        ContactItem itemTest2 = new ContactItem(new Contact("pseudoTest2", 2));
        contactListInnerPanel.add(itemTest2);
        ContactItem itemTest3 = new ContactItem(new Contact("pseudoTest2", 2));
        contactListInnerPanel.add(itemTest3);
        itemTest2.setOffline();

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

        // Create a split pane for contact list and chat history
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contactListPanel, EmptyChatHistoryPanel);
        //splitPane.setResizeWeight(0.1); // Adjust the divider location

        // Add the split pane and input panel to the frame
        Chatting.add(chattingTitle, BorderLayout.NORTH);
        Chatting.add(splitPane, BorderLayout.CENTER);
        Chatting.add(inputPanel, BorderLayout.SOUTH);
        Chatting.setVisible(true);
        Login.setVisible(true);

        frame.add(Login);
        frame.setSize(1400, 600);
        frame.setVisible(true);

        //MainVM.setViewOfFrame(frame, Sign)

        /** ======================    Action Listeners Implementation    ================================ */


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
                    MainVM.setViewOfFrame(frame, Chatting);
                }else{
                    loginInfo.setText("Please enter a pseudo");
                }
            } catch (PseudoRejectedException ex){
                loginInfo.setText("Someone already have this pseudo, please choose another one");
                pseudoField1.setText("");
            }

        });

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
                    contactListTitle.setText("Connected as " + CS.getMonContact().getPseudo());
                }else{
                    infoChangePseudo.setText("Please enter a pseudo"); //TODO: notify somewhere on the screen
                }
            } catch (PseudoRejectedException ex){
                infoChangePseudo.setText("Pseudo not available");
                pseudoFieldd.setText("");
            }



        });



    }


    public void start() {
        this.CS.start();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                createAndShowGUI();
                LOGGER.info("Showing gui");
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

    public static class ContactItem extends JPanel{
        JLabel pseudo;
        JLabel onlineMark;
        JPanel chat;
        Contact contact;

        public ContactItem(Contact contact){
            super(new FlowLayout());
            this.contact = contact;
            this.chat = new JPanel(new BorderLayout());
            this.pseudo = new JLabel(this.contact.getPseudo());
            this.onlineMark = new JLabel();
            
            this.setUpPanel();
        }

        private void setUpPanel(){

            onlineMark.setOpaque(true);
            onlineMark.setPreferredSize(new Dimension(10, 10));
            setOnline();

            this.add(pseudo);
            this.add(onlineMark);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }
            });
        }
        
        public JPanel getChat(){
            return this.chat;
        }

        public void setOnline(){
            onlineMark.setBackground(Color.GREEN);
        }

        public void setOffline(){
            onlineMark.setBackground(Color.RED);
        }


    }



}
