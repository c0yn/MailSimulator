package mail.filters;

import mail.Message;
import mail.storage.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleSpamFilterTest {

    @Test
    void isSpamTest() {
        Message message1 = new Message("spam", "1",
                new User("1"), new User("2"));
        Message message2 = new Message("1", "spam",
                new User("1"), new User("2"));
        Message message3 = new Message("!spam1", "1",
                new User("1"), new User("2"));
        Message message4 = new Message("1", "!spam1",
                new User("1"), new User("2"));
        SimpleSpamFilter filter = new SimpleSpamFilter();
        assertAll(
                () -> assertTrue(filter.isSpam(message1)),
                () -> assertTrue(filter.isSpam(message2)),
                () -> assertFalse(filter.isSpam(message3)),
                () -> assertFalse(filter.isSpam(message4))
        );
    }
}