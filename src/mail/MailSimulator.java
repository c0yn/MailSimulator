package mail;

import mail.exceptions.filters.IllegalKeywordArgumentException;
import mail.exceptions.filters.IllegalRepetitionArgumentException;
import mail.exceptions.storage.EmptyUserNameException;
import mail.exceptions.storage.UserAlreadyExistsException;
import mail.exceptions.storage.UserNotFoundException;
import mail.filters.*;
import mail.storage.User;
import mail.storage.UserStorage;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class MailSimulator {
    private static void addNewUser(UserStorage userStorage, Scanner in) {
        // Метод для создания нового пользователя и отлавливания ошибок
        System.out.print("Введите имя нового пользователя: ");
        String input = in.nextLine();
        try {
            userStorage.addNewUser(input);
            System.out.print("\nСоздан пользователь с именем '" + input + "'\n>");
        }
        catch (EmptyUserNameException | UserAlreadyExistsException exception) {
            System.out.print("\n" + exception.getMessage() + "\n>");
        }
    }

    private static void showUserList(UserStorage userStorage) {
        // Метод для вывода списка пользователей
        System.out.print("\nСписок имен пользователей:");
        AtomicInteger num = new AtomicInteger(0);
        userStorage.getUsersList().forEach(user ->
                System.out.print("\n" + num.incrementAndGet() + " '" + user.getUserName() + "'"));
        System.out.print("\nВсего: " + num + " пользоват.\n>");
    }

    private static User askUserName(String mode, UserStorage userStorage, Scanner in)
            throws UserNotFoundException {
        // Метод для обработки различных ситуаций, в которых нужно узнать имя пользователя
        switch (mode) {
            case "sender" -> System.out.print("\nВведите имя пользователя отправителя: ");
            case "view" -> System.out.print("\nВведите имя пользователя для показа: ");
            case "receiver" -> System.out.print("\nВведите имя пользователя получателя: ");
            case "setfilter" -> System.out.print("\nВведите имя пользователя для установки фильтров: ");
        }
        String input = in.nextLine();
        return userStorage.getUser(input);
    }

    private static void showCurrentMessage(Message currentMessage) {
        // Метод для вывода заданного сообщения
        System.out.print("\nОтправитель: " + currentMessage.getSender().getUserName() +
                "\nПолучатель: " + currentMessage.getReceiver().getUserName() +
                "\nЗаголовок письма:\n" + currentMessage.getCaption() +
                "\nТекст письма:\n" + currentMessage.getText());
    }

    private static void showCurrentUserInfo(String mode, User currentUser) {
        // Метод для обработки данных при выводе либо inbox, либо outbox, либо spam
        switch (mode) {
            case "inbox" -> {
                List<Message> currentUserInbox = currentUser.getInbox();
                System.out.print("\nВходящие письма пользователя с именем '" + currentUser.getUserName() + "'");
                for (Message inboxMessage : currentUserInbox) {
                    System.out.print("\n=========================");
                    showCurrentMessage(inboxMessage);
                }
            }
            case "outbox" -> {
                List<Message> currentUserOutbox = currentUser.getOutbox();
                System.out.print("\nИсходящие письма пользователя с именем '" + currentUser.getUserName() + "'");
                for (Message outboxMessage : currentUserOutbox) {
                    System.out.print("\n=========================");
                    showCurrentMessage(outboxMessage);
                }
            }
            case "spam" -> {
                List<Message> currentUserSpam = currentUser.getSpam();
                System.out.print("\nСпам письма пользователя с именем '" + currentUser.getUserName() + "'");
                for (Message spamMessage : currentUserSpam) {
                    System.out.print("\n=========================");
                    showCurrentMessage(spamMessage);
                }
            }
        }
        System.out.print("\n=========================");
    }

    private static void showUserInfo(String input, UserStorage userStorage, Scanner in) {
        // Метод для предварительной обработки данных при выводе либо inbox, либо outbox, либо spam
        try {
            User currentUser = askUserName("view", userStorage, in);
            showCurrentUserInfo(input.toLowerCase(), currentUser);
            System.out.print("\n>");
        }
        catch (UserNotFoundException exception) {
            System.out.print("\n" + exception.getMessage() + "\n>");
        }
    }

    private static String askMessageInfo(String mode, Scanner in) {
        // Дополнительный метод, убирающий лишнюю "грязь" из кода
        if (mode.equals("caption")) {
            System.out.print("\nВведите заголовок письма:\n");
        }
        else if (mode.equals("text")) {
            System.out.print("\nВведите текст письма:\n");
        }
        return in.nextLine();
    }

    private static void sendMessage(UserStorage userStorage, Scanner in) {
        // Метод для обработки входящих данных и отправления сообщения
        try {
            User sender = askUserName("sender", userStorage, in);
            User receiver = askUserName("receiver", userStorage, in);
            String caption = askMessageInfo("caption", in);
            String text = askMessageInfo("text", in);
            sender.sendMessage(caption, text, receiver);
            System.out.print("\nСообщение отправлено\n>");
        }
        catch (UserNotFoundException exception) {
            System.out.print("\n" + exception.getMessage() + "\n>");
        }
    }

    private static SpamFilter askFilterInfo(String input, Scanner in)
            throws IllegalRepetitionArgumentException, IllegalKeywordArgumentException,
            IllegalArgumentException {
        // Метод для обработки входных данных и созданию по ним определенных фильтров
        switch (input) {
            case "simple" -> {
                return new SimpleSpamFilter();
            }
            case "keywords" -> {
                System.out.print("\nВведите ключевые слова через пробел: ");
                input = in.nextLine();

                return new KeywordsSpamFilter(input);
            }
            case "repetition" -> {
                System.out.print("\nВведите максимальное число повторений слов в письме: ");
                input = in.nextLine();

                return new RepetitionSpamFilter(input);
            }
            case "sender" -> {
                System.out.print("\nВведите имя пользователя: ");
                input = in.nextLine();

                return new SenderSpamFilter(input);
            }
            default -> throw new IllegalArgumentException("Фильтра с названием '" + input + "' не существует\n");
        }
    }

    private static void setFilter(String mode, UserStorage userStorage, Scanner in) {
        // Метод для обработки входных данных и создания спам-фильтра пользователя
        try {
            User currentUser = askUserName(mode, userStorage, in);
            CompositeSpamFilter newSpamFilter = new CompositeSpamFilter();
            System.out.print("\nВведите название фильтра: ");
            String input = in.nextLine();
            while (!input.equalsIgnoreCase("done")) {
                try {
                    newSpamFilter.addSpamFilter(askFilterInfo(input.toLowerCase(), in));
                }
                catch (IllegalKeywordArgumentException |
                       IllegalRepetitionArgumentException |
                       IllegalArgumentException
                        exception) {
                    System.out.print("\n" + exception.getMessage() + "\n");
                }

                System.out.print("\nВведите название фильтра: ");
                input = in.nextLine();
            }
            currentUser.setSpamFilter(newSpamFilter);
            System.out.print("\nСпам фильтр успешно установлен\n>");
        }
        catch (UserNotFoundException exception) {
            System.out.print("\n" + exception.getMessage() + "\n>");
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Здравствуй, пользователь! Это почтовый симулятор.\n" +
                "Руководство пользователя ты сможешь прочитать в файле README.txt\n>");
        String input = in.nextLine();
        UserStorage userStorage = new UserStorage();

        while (!input.equalsIgnoreCase("quit")) {
            if (input.equalsIgnoreCase("add")) {
                addNewUser(userStorage, in);

                input = in.nextLine();
            }
            else if (input.equalsIgnoreCase("list")) {
                showUserList(userStorage);

                input = in.nextLine();
            }
            else if (input.equalsIgnoreCase("send")) {
                sendMessage(userStorage, in);

                input = in.nextLine();
            }
            else if (input.equalsIgnoreCase("inbox") ||
                    input.equalsIgnoreCase("outbox") ||
                    input.equalsIgnoreCase("spam")) {
                showUserInfo(input, userStorage, in);

                input = in.nextLine();
            }
            else if (input.equalsIgnoreCase("setfilter")) {
                setFilter(input.toLowerCase(), userStorage, in);

                input = in.nextLine();
            }
            else {
                System.out.print("\nВведенная команда '" + input +
                        "' не поддерживается.\nВы можете ознакомиться" +
                        " с руководством пользователя, открыв файл README.txt\n>");

                input = in.nextLine();
            }
        }
    }
}
