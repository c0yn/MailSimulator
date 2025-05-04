package mail.storage;

import mail.exceptions.storage.EmptyUserNameException;
import mail.exceptions.storage.UserAlreadyExistsException;
import mail.exceptions.storage.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserStorage {
    private List<User> usersList;

    public UserStorage() {
        this.usersList = new ArrayList<>();
    }

    public List<User> getUsersList() {
        return this.usersList;
    }

    public void addNewUser(String newUserName) {
        if (newUserName.isEmpty()) {
            throw new EmptyUserNameException("Пустое имя пользователя");
        }
        else if (!verifyUserExistence(newUserName)) {
            this.usersList.add(new User(newUserName));
        }
        else {
            throw new UserAlreadyExistsException
                    ("Ошибка. Пользователь с именем '" + newUserName + "' уже существует");
        }
    }

    public User getUser(String userName) {
        if (verifyUserExistence(userName)) {
            for (User user : this.usersList) {
                if (user.getUserName().equals(userName)) {
                    return user;
                }
            }
        }
        throw new UserNotFoundException
                ("Ошибка. Пользователя с именем '" + userName + "' не существует");
    }

    public boolean verifyUserExistence(String userName) {
        return usersList.stream()
                .anyMatch(user -> user.getUserName().equals(userName));
    }
}
