package chatsystem.ContactDiscoveryLib;

import chatsystem.ContactDiscoveryLib.Contact;
import chatsystem.ContactDiscoveryLib.ContactsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class ContactsManagerTest {

    private ContactsManager contactsManager;
    private Contact testContact;
    private Contact testContact2;

    @BeforeEach
    public void setUp() {
        contactsManager = new ContactsManager();
        testContact = new Contact("Test", 1);
        testContact2 = new Contact("test2", 2);
    }

    @Test
    public void testUpdateContact() {
        setUp();


        // Test de mise à jour du TTL d'un contact existant
        testContact.setTTL(3);
        contactsManager.updateContact(testContact);
        contactsManager.updateContact(testContact2);
        assertEquals(4, contactsManager.searchContactByID(testContact.getId()).getTTL());
        assertEquals(4, contactsManager.searchContactByID(testContact2.getId()).getTTL());
    }

    @Test
    public void testSearchContactById() {
        // Test de recherche d'un contact
        contactsManager.updateContact(testContact);
        assertEquals(testContact, contactsManager.searchContactByID(testContact.getId()));
    }

    @Test
    public void testSearchContactByPseudo() {
        contactsManager.updateContact(testContact);
        assertEquals(testContact, contactsManager.searchContactByPseudo("Test"));
    }

    @Test
    public void testDecreaseTTL() {
        contactsManager.updateContact(testContact);
        contactsManager.updateContact(testContact2);
        testContact.setTTL(0);
        testContact2.setTTL(3);
        contactsManager.decreaseTTL();
        assertFalse(contactsManager.searchContactByID(testContact.getId()).getOnline()); // testContact should be removed from list
        assertEquals(2,testContact2.getTTL()); //TTL should be decreased
    }

}
