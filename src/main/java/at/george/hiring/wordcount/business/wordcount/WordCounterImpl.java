package at.george.hiring.wordcount.business.wordcount;

import at.george.hiring.wordcount.business.stopword.StopWordsLoader;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordCounterImpl implements WordCounter {

    private final StopWordsLoader stopWordsLoader;
    private final Pattern isValidWordPattern;

    public WordCounterImpl(StopWordsLoader stopWordsLoader) {
        this.stopWordsLoader = stopWordsLoader;
        this.isValidWordPattern = Pattern.compile("[A-Za-z]+");
    }

    @Override
    public WordCountData countWords(String text) {
        Objects.requireNonNull(text, "Text input must not be null");

        List<String> allWords = Arrays.stream(text.trim().split("\\s|-{2,}"))
                .map(this::removeDotOnWordEnd)
                .map(this::removeHypens)
                .filter(this::isWordValid)
                .filter(this::filterStopWords)
                .collect(Collectors.toList());

        int totalWordLength = allWords.stream()
                .mapToInt(String::length)
                .sum();

        Set<String> uniqueWordsSet = new HashSet<>(allWords);

        BigDecimal dividend = new BigDecimal(totalWordLength);
        BigDecimal divisor = new BigDecimal(allWords.size());
        BigDecimal averageWordLength = (allWords.isEmpty())? BigDecimal.ZERO : dividend.divide(divisor, 2, BigDecimal.ROUND_UP);

        return new WordCountData(allWords.size(), uniqueWordsSet.size(), averageWordLength, uniqueWordsSet);
    }

    private String removeDotOnWordEnd(String w) {
        return (!w.trim().isEmpty() && w.charAt(w.length() - 1) == '.') ? w.substring(0, w.length() - 1) : w;
    }

    private String removeHypens(String w) {
        return w.replaceAll("-", "");
    }

    private boolean isWordValid(String word) {
        return isValidWordPattern.matcher(word).matches();
    }

    private boolean filterStopWords(String w) {
        return !stopWordsLoader.containsWord(w);
    }
}
