package chatsystem.view;

import chatsystem.ContactDiscoveryLib.Contact;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A ContactItem is the element showing the contact in the list of Contacts */
public class ContactItem extends JPanel {
    JLabel pseudo;
    JLabel onlineMark;
    JPanel chat; // corresponding chat view
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
        setBackground(Color.WHITE);

        this.add(pseudo);
        this.add(onlineMark);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setBackground(Color.WHITE);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                setBackground(Color.LIGHT_GRAY);
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
