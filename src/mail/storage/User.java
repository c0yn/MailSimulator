package mail.storage;

import mail.Message;
import mail.filters.CompositeSpamFilter;
import mail.filters.SpamFilter;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String userName;
    private final List<Message> inbox;
    private final List<Message> outbox;
    private final List<Message> spam;
    private SpamFilter spamFilter;

    public User(String userName) {
        this.userName = userName;
        this.inbox = new ArrayList<>();
        this.outbox = new ArrayList<>();
        this.spam = new ArrayList<>();
        this.spamFilter = new CompositeSpamFilter();
    }

    public String getUserName() {
        return this.userName;
    }

    public List<Message> getInbox() {
        return this.inbox;
    }

    public List<Message> getOutbox() {
        return this.outbox;
    }

    public List<Message> getSpam() {
        return this.spam;
    }

    public void setSpamFilter(SpamFilter newSpamFilter) {
        this.spamFilter = newSpamFilter;
    }

    public void sendMessage(String caption, String text, User receiver) {
        Message outgoingMessage = new Message(caption, text, this, receiver);
        this.outbox.add(outgoingMessage);
        receiver.addReceivedMessage(outgoingMessage);
    }

    public void addReceivedMessage(Message incomingMessage) {
        if (this.spamFilter.isSpam(incomingMessage)) {
            this.spam.add(incomingMessage);
        }
        else {
            this.inbox.add(incomingMessage);
        }
    }
}
