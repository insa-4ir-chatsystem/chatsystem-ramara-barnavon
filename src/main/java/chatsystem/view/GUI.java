package chatsystem.view;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;

public class GUI {

    private static final Logger LOGGER = LogManager.getLogger(GUI.class);
    private static void createAndShowGUI() {

        final int[] UID = {-1};
        final Color GREEN = new Color(60, 252, 60, 255);
        final Color RED = new Color(255, 32, 32, 255);


        ViewManager MainVM = new ViewManager();
        ViewManager ChatVM = new ViewManager();

        // Create and set up the window.
        JFrame frame = new JFrame("Clavard'App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** Creation and initialization of the main views */
        JPanel Login = MainVM.createPanelView(); // First page to be displayed to choose a pseudo
        JPanel Chatting = MainVM.createPanelView(); // Page to chat with contacts

        Login.setLayout(new BoxLayout(Login, BoxLayout.PAGE_AXIS));
        Chatting.setLayout(new BoxLayout(Login, BoxLayout.PAGE_AXIS));



        /** Creation of ActionListeners to switch the view */
        ChangeView ShowLogin = new ChangeView(frame, MainVM, Login);
        ChangeView ShowChatting = new ChangeView(frame, MainVM, Chatting);





        /** Creating pages */

        /** ---------------------- Logging in page ------------------------ */

        JLabel loginTitle = new JLabel("Welcome to Clavard'App\n Please choose your pseudo ", JLabel.CENTER);
        JLabel loginInfo = new JLabel("");
        JTextField pseudoField1 = new JTextField();
        JButton buttonStartChatting= new JButton("Start chatting");
        // buttonStartChatting.addActionListener(choosePseudo);

        Login.add(loginTitle);
        Login.add(pseudoField1);
        Login.add(buttonStartChatting);
        Login.add(loginInfo);

        /** ---------------------- Chatting page ------------------------ */

        JLabel chattingTitle = new JLabel("Start chatting with your contacts", JLabel.CENTER);

        //TODO: verifier les definitions du gui
        // Create panels for contact list, chat history, and input section
        JPanel contactListPanel = new JPanel(new BorderLayout());
        JPanel chatHistoryPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel contactInputPanel = new JPanel(new FlowLayout()); // Panel for contact input

        // Add components to the contact list panel
        contactListPanel.add(new JLabel("Contact List"), BorderLayout.NORTH);
        // Add your contact list components here

        // Add components to the chat history panel
        JTextArea chatHistory = new JTextArea();
        JScrollPane chatScrollPane = new JScrollPane(chatHistory);
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);
        chatHistory.setWrapStyleWord(true);
        chatHistoryPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Add components to the input panel for sending messages
        JTextField messageField = new JTextField(20);
        JButton sendMessageButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendMessageButton, BorderLayout.EAST);

        // Add components to the input panel for changing pseudonym
        JTextField pseudoFieldd = new JTextField(20);
        JButton changePseudoButton = new JButton("Change Pseudonym");
        contactInputPanel.add(pseudoFieldd);
        contactInputPanel.add(changePseudoButton);
        contactListPanel.add(contactInputPanel, BorderLayout.SOUTH);

        // Create a split pane for contact list and chat history
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contactListPanel, chatHistoryPanel);
        splitPane.setResizeWeight(0.2); // Adjust the divider location

        // Add the split pane and input panel to the frame
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);


        frame.setSize(800, 600);
        frame.setVisible(true);

        //MainVM.setViewOfFrame(frame, Sign)
    }


    public static void start() {
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

    /** This ActionListener changes the form of the current JPanel */
    public static class ChangeForm implements ActionListener {
        private JPanel View;
        private ViewManager VM;
        private Component form;
        private int index;

        public ChangeForm(Component form, ViewManager VM, JPanel View, int index) {
            this.View = View;
            this.VM = VM;
            this.form = form;
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            View.remove(this.index);
            VM.setView(form);
            View.add(form, this.index);
            View.revalidate();
            View.repaint();
        }

    }

    /** This ActionListener changes the label of the current JPanel */
    public static class ChangeLabel implements ActionListener {
        private JPanel View;
        private ViewManager VM;
        private Component label;
        private int index;

        public ChangeLabel(Component label, ViewManager VM, JPanel View, int index) {
            this.View = View;
            this.VM = VM;
            this.label = label;
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            View.remove(this.index);
            VM.setView(label);
            View.add(label, this.index);
            View.revalidate();
            View.repaint();
        }

    }


}
