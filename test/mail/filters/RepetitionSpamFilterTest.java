package mail.filters;

import mail.Message;
import mail.exceptions.filters.IllegalRepetitionArgumentException;
import mail.storage.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepetitionSpamFilterTest {

    @Test
    void exceptionThrowTest() {
        assertAll(
                () -> {
                    IllegalRepetitionArgumentException ex = assertThrows(
                            IllegalRepetitionArgumentException.class,
                            () -> new RepetitionSpamFilter("0")
                    );
                    assertTrue(ex.getMessage().contains("Ошибка. Кол"));
                },
                () -> {
                    IllegalRepetitionArgumentException ex = assertThrows(
                            IllegalRepetitionArgumentException.class,
                            () -> new RepetitionSpamFilter("-1")
                    );
                    assertTrue(ex.getMessage().contains("Ошибка. Кол"));
                },
                () -> {
                    IllegalRepetitionArgumentException ex = assertThrows(
                            IllegalRepetitionArgumentException.class,
                            () -> new RepetitionSpamFilter("восемь")
                    );
                    assertTrue(ex.getMessage().contains("Ошибка. Кол"));
                },
                () -> {
                    IllegalRepetitionArgumentException ex = assertThrows(
                            IllegalRepetitionArgumentException.class,
                            () -> new RepetitionSpamFilter("9999999999999999999999999999")
                    );
                    assertTrue(ex.getMessage().contains("Ошибка. Введ"));
                }
        );
    }

    @Test
    void isSpamTest() {
        Message message1 = new Message("123", "1 1_1@1-1!4++1=",
                new User("1"), new User("2"));
        Message message2 = new Message("1", "hi_hi_h1 hi!_@#^#hi hi1!hi!1o hi",
                new User("1"), new User("2"));
        Message message3 = new Message("1 1 1 1 1 1", "1",
                new User("1"), new User("2"));
        Message message4 = new Message("1", "1 1 1 1 1",
                new User("1"), new User("2"));
        RepetitionSpamFilter filter = new RepetitionSpamFilter("5");
        assertAll(
                () -> assertTrue(filter.isSpam(message1)),
                () -> assertTrue(filter.isSpam(message2)),
                () -> assertFalse(filter.isSpam(message3)),
                () -> assertFalse(filter.isSpam(message4))
        );
    }
}