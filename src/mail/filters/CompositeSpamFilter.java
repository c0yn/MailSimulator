package mail.filters;

import mail.Message;

import java.util.ArrayList;
import java.util.List;

public class CompositeSpamFilter implements SpamFilter {
    private List<SpamFilter> spamFilters;

    public CompositeSpamFilter() {
        this.spamFilters = new ArrayList<>();
    }

    public void addSpamFilter(SpamFilter newSpamFilter) {
        this.spamFilters.add(newSpamFilter);
    }

    @Override
    public boolean isSpam(Message message) {
        return spamFilters.stream()
                .anyMatch(spamFilter -> spamFilter.isSpam(message));
    }
}
