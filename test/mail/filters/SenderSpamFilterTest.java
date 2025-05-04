package mail.filters;

import mail.Message;
import mail.storage.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenderSpamFilterTest {

    @Test
    void exceptionThrowTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SenderSpamFilter("");
        });

        assertTrue(exception.getMessage().contains("Имя пользователя отправителя для спама не может быть пустым."));
    }

    @Test
    void isSpam() {
        Message message1 = new Message("1", "1",
                new User("2"), new User("1"));
        Message message2 = new Message("1", "1",
                new User("1"), new User("2"));
        SenderSpamFilter filter = new SenderSpamFilter("2");
        assertAll(
                () -> assertTrue(filter.isSpam(message1)),
                () -> assertFalse(filter.isSpam(message2))
        );
    }
}