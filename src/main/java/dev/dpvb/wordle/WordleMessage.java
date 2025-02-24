package dev.dpvb.wordle;

import static dev.dpvb.util.NumberUtil.safeParseInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordleMessage {

    private static Pattern WORDLE_HEADER_PATTERN = Pattern.compile("Wordle ((\\d{1,3})(,\\d{3})*) (\\d|X)/6(\\*)?");

    private int wordleNumber;
    private int guessCount;
    private boolean hardMode;
    private List<WordleGuess> guesses;

    public WordleMessage() {
    }

    public WordleMessage(int wordleNumber, int guessCount, boolean hardMode, List<WordleGuess> guesses) {
        this.wordleNumber = wordleNumber;
        this.guessCount = guessCount;
        this.hardMode = hardMode;
        this.guesses = guesses;
    }

    public static Optional<WordleMessage> messageFrom(String content) {
        String[] pieces = content.split("\n\n");

        if (pieces.length < 2) {
            return Optional.empty();
        }

        Matcher headerMatcher = WORDLE_HEADER_PATTERN.matcher(pieces[0]);
        if (!headerMatcher.matches()) {
            return Optional.empty();
        }

        String wordleNumStr = headerMatcher.group(1).replace(",", "");
        Optional<Integer> wordleNumOp = safeParseInt(wordleNumStr);
        if (wordleNumOp.isEmpty()) {
            return Optional.empty();
        }

        String guessCountStr = headerMatcher.group(4);
        Optional<Integer> guessCountOp = safeParseInt(guessCountStr);
        if (guessCountOp.isEmpty()) {
            return Optional.empty();
        }

        int wordleNumber = wordleNumOp.get();
        int guessCount = guessCountOp.get();
        boolean hardMode = headerMatcher.group(5) != null;

        if (!(guessCount == -1 || (1 <= guessCount && guessCount <= 6))) {
            // invalid count
            return Optional.empty();
        }

        String[] guessStrs = pieces[1].split("\n");
        if (guessStrs.length > guessCount) {
            String[] trimmed = new String[guessCount];
            System.arraycopy(guessStrs, 0, trimmed, 0, guessCount);

            guessStrs = trimmed;
        }

        List<WordleGuess> guesses = readGuesses(guessStrs);
        if (!((guessCount == -1 && guesses.size() == 6) || (guesses.size() == guessCount))) {
            return Optional.empty();
        }

        if (hardMode && !validateGuesses(guesses)) {
            return Optional.empty();
        }

        System.out.println("Successfully created a WordleMessage");

        WordleMessage message = new WordleMessage(wordleNumber, guessCount, hardMode, guesses);
        return Optional.of(message);
    }

    public int getWordleNumber() {
        return this.wordleNumber;
    }

    public void setWordleNumber(int wordleNumber) {
        this.wordleNumber = wordleNumber;
    }

    public int getGuessCount() {
        return this.guessCount;
    }

    public void setGuessCount(int guessCount) {
        this.guessCount = guessCount;
    }

    public boolean isHardMode() {
        return this.hardMode;
    }

    public void setHardMode(boolean hardMode) {
        this.hardMode = hardMode;
    }

    public List<WordleGuess> getGuesses() {
        return this.guesses;
    }

    public void setGuesses(List<WordleGuess> guesses) {
        this.guesses = guesses;
    }

    private static boolean validateGuesses(List<WordleGuess> guesses) {
        // TODO(bhester): validate the guess strings?
        return true;
    }

    private static List<WordleGuess> readGuesses(String[] guessStrs) {
        List<WordleGuess> guesses = new ArrayList<>(guessStrs.length);

        for (String guessStr : guessStrs) {
            Optional<WordleGuess> guessOp = WordleGuess.fromString(guessStr);
            if (guessOp.isEmpty()) {
                return Collections.emptyList();
            }
            guesses.add(guessOp.get());
        }

        return guesses;
    }

}
