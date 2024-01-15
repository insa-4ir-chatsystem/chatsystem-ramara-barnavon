package chatsystem.view;

import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.database.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** A ChatHistory is used to display previous and current exchanged messages with a Contact */
public class ChatHistory extends JScrollPane { // Corresponding Contact necessary as attribute ?
    private JPanel panel = new JPanel();


    public ChatHistory() {
        super();
        this.setViewportView(this.panel);
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));


        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.getViewport().addComponentListener(new ResizeListener(this.getViewport().getSize(), panel));

    }

    /** Adds a message sent by the local chatSystem in the conversation */
    public void addSentMessage(ChatMessage message){
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textArea.setBackground(Color.BLUE);
        panel.add(textArea);
        panel.revalidate();
        panel.repaint();
        this.revalidate();
        this.repaint();
    }

    /** Adds a message sent by the remote chatSystem in the conversation */
    public void addReceivedMessage(ChatMessage message){
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textArea.setBackground(Color.LIGHT_GRAY);
        panel.add(textArea);
        panel.revalidate();
        panel.repaint();
        this.revalidate();
        this.repaint();

    }

    /** flushes all the conversation */
    public void flushHistory(){
        this.panel.removeAll();
        panel.revalidate();
        panel.repaint();
        this.revalidate();
        this.repaint();
    }

    /** Loads a conversation from the DataBase */
    public void getHistoryOf(Contact me, Contact other){
        //TODO : Get all the messages exchanged between the contacts and add them in the right order in the panel
    }


    /** Inner class for resizing the view everytime the ChatHistory is resized */
    class ResizeListener extends ComponentAdapter {
        Dimension newDimension;
        Component compToResize;

        public ResizeListener(Dimension d, Component c){
            this.newDimension = d;
            this.compToResize = c;
        }
        public void componentResized(ComponentEvent e) {
            this.compToResize.setSize(this.newDimension);
            this.compToResize.setPreferredSize(this.newDimension);
            this.compToResize.setMaximumSize(this.newDimension);
            this.compToResize.setMinimumSize(this.newDimension);
        }
    }
}
