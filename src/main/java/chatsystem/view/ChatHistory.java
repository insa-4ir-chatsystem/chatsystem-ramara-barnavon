package chatsystem.view;

import chatsystem.database.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** A ChatHistory is used to display previous exchanges messages with a Contact */
public class ChatHistory extends JScrollPane { // Corresponding Contact necessary as attribute ?
    private JPanel panel = new JPanel();
    private JList<ChatMessage> messageList;

    public ChatHistory() {
        super();
        this.setViewportView(this.panel);
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));

        /*
        JTextArea textArea = new JTextArea("TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea TextArea ");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        panel.add(textArea);
         */

        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.getViewport().addComponentListener(new ResizeListener(this.getViewport().getSize(), panel));

    }

    public void addMessage(ChatMessage message) {
        //messageListModel.addElement(message);
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
