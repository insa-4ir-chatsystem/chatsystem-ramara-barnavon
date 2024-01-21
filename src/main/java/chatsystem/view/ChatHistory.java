package chatsystem.view;

import chatsystem.database.ChatMessage;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/** A ChatHistory is used to display previous and current exchanged messages with a Contact */
public class ChatHistory extends JScrollPane {
    private JPanel panel = new JPanel();
    private final Color LIGHT_BLUE = new Color(0, 246, 255); // Color for sent messages
    private final Color LIGHT_GRAY = Color.LIGHT_GRAY; // Color for received messages


    public ChatHistory() {
        super();
        this.setViewportView(this.panel);
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }

    /** Adds a message sent by the local chatSystem in the conversation */
    public void addSentMessage(ChatMessage message){
        JTextField timeArea = new JTextField(message.dateFormatted());
        timeArea.setEditable(false);
        timeArea.setHorizontalAlignment(JTextField.CENTER);
        timeArea.setMaximumSize(new Dimension(10000, 15));

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);


        textArea.setText(message.content());
        try {
            Highlighter highlighter = textArea.getHighlighter();
            Highlighter.HighlightPainter painter =
                    new DefaultHighlighter.DefaultHighlightPainter(LIGHT_BLUE);
            highlighter.addHighlight(0, textArea.getText().length(), painter);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }


        panel.add(timeArea);
        panel.add(textArea);
        panel.revalidate();
        panel.repaint();
        this.revalidate();
        this.repaint();
    }

    /** Adds a message sent by the remote chatSystem in the conversation */
    public void addReceivedMessage(ChatMessage message){

        JTextField timeArea = new JTextField(message.dateFormatted());
        timeArea.setEditable(false);
        timeArea.setHorizontalAlignment(JTextField.CENTER);
        timeArea.setMaximumSize(new Dimension(10000, 15));

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);


        textArea.setText(message.content());
        try {
            Highlighter highlighter = textArea.getHighlighter();
            Highlighter.HighlightPainter painter =
                    new DefaultHighlighter.DefaultHighlightPainter(LIGHT_GRAY);
            highlighter.addHighlight(0, textArea.getText().length(), painter);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }


        panel.add(timeArea);
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
    public void loadHistory(ArrayList<ChatMessage> messageList, int localID){
        for(ChatMessage mess : messageList){
            if(mess.senderId() == localID){
                addSentMessage(mess);
            }else{
                addReceivedMessage(mess);
            }
        }
    }

}
