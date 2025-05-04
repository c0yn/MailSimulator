package mail.filters;

import mail.Message;
import mail.exceptions.filters.IllegalKeywordArgumentException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KeywordsSpamFilter implements SpamFilter {
    private final Set<String> keywordsSpam;

    public KeywordsSpamFilter(String keywordsSpamString) {
        this.keywordsSpam = Arrays.stream(keywordsSpamString.trim().split("\\s+"))
                .peek(keyword -> {
                    if (!keyword.matches("[а-яА-ЯёЁa-zA-Z0-9]+")) {
                        throw new IllegalKeywordArgumentException
                                ("Недопустимое ключевое слово спама: " + keyword);
                    }
                })
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isSpam(Message message) {
        List<String> messageWords = new ArrayList<>();
        String[] temporalMessageWords = (message.getCaption() + " " + message.getText())
                .toLowerCase()
                .split("[^а-яёa-z0-9]");
        for (String potentialWord : temporalMessageWords) {
            if (!potentialWord.isEmpty()) {
                messageWords.add(potentialWord);
            }
        }

        return messageWords.stream().anyMatch(keywordsSpam::contains);
    }
}
