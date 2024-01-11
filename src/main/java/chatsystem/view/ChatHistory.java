package chatsystem.view;

import chatsystem.database.ChatMessage;

import javax.swing.*;
import java.awt.*;

/** A ChatHistory is used to display previous exchanges messages with a Contact */
public class ChatHistory extends JPanel { // Corresponding Contact necessary as attribute ?
    private DefaultListModel<ChatMessage> messageListModel;
    private JList<ChatMessage> messageList;

    public ChatHistory() {
        setLayout(new BorderLayout());

        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);

        JScrollPane scrollPane = new JScrollPane(messageList);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(ChatMessage message) {
        messageListModel.addElement(message);
    }
}
