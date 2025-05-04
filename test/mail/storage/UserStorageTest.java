package mail.storage;

import mail.exceptions.storage.EmptyUserNameException;
import mail.exceptions.storage.UserAlreadyExistsException;
import mail.exceptions.storage.UserNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStorageTest {

    @Test
    void getUsersListTest() {
        UserStorage userStorage = new UserStorage();
        assertTrue(userStorage.getUsersList().isEmpty());
    }

    @Test
    void addNewUserTest() {
        UserStorage userStorage = new UserStorage();
        Exception exception1 = assertThrows(EmptyUserNameException.class, () -> {
            userStorage.addNewUser("");
        });
        userStorage.addNewUser("1");
        Exception exception2 = assertThrows(UserAlreadyExistsException.class, () -> {
            userStorage.addNewUser("1");
        });

        assertAll(
                () -> assertEquals("Пустое имя пользователя", exception1.getMessage()),
                () -> assertEquals("Ошибка. Пользователь с именем '1' уже существует",
                        exception2.getMessage()),
                () -> assertEquals("1", userStorage.getUsersList().getFirst().getUserName())
        );
    }

    @Test
    void getUserTest() {
        UserStorage userStorage = new UserStorage();
        userStorage.addNewUser("1");
        Exception exception1 = assertThrows(UserNotFoundException.class, () -> {
            userStorage.getUser("_1_");
        });
        assertAll(
                () -> assertEquals("Ошибка. Пользователя с именем '_1_' не существует",
                        exception1.getMessage()),
                () -> assertEquals("1", userStorage.getUser("1").getUserName())
        );
    }

    @Test
    void verifyUserExistenceTest() {
        UserStorage userStorage = new UserStorage();
        userStorage.addNewUser("1");

        assertAll(
                () -> assertTrue(userStorage.verifyUserExistence("1")),
                () -> assertFalse(userStorage.verifyUserExistence("_1"))
        );
    }
}