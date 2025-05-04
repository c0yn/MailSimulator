package mail.filters;

import mail.Message;
import mail.storage.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeSpamFilterTest {

    @Test
    void isSpamTest() {
        CompositeSpamFilter filter = new CompositeSpamFilter();
        assertFalse(filter.isSpam(new Message("", "", new User("1"), new User("2"))));
        filter.addSpamFilter(new SimpleSpamFilter());
        filter.addSpamFilter(new KeywordsSpamFilter("1"));
        filter.addSpamFilter(new SenderSpamFilter("2"));
        filter.addSpamFilter(new RepetitionSpamFilter("3"));

        Message message1 = new Message("spam", "2",
                new User("1"), new User("2"));
        Message message2 = new Message("1", "Text",
                new User("1"), new User("2"));
        Message message3 = new Message("Title", "1",
                new User("2"), new User("1"));
        Message message4 = new Message("Title", "2 2 2 2",
                new User("1"), new User("2"));
        Message message5 = new Message("Title", "Text",
                new User("1"), new User("2"));
        assertAll(
                () -> assertTrue(filter.isSpam(message1)),
                () -> assertTrue(filter.isSpam(message2)),
                () -> assertTrue(filter.isSpam(message3)),
                () -> assertTrue(filter.isSpam(message4)),
                () -> assertFalse(filter.isSpam(message5))
        );
    }
}