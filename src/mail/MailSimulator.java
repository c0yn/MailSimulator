package mail;

import mail.exceptions.filters.IllegalKeywordArgumentException;
import mail.exceptions.filters.IllegalRepetitionArgumentException;
import mail.exceptions.storage.EmptyUserNameException;
import mail.exceptions.storage.UserAlreadyExistsException;
import mail.exceptions.storage.UserNotFoundException;
import mail.filters.*;
import mail.storage.User;
import mail.storage.UserStorage;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class MailSimulator {
    private final UserStorage userStorage;
    private final Scanner scanner;
    private final PrintStream output;

    public MailSimulator(UserStorage userStorage, InputStream input, PrintStream output) {
        this.userStorage = userStorage;
        this.scanner = new Scanner(input);
        this.output = output;
    }

    void addNewUser() {
        // Метод для создания нового пользователя и отлавливания ошибок
        output.print("Введите имя нового пользователя: ");
        String input = scanner.nextLine();
        try {
            userStorage.addNewUser(input);
            output.print("\nСоздан пользователь с именем '" + input + "'\n>");
        }
        catch (EmptyUserNameException | UserAlreadyExistsException exception) {
            output.print("\n" + exception.getMessage() + "\n>");
        }
    }

    void showUserList() {
        // Метод для вывода списка пользователей
        output.print("\nСписок имен пользователей:");
        AtomicInteger counter = new AtomicInteger(0);
        userStorage.getUsersList().forEach(user ->
                output.print("\n" + counter.incrementAndGet() + " '" + user.getUserName() + "'"));
        output.print("\nВсего: " + counter + " пользоват.\n>");
    }

    User askUserName(String mode)
            throws UserNotFoundException {
        // Метод для обработки различных ситуаций, в которых нужно узнать имя пользователя
        switch (mode) {
            case "sender" -> output.print("\nВведите имя пользователя отправителя: ");
            case "view" -> output.print("\nВведите имя пользователя для показа: ");
            case "receiver" -> output.print("\nВведите имя пользователя получателя: ");
            case "setfilter" -> output.print("\nВведите имя пользователя для установки фильтров: ");
        }
        String input = scanner.nextLine();
        return userStorage.getUser(input);
    }

    void showCurrentMessage(Message currentMessage) {
        // Метод для вывода заданного сообщения
        output.print("\nОтправитель: " + currentMessage.getSender().getUserName() +
                "\nПолучатель: " + currentMessage.getReceiver().getUserName() +
                "\nЗаголовок письма:\n" + currentMessage.getCaption() +
                "\nТекст письма:\n" + currentMessage.getText());
    }

    void showCurrentUserInfo(String mode, User currentUser) {
        // Метод для обработки данных при выводе либо inbox, либо outbox, либо spam
        switch (mode) {
            case "inbox" -> {
                List<Message> currentUserInbox = currentUser.getInbox();
                output.print("\nВходящие письма пользователя с именем '" + currentUser.getUserName() + "'");
                for (Message inboxMessage : currentUserInbox) {
                    output.print("\n=========================");
                    showCurrentMessage(inboxMessage);
                }
            }
            case "outbox" -> {
                List<Message> currentUserOutbox = currentUser.getOutbox();
                output.print("\nИсходящие письма пользователя с именем '" + currentUser.getUserName() + "'");
                for (Message outboxMessage : currentUserOutbox) {
                    output.print("\n=========================");
                    showCurrentMessage(outboxMessage);
                }
            }
            case "spam" -> {
                List<Message> currentUserSpam = currentUser.getSpam();
                output.print("\nСпам письма пользователя с именем '" + currentUser.getUserName() + "'");
                for (Message spamMessage : currentUserSpam) {
                    output.print("\n=========================");
                    showCurrentMessage(spamMessage);
                }
            }
        }
        output.print("\n==========================");
    }

    void showUserInfo(String input) {
        // Метод для предварительной обработки данных при выводе либо inbox, либо outbox, либо spam
        try {
            User currentUser = askUserName("view");
            showCurrentUserInfo(input.toLowerCase(), currentUser);
            output.print("\n>");
        }
        catch (UserNotFoundException exception) {
            output.print("\n" + exception.getMessage() + "\n>");
        }
    }

    String askMessageInfo(String mode) {
        // Дополнительный метод, убирающий лишнюю "грязь" из кода
        if (mode.equals("caption")) {
            output.print("\nВведите заголовок письма:\n");
        }
        else if (mode.equals("text")) {
            output.print("\nВведите текст письма:\n");
        }
        return scanner.nextLine();
    }

    void sendMessage() {
        // Метод для обработки входящих данных и отправления сообщения
        try {
            User sender = askUserName("sender");
            User receiver = askUserName("receiver");
            String caption = askMessageInfo("caption");
            String text = askMessageInfo("text");
            sender.sendMessage(caption, text, receiver);
            output.print("\nСообщение отправлено\n>");
        }
        catch (UserNotFoundException exception) {
            output.print("\n" + exception.getMessage() + "\n>");
        }
    }

    SpamFilter askFilterInfo(String input)
            throws IllegalRepetitionArgumentException, IllegalKeywordArgumentException,
            IllegalArgumentException {
        // Метод для обработки входных данных и созданию по ним определенных фильтров
        switch (input) {
            case "simple" -> {
                return new SimpleSpamFilter();
            }
            case "keywords" -> {
                output.print("\nВведите ключевые слова через пробел: ");
                input = scanner.nextLine();

                return new KeywordsSpamFilter(input);
            }
            case "repetition" -> {
                output.print("\nВведите максимальное число повторений слов в письме: ");
                input = scanner.nextLine();

                return new RepetitionSpamFilter(input);
            }
            case "sender" -> {
                output.print("\nВведите имя пользователя: ");
                input = scanner.nextLine();

                return new SenderSpamFilter(input);
            }
            default -> throw new IllegalArgumentException("Фильтра с названием '" + input + "' не существует\n");
        }
    }

    void setFilter(String mode) {
        // Метод для обработки входных данных и создания спам-фильтра пользователя
        try {
            User currentUser = askUserName(mode);
            CompositeSpamFilter newSpamFilter = new CompositeSpamFilter();
            output.print("\nВведите название фильтра: ");
            String input = scanner.nextLine();
            while (!input.equalsIgnoreCase("done")) {
                try {
                    newSpamFilter.addSpamFilter(askFilterInfo(input.toLowerCase()));
                }
                catch (IllegalKeywordArgumentException |
                       IllegalRepetitionArgumentException |
                       IllegalArgumentException
                        exception) {
                    output.print("\n" + exception.getMessage() + "\n");
                }

                output.print("\nВведите название фильтра: ");
                input = scanner.nextLine();
            }
            currentUser.setSpamFilter(newSpamFilter);
            output.print("\nСпам фильтр успешно установлен\n>");
        }
        catch (UserNotFoundException exception) {
            output.print("\n" + exception.getMessage() + "\n>");
        }
    }

    void processCommand(String command) {
        // Метод для обработки и запуска команд пользователя
        if (command.equalsIgnoreCase("add")) {
            addNewUser();
        }
        else if (command.equalsIgnoreCase("list")) {
            showUserList();
        }
        else if (command.equalsIgnoreCase("send")) {
            sendMessage();
        }
        else if (command.equalsIgnoreCase("inbox") ||
                command.equalsIgnoreCase("outbox") ||
                command.equalsIgnoreCase("spam")) {
            showUserInfo(command);
        }
        else if (command.equalsIgnoreCase("setfilter")) {
            setFilter(command.toLowerCase());
        }
        else {
            output.print("\nВведенная команда '" + command +
                    "' не поддерживается.\nВы можете ознакомиться" +
                    " с руководством пользователя, открыв файл README.txt\n>");
        }
    }

    public void startMailSimulator() {
        output.print("""
                Здравствуй, пользователь! Это почтовый симулятор.
                Руководство пользователя ты сможешь прочитать в файле README.txt
                >""");
        String input = scanner.nextLine();

        while (!input.equalsIgnoreCase("quit")) {
            processCommand(input);

            input = scanner.nextLine();
        }

        output.print("\nСпасибо! До новых встреч!");
    }
}
