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
        Matcher matcher = Pattern.compile("\\b[а-яА-ЯёЁa-zA-Z0-9]+\\b")
                .matcher(message.getCaption() + " " + message.getText());
        while (matcher.find()) {
            messageWords.add(matcher.group().toLowerCase());
        }

        return messageWords.stream().anyMatch(keywordsSpam::contains);
    }
}
