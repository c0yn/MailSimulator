package mail.storage;

import mail.Message;
import mail.filters.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void getUserNameTest() {
        User user = new User("1");
        assertEquals("1", user.getUserName());
    }

    @Test
    void getInboxTest() {
        User user = new User("1");
        assertTrue(user.getInbox().isEmpty());
        Message message = new Message("Title", "Text", new User("2"), user);
        user.addReceivedMessage(message);
        assertEquals(message, user.getInbox().getFirst());
    }

    @Test
    void getOutboxTest() {
        User user = new User("1");
        assertTrue(user.getOutbox().isEmpty());
        User user2 = new User("2");
        user.sendMessage("Title", "Text", user2);
        assertAll (
                () -> assertEquals("Title", user.getOutbox().getFirst().getCaption()),
                () -> assertEquals("Text", user.getOutbox().getFirst().getText()),
                () -> assertEquals(user2, user.getOutbox().getFirst().getReceiver()),
                () -> assertEquals(user, user.getOutbox().getFirst().getSender())
                );
    }

    @Test
    void getSpamTest() {
        User user = new User("1");
        assertTrue(user.getSpam().isEmpty());
        user.setSpamFilter(new SimpleSpamFilter());
        Message message = new Message("Title", "Text spam", new User("2"), user);
        user.addReceivedMessage(message);
        assertEquals(message, user.getSpam().getFirst());
    }

    @Test
    void sendMessageTest() {
        User user = new User("1");
        user.sendMessage("Title", "Text", user);

        assertAll (
                () -> assertEquals("Title", user.getOutbox().getFirst().getCaption(),
                        user.getInbox().getFirst().getCaption()),
                () -> assertEquals("Text", user.getOutbox().getFirst().getText(),
                        user.getInbox().getFirst().getText()),
                () -> assertEquals(user, user.getOutbox().getFirst().getReceiver()),
                () -> assertEquals(user, user.getInbox().getFirst().getReceiver()),
                () -> assertEquals(user, user.getInbox().getFirst().getSender()),
                () -> assertEquals(user, user.getOutbox().getFirst().getSender())
        );
    }

    @Test
    void addReceivedMessageTest() {
        User user = new User("1");
        Message message = new Message("Title", "Text spam", user, user);
        user.addReceivedMessage(message);
        assertEquals(message, user.getInbox().getFirst());
        user.setSpamFilter(new SimpleSpamFilter());
        user.addReceivedMessage(message);
        assertEquals(message, user.getSpam().getFirst());
    }
}