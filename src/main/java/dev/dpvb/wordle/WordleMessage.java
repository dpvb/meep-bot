package dev.dpvb.wordle;

import static dev.dpvb.util.NumberUtil.safeParseInt;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordleMessage {

    private static Pattern WORDLE_HEADER_PATTERN = Pattern.compile("Wordle ((\\d{1,3})(,\\d{3})*) (\\d|X)/6(\\*)?");

    private final int wordleNumber;
    private final int guessCount;
    private final boolean hardMode;
    private final String[] guesses;

    private WordleMessage(int wordleNumber, int guessCount, boolean hardMode, String[] guesses) {
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

        if (!(guessCount == -1 || (1 <= guessCount && guessCount <= 6))) {
            // invalid count
            return Optional.empty();
        }

        String[] guesses = pieces[1].split("\n");
        if (guesses.length > guessCount) {
            String[] trimmed = new String[guessCount];
            System.arraycopy(guesses, 0, trimmed, 0, guessCount);

            guesses = trimmed;
        }

        // TODO(bhester): validate the guess strings?
        // TODO(bhester): change internal representation of the guesses for future
        // processing?

        boolean hardMode = headerMatcher.group(5) != null;

        WordleMessage message = new WordleMessage(wordleNumber, guessCount, hardMode, guesses);
        return Optional.of(message);
    }

    public int getWordleNumber() {
        return this.wordleNumber;
    }

    public int getGuessCount() {
        return this.guessCount;
    }

    public boolean isHardMode() {
        return this.hardMode;
    }

    public String[] getGuesses() {
        return this.guesses;
    }

}
