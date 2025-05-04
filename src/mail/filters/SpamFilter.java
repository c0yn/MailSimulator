package mail.filters;

import mail.Message;

public interface SpamFilter {
    boolean isSpam(Message message);
}
