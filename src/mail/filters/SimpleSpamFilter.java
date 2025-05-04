package mail.filters;

import mail.Message;

public class SimpleSpamFilter implements SpamFilter {
    private final KeywordsSpamFilter simpleSpamFilter;

    public SimpleSpamFilter() {
        this.simpleSpamFilter = new KeywordsSpamFilter("spam спам");
    }

    @Override
    public boolean isSpam(Message message) {
        return this.simpleSpamFilter.isSpam(message);
    }
}
