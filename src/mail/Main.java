package mail;

import mail.storage.UserStorage;

public class Main {
    public static void main(String[] args) {
        MailSimulator mailSimulator = new MailSimulator(new UserStorage(), System.in, System.out);

        mailSimulator.startMailSimulator();
    }
}
