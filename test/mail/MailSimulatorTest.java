package mail;

import mail.exceptions.filters.IllegalKeywordArgumentException;
import mail.exceptions.filters.IllegalRepetitionArgumentException;
import mail.exceptions.storage.UserNotFoundException;
import mail.storage.User;
import mail.storage.UserStorage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
class MailSimulatorTest {

    @Test
    void addNewUserTest() {
        UserStorage userStorage = new UserStorage();

        ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        MailSimulator simulator1 = new MailSimulator(
                userStorage,
                new ByteArrayInputStream("test_user\n".getBytes()),
                new PrintStream(output1)
        );
        simulator1.addNewUser();

        ByteArrayOutputStream output2 = new ByteArrayOutputStream();
        MailSimulator simulator2 = new MailSimulator(
                userStorage,
                new ByteArrayInputStream("\n".getBytes()),
                new PrintStream(output2)
        );
        simulator2.addNewUser();

        ByteArrayOutputStream output3 = new ByteArrayOutputStream();
        MailSimulator simulator3 = new MailSimulator(
                userStorage,
                new ByteArrayInputStream("test_user\n".getBytes()),
                new PrintStream(output3)
        );
        simulator3.addNewUser();

        assertAll(
                () -> assertTrue(userStorage.verifyUserExistence("test_user")),
                () -> assertTrue(output1.toString().contains("Создан пользователь")),
                () -> assertTrue(output2.toString().contains("Пустое имя пользователя")),
                () -> assertTrue(output3.toString().contains
                        ("Ошибка. Пользователь с именем 'test_user' уже существует"))
        );
    }

    @Test
    void showUserListTest() {
        UserStorage userStorage = new UserStorage();

        String input = "user1\nuser2\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.addNewUser();
        simulator.addNewUser();
        simulator.showUserList();

        assertAll(
                () -> assertTrue(userStorage.verifyUserExistence("user1")),
                () -> assertTrue(userStorage.verifyUserExistence("user2")),
                () -> assertTrue(output.toString().contains("user1")),
                () -> assertTrue(output.toString().contains("user2"))
        );
    }

    @Test
    void askUserNameTest() {
        UserStorage userStorage = new UserStorage();

        String input = "user1\nuser1\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.addNewUser();
        User result = simulator.askUserName("");

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            String input1 = "user\n";
            ByteArrayOutputStream output1 = new ByteArrayOutputStream();
            MailSimulator simulator1 = new MailSimulator(
                    userStorage,
                    new ByteArrayInputStream(input1.getBytes()),
                    new PrintStream(output1)
            );
            simulator1.askUserName("");
        });

        assertAll(
                () -> assertEquals("user1", result.getUserName()),
                () -> assertTrue(exception.getMessage().contains("Ошибка. Пользователя с именем"))
        );
    }

    @Test
    void showCurrentMessageTest() {
        UserStorage userStorage = new UserStorage();
        Message message = new Message("Title", "Text", new User("1"), new User("2"));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream("".getBytes()),
                new PrintStream(output)
        );
        simulator.showCurrentMessage(message);

        assertAll(
                () -> assertTrue(output.toString().contains(message.getText())),
                () -> assertTrue(output.toString().contains(message.getCaption())),
                () -> assertTrue(output.toString().contains(message.getSender().getUserName())),
                () -> assertTrue(output.toString().contains(message.getReceiver().getUserName()))
        );
    }

    @Test
    void showCurrentUserInfoTest() {
        UserStorage userStorage = new UserStorage();
        userStorage.addNewUser("1");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream("".getBytes()),
                new PrintStream(output)
        );
        simulator.showCurrentUserInfo("inbox", userStorage.getUser("1"));
        simulator.showCurrentUserInfo("outbox", userStorage.getUser("1"));
        simulator.showCurrentUserInfo("spam", userStorage.getUser("1"));

        assertAll(
                () -> assertTrue(output.toString().contains("Входящие письма пользователя с именем")),
                () -> assertTrue(output.toString().contains("Исходящие письма пользователя с именем")),
                () -> assertTrue(output.toString().contains("Спам письма пользователя с именем")),
                () -> assertTrue(output.toString().contains("========================="))
        );
    }

    @Test
    void showUserInfoTest() {
        UserStorage userStorage = new UserStorage();
        userStorage.addNewUser("user1");

        String input = "user1\nuser\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.showUserInfo("inbox");
        simulator.showUserInfo("inbox");

        assertAll(
                () -> assertTrue(output.toString().contains("Введите имя пользователя для показа:")),
                () -> assertTrue(output.toString().contains("Входящие письма пользователя с именем")),
                () -> assertTrue(output.toString().contains("=========================")),
                () -> assertTrue(output.toString().contains("Ошибка. Пользователя с именем"))
        );
    }

    @Test
    void askMessageInfoTest() {
        UserStorage userStorage = new UserStorage();

        String input = "a\nb\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        String a = simulator.askMessageInfo("caption");
        String b = simulator.askMessageInfo("text");

        assertAll(
                () -> assertTrue(output.toString().contains("Введите заголовок письма:")),
                () -> assertTrue(output.toString().contains("Введите текст письма:")),
                () -> assertEquals("a", a),
                () -> assertEquals("b", b)
        );
    }

    @Test
    void sendMessageTest() {
        UserStorage userStorage = new UserStorage();
        userStorage.addNewUser("user");

        String input = "user\nuser\nTitle\nText\nnoUser\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.sendMessage();
        simulator.sendMessage();

        assertAll(
                () -> assertTrue(output.toString().contains("Введите имя пользователя отправителя:")),
                () -> assertTrue(output.toString().contains("Введите имя пользователя получателя:")),
                () -> assertTrue(output.toString().contains("Введите заголовок письма:")),
                () -> assertTrue(output.toString().contains("Введите текст письма:")),
                () -> assertTrue(output.toString().contains("Сообщение отправлено")),
                () -> assertTrue(output.toString().contains("Ошибка. Пользователя с именем"))
        );
    }

    @Test
    void askFilterInfoTest() {
        UserStorage userStorage = new UserStorage();

        String input = "1 2 3\n3\nuser\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.askFilterInfo("simple");
        simulator.askFilterInfo("keywords");
        simulator.askFilterInfo("repetition");
        simulator.askFilterInfo("sender");

        Exception exception1 = assertThrows(IllegalKeywordArgumentException.class, () -> {
            String input1 = "valid valid1 валид валид1 !nvalid инв@лид\n";
            ByteArrayOutputStream output1 = new ByteArrayOutputStream();
            MailSimulator simulator1 = new MailSimulator(
                    userStorage,
                    new ByteArrayInputStream(input1.getBytes()),
                    new PrintStream(output1)
            );
            simulator1.askFilterInfo("keywords");
        });

        Exception exception2 = assertThrows(IllegalRepetitionArgumentException.class, () -> {
            String input1 = "0\n";
            ByteArrayOutputStream output1 = new ByteArrayOutputStream();
            MailSimulator simulator1 = new MailSimulator(
                    userStorage,
                    new ByteArrayInputStream(input1.getBytes()),
                    new PrintStream(output1)
            );
            simulator1.askFilterInfo("repetition");
        });

        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            String input1 = "";
            ByteArrayOutputStream output1 = new ByteArrayOutputStream();
            MailSimulator simulator1 = new MailSimulator(
                    userStorage,
                    new ByteArrayInputStream(input1.getBytes()),
                    new PrintStream(output1)
            );
            simulator1.askFilterInfo("invalid");
        });

        assertAll(
                () -> assertTrue(output.toString().contains("Введите ключевые слова через пробел:")),
                () -> assertTrue(output.toString().contains("Введите максимальное число повторений слов в письме: ")),
                () -> assertTrue(output.toString().contains("Введите имя пользователя: ")),
                () -> assertTrue(exception1.getMessage().contains("Недопустимое ключевое слово спама: ")),
                () -> assertTrue(exception2.getMessage().contains("Ошибка. ")),
                () -> assertTrue(exception3.getMessage().contains("Фильтра с названием "))
        );
    }

    @Test
    void setFilterTest() {
        UserStorage userStorage = new UserStorage();
        userStorage.addNewUser("user");

        String input = "user\nsimple\nkeywords\n1 2 3\nrepetition\n3\nsender\nuser1\ninvalid\ndone\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.setFilter("setfilter");

        assertAll(
                () -> assertTrue(output.toString().contains("Введите имя пользователя для установки фильтров:")),
                () -> assertTrue(output.toString().contains("Введите название фильтра:")),
                () -> assertTrue(output.toString().contains("Введите ключевые слова через пробел:")),
                () -> assertTrue(output.toString().contains("Введите максимальное число повторений слов в письме:")),
                () -> assertTrue(output.toString().contains("Введите имя пользователя:")),
                () -> assertTrue(output.toString().contains("Фильтра с названием ")),
                () -> assertTrue(output.toString().contains("Спам фильтр успешно установлен"))
        );
    }

    @Test
    void processCommandTest() {
        UserStorage userStorage = new UserStorage();

        String input = "user\nuser\nuser\nTitle\nText\nuser\nuser\nuser\nuser\ndone\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.processCommand("add");
        simulator.processCommand("list");
        simulator.processCommand("send");
        simulator.processCommand("inbox");
        simulator.processCommand("outbox");
        simulator.processCommand("spam");
        simulator.processCommand("setfilter");
        simulator.processCommand("invalid");

        assertAll(
                () -> assertTrue(output.toString().contains("Создан пользователь с именем")),
                () -> assertTrue(output.toString().contains("пользоват.")),
                () -> assertTrue(output.toString().contains("Сообщение отправлено")),
                () -> assertTrue(output.toString().contains("Входящие письма пользователя с именем")),
                () -> assertTrue(output.toString().contains("Исходящие письма пользователя с именем")),
                () -> assertTrue(output.toString().contains("Спам письма пользователя с именем")),
                () -> assertTrue(output.toString().contains("==========================")),
                () -> assertTrue(output.toString().contains("Спам фильтр успешно установлен")),
                () -> assertTrue(output.toString().contains("с руководством пользователя, открыв файл README"))
        );
    }

    @Test
    void startMailSimulatorTest() {
        UserStorage userStorage = new UserStorage();

        String input = "invalid\nquit\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MailSimulator simulator = new MailSimulator(
                userStorage,
                new ByteArrayInputStream(input.getBytes()),
                new PrintStream(output)
        );
        simulator.startMailSimulator();

        assertAll(
                () -> assertTrue(output.toString().contains("Здравствуй, пользователь! Это почтовый симулятор")),
                () -> assertTrue(output.toString().contains("Спасибо! До новых встреч!"))
        );
    }
}