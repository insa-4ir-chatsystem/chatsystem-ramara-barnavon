package chatsystem.ContactDiscoveryLib;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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



        // Test de mise à jour du TTL d'un contact existant
        testContact.setTTL(3);
        contactsManager.updateContact(testContact);
        contactsManager.updateContact(testContact2);
        assertEquals(4, contactsManager.search_contact_by_id(testContact.getId()).getTTL());
        assertEquals(4, contactsManager.search_contact_by_id(testContact2.getId()).getTTL());
    }

    @Test
    public void testSearchContactById() {
        // Test de recherche d'un contact
        contactsManager.updateContact(testContact);
        assertEquals(testContact, contactsManager.search_contact_by_id(testContact.getId()));
    }

    @Test
    public void testSearchContactByPseudo() {
        contactsManager.updateContact(testContact);
        assertEquals(testContact, contactsManager.search_contact_by_pseudo("Test"));
    }

    @Test
    public void testDecreaseTTL() {
        contactsManager.updateContact(testContact);
        contactsManager.updateContact(testContact2);
        testContact.setTTL(0);
        testContact2.setTTL(3);
        contactsManager.decreaseTTL();
        assertTrue(contactsManager.search_contact_by_id(testContact.getId()) == null); // testContact should be removed from list
        assertEquals(2,testContact2.getTTL()); //TTL should be decreased
    }

}
