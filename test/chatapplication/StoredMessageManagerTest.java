package chatapplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit tests for Part 3 - StoredMessageManager
 * Covers all test cases from the assignment specification
 */
public class StoredMessageManagerTest {
    
    private StoredMessageManager manager;
    private List<Message> testMessages;
    
    // Test data from assignment
    private static final String MSG1_TEXT = "Did you get the cake?";
    private static final String MSG2_TEXT = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3_TEXT = "Yohoooo, I am at your gate.";
    private static final String MSG4_TEXT = "It is dinner time !";
    private static final String MSG5_TEXT = "Ok, I am leaving without you.";
    
    private static final String RECIPIENT1 = "+27834557896";
    private static final String RECIPIENT2 = "+27838884567";
    private static final String RECIPIENT3 = "+27834484567";
    private static final String RECIPIENT4 = "0838884567";
    private static final String RECIPIENT5 = "+27838884567"; // Same as RECIPIENT2
    
    @BeforeEach
    public void setUp() {
        manager = new StoredMessageManager();
        testMessages = new ArrayList<>();
        
        // Create test messages matching the assignment test data
        // Message 1: Sent
        Message m1 = new Message(1, RECIPIENT1, MSG1_TEXT);
        m1.performAction(0); // SENT
        testMessages.add(m1);
        manager.addSentMessage(MSG1_TEXT);
        manager.addMessageId(m1.getMessageId());
        manager.addMessageHash(m1.getMessageHash());
        
        // Message 2: Stored
        Message m2 = new Message(2, RECIPIENT2, MSG2_TEXT);
        m2.performAction(2); // STORED
        testMessages.add(m2);
        manager.addStoredMessage(MSG2_TEXT);
        manager.addMessageId(m2.getMessageId());
        manager.addMessageHash(m2.getMessageHash());
        
        // Message 3: Disregarded
        Message m3 = new Message(3, RECIPIENT3, MSG3_TEXT);
        m3.performAction(1); // DISREGARDED
        testMessages.add(m3);
        manager.addDisregardedMessage(MSG3_TEXT);
        manager.addMessageId(m3.getMessageId());
        manager.addMessageHash(m3.getMessageHash());
        
        // Message 4: Sent (Developer entry)
        Message m4 = new Message(4, RECIPIENT4, MSG4_TEXT);
        m4.performAction(0); // SENT
        testMessages.add(m4);
        manager.addSentMessage(MSG4_TEXT);
        manager.addMessageId(m4.getMessageId());
        manager.addMessageHash(m4.getMessageHash());
        
        // Message 5: Stored
        Message m5 = new Message(5, RECIPIENT5, MSG5_TEXT);
        m5.performAction(2); // STORED
        testMessages.add(m5);
        manager.addStoredMessage(MSG5_TEXT);
        manager.addMessageId(m5.getMessageId());
        manager.addMessageHash(m5.getMessageHash());
    }
    
    // ==================== TEST 1: Array Population ====================
    
    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        String[] sent = manager.getSentMessages();
        
        // Should contain messages 1 and 4 (the sent ones)
        assertEquals(MSG1_TEXT, sent[0]);
        assertEquals(MSG4_TEXT, sent[1]);
        assertEquals(2, manager.getSentCount());
    }
    
    @Test
    public void testDisregardedMessagesArrayCorrectlyPopulated() {
        String[] disregarded = manager.getDisregardedMessages();
        
        assertEquals(MSG3_TEXT, disregarded[0]);
        assertEquals(1, manager.getDisregardedCount());
    }
    
    @Test
    public void testStoredMessagesArrayCorrectlyPopulated() {
        String[] stored = manager.getStoredMessages();
        
        assertEquals(MSG2_TEXT, stored[0]);
        assertEquals(MSG5_TEXT, stored[1]);
        assertEquals(2, manager.getStoredCount());
    }
    
    @Test
    public void testMessageIdsArrayCorrectlyPopulated() {
        String[] ids = manager.getMessageIds();
        
        assertNotNull(ids[0]);
        assertNotNull(ids[1]);
        assertNotNull(ids[2]);
        assertNotNull(ids[3]);
        assertNotNull(ids[4]);
        assertEquals(5, manager.getIdCount());
    }
    
    @Test
    public void testMessageHashesArrayCorrectlyPopulated() {
        String[] hashes = manager.getMessageHashes();
        
        assertNotNull(hashes[0]);
        assertNotNull(hashes[1]);
        assertNotNull(hashes[2]);
        assertNotNull(hashes[3]);
        assertNotNull(hashes[4]);
        assertEquals(5, manager.getHashCount());
    }
    
    // ==================== TEST 2: Display Longest Message ====================
    
    @Test
    public void testDisplayLongestStoredMessage() {
        String result = manager.displayLongestStoredMessage();
        
        // Message 2 is the longest stored message
        assertTrue(result.contains(MSG2_TEXT));
        assertTrue(result.contains("Longest stored message"));
    }
    
    // ==================== TEST 3: Search by Message ID ====================
    
    @Test
    public void testSearchByMessageId() {
        // Get message 4's ID
        String msg4Id = testMessages.get(3).getMessageId();
        
        String result = manager.searchByMessageId(msg4Id, testMessages);
        
        assertTrue(result.contains(MSG4_TEXT));
        assertTrue(result.contains(RECIPIENT4));
    }
    
    @Test
    public void testSearchByMessageIdNotFound() {
        String result = manager.searchByMessageId("INVALID_ID", testMessages);
        
        assertTrue(result.contains("not found"));
    }
    
    // ==================== TEST 4: Search by Recipient ====================
    
    @Test
    public void testSearchMessagesByRecipient() {
        // Search for recipient +27838884567 (messages 2 and 5)
        String result = manager.searchMessagesByRecipient(RECIPIENT2, testMessages);
        
        assertTrue(result.contains(MSG2_TEXT));
        assertTrue(result.contains(MSG5_TEXT));
    }
    
    @Test
    public void testSearchMessagesByRecipientNotFound() {
        String result = manager.searchMessagesByRecipient("+27000000000", testMessages);
        
        assertTrue(result.contains("No messages found"));
    }
    
    // ==================== TEST 5: Delete by Message Hash ====================
    
    @Test
    public void testDeleteByMessageHash() {
        // Get message 2's hash (Test Message 2 from assignment)
        String msg2Hash = testMessages.get(1).getMessageHash();
        String msg2Text = testMessages.get(1).getMessageText();
        
        String result = manager.deleteByMessageHash(msg2Hash, testMessages);
        
        assertTrue(result.contains("successfully deleted"));
        assertTrue(result.contains(msg2Text));
        
        // Verify message was removed from list
        assertEquals(4, testMessages.size());
    }
    
    @Test
    public void testDeleteByMessageHashNotFound() {
        String result = manager.deleteByMessageHash("INVALID_HASH", testMessages);
        
        assertTrue(result.contains("not found"));
    }
    
    // ==================== TEST 6: Display Report ====================
    
    @Test
    public void testDisplayStoredMessagesReport() {
        String result = manager.displayStoredMessagesReport(testMessages);
        
        assertTrue(result.contains("STORED MESSAGES REPORT"));
        assertTrue(result.contains(MSG2_TEXT) || result.contains(MSG2_TEXT.substring(0, 20)));
        assertTrue(result.contains(MSG5_TEXT) || result.contains(MSG5_TEXT.substring(0, 20)));
    }
    
    // ==================== TEST 7: Display Sender & Recipient ====================
    
    @Test
    public void testDisplayStoredMessagesSenderRecipient() {
        String result = manager.displayStoredMessagesSenderRecipient("John_Doe", testMessages);
        
        // Should show stored messages (messages 2 and 5)
        assertTrue(result.contains("John_Doe"));
        assertTrue(result.contains(RECIPIENT2));
    }
    
    // ==================== TEST 8: JSON Reading (Optional) ====================
    
    @Test
    public void testReadStoredMessagesFromJson() {
        // This test would require a mock JSON file
        // For now, verify the method exists and doesn't crash with invalid path
        assertDoesNotThrow(() -> manager.readStoredMessagesFromJson("nonexistent.json"));
    }

    private void assertEquals(String MSG1_TEXT, String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void assertEquals(int i, int sentCount) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void assertNotNull(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void assertTrue(boolean contains) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
