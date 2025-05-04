package mail;

import mail.storage.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void getCaptionTest() {
        assertEquals("Title", new Message("Title", "Text",
                new User("1"), new User("2")).getCaption());
    }

    @Test
    void getTextTest() {
        assertEquals("Text", new Message("Title", "Text",
                new User("1"), new User("2")).getText());
    }

    @Test
    void getSenderTest() {
        User user1 = new User("1");
        User user2 = new User("2");

        assertEquals(user1, new Message("Title", "Text", user1, user2).getSender());
    }

    @Test
    void getReceiverTest() {
        User user1 = new User("1");
        User user2 = new User("2");

        assertEquals(user2, new Message("Title", "Text", user1, user2).getReceiver());
    }
}