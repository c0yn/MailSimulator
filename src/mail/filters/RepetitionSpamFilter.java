package mail.filters;

import mail.Message;
import mail.exceptions.filters.IllegalRepetitionArgumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RepetitionSpamFilter implements SpamFilter {
    private final long maxRepetition;

    public RepetitionSpamFilter(String maxRepetitionString) {
        if (!maxRepetitionString.matches("\\d+") || maxRepetitionString.equals("0")) {
            throw new IllegalRepetitionArgumentException
                    ("Ошибка. Количество повторений должно быть целым положительным десятичным числом");
        }
        try {
            this.maxRepetition = Long.parseLong(maxRepetitionString);
        }
        catch (NumberFormatException err) {
            throw new IllegalRepetitionArgumentException
                    ("Ошибка. Введенное число слишком большое и не поддерживается");
        }
    }

    @Override
    public boolean isSpam(Message message) {
        List<String> messageWords = new ArrayList<>();
        String[] temporalMessageWords = message.getText().toLowerCase().split("[^а-яёa-z0-9]");
        for (String potentialWord : temporalMessageWords) {
            if (!potentialWord.isEmpty()) {
                messageWords.add(potentialWord);
            }
        }

        Map<String, Long> frequencyMessageWordsMap = messageWords.stream()
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        return frequencyMessageWordsMap.values().stream().anyMatch(count -> count > maxRepetition);
    }
}
