package mail.filters;

import mail.Message;
import mail.exceptions.filters.IllegalKeywordArgumentException;
import mail.storage.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeywordsSpamFilterTest {

    @Test
    void exceptionThrowTest() {
        Exception exception = assertThrows(IllegalKeywordArgumentException.class, () ->
                new KeywordsSpamFilter("valid valid1 валид валид1 !nvalid инв@лид"));

        assertTrue(exception.getMessage().contains("Недопустимое ключевое слово спама: "));
    }

    @Test
    void isSpamTest() {
        Message message1 = new Message("Title_spamword", "1",
                new User("1"), new User("2"));
        Message message2 = new Message("1", "Text!spamword",
                new User("1"), new User("2"));
        Message message3 = new Message("Title_1spamword", "1",
                new User("1"), new User("2"));
        Message message4 = new Message("1", "Text!spamword2",
                new User("1"), new User("2"));
        KeywordsSpamFilter filter = new KeywordsSpamFilter("spamword");
        assertAll(
                () -> assertTrue(filter.isSpam(message1)),
                () -> assertTrue(filter.isSpam(message2)),
                () -> assertFalse(filter.isSpam(message3)),
                () -> assertFalse(filter.isSpam(message4))
        );
    }
}