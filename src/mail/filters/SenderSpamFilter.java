package mail.filters;

import mail.Message;
import mail.exceptions.storage.EmptyUserNameException;

public class SenderSpamFilter implements SpamFilter {
    private final String userSpam;

    public SenderSpamFilter(String userSpam) {
        if (userSpam.isEmpty()) {
            throw new IllegalArgumentException
                    ("Имя пользователя отправителя для спама не может быть пустым.");
        }
        this.userSpam = userSpam;
    }

    @Override
    public boolean isSpam(Message message) {
        return message.getSender().getUserName().equals(this.userSpam);
    }
}
