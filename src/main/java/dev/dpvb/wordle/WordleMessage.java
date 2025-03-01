package dev.dpvb.wordle;

import dev.dpvb.wordle.WordleGuess.CellType;

import static dev.dpvb.util.NumberUtil.safeParseInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordleMessage {

    public static final int LOSE_COUNT = -1;
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
        int wordleNumber = wordleNumOp.get();

        String guessCountStr = headerMatcher.group(4);
        // Regex is being matched in the header matcher above, so this has already been
        // validated. Thus, if it is not a number between 1-6 it must be X.
        int guessCount = safeParseInt(guessCountStr).orElse(LOSE_COUNT);

        if (!(guessCount == LOSE_COUNT || (1 <= guessCount && guessCount <= 6))) {
            // invalid count
            return Optional.empty();
        }

        boolean hardMode = headerMatcher.group(5) != null;

        String[] guessStrs = pieces[1].split("\n");
        if (guessCount == LOSE_COUNT && guessStrs.length > 6) {
            // If someone got scored X, and they wrote stuff after they pasted their score.
            String[] trimmed = new String[6];
            System.arraycopy(guessStrs, 0, trimmed, 0, 6);
            guessStrs = trimmed;

        } else if (guessCount != LOSE_COUNT && guessStrs.length > guessCount) {
            // If someone got a score in range [1,6] and we need to trim the guesses down.
            String[] trimmed = new String[guessCount];
            System.arraycopy(guessStrs, 0, trimmed, 0, guessCount);
            guessStrs = trimmed;
        }

        List<WordleGuess> guesses = readGuesses(guessStrs);
        if (!((guessCount == LOSE_COUNT && guesses.size() == 6) || (guesses.size() == guessCount))) {
            return Optional.empty();
        }

        if (!validateGuesses(guesses, hardMode)) {
            return Optional.empty();
        }

        WordleMessage message = new WordleMessage(wordleNumber, guessCount, hardMode, guesses);
        return Optional.of(message);
    }

    public boolean didWin() {
        return this.guessCount != LOSE_COUNT;
    }

    public String toGuessString() {
        return this.guesses.stream().map(WordleGuess::toGuessString).collect(Collectors.joining("\n"));
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

    /**
     * Validates the list of guesses for the Wordle message. If not played in hard
     * mode, checks that either the user did not complete the Wordle, or that the
     * last guess was successful (and no sooner). If played in hard mode, that same
     * check is performed, as well as ensuring that the number of
     * {@link CellType#CORRECT correct} and {@link CellType#MISPLACED misplaced}
     * cells behave as one would expect in a hard mode run.
     *
     * @param guesses  the list of guesses in this message
     * @param hardMode whether the message indicates the game was played in hard
     *                 mode
     * @return {@code true} if the guesses indicate a valid message, {@code false}
     *         otherwise
     */
    private static boolean validateGuesses(List<WordleGuess> guesses, boolean hardMode) {
        assert guesses.size() > 0 : "Expected at least one guess";

        int lastIndex = guesses.size() - 1;
        WordleGuess lastGuess = guesses.get(lastIndex);
        if (!isGuessAllCorrect(lastGuess) && guesses.size() != 6) {
            return false;
        }

        for (WordleGuess guess : guesses.subList(0, lastIndex)) {
            if (isGuessAllCorrect(guess)) {
                return false;
            }
        }

        if (hardMode) {
            CellType cell0 = CellType.INCORRECT;
            CellType cell1 = CellType.INCORRECT;
            CellType cell2 = CellType.INCORRECT;
            CellType cell3 = CellType.INCORRECT;
            CellType cell4 = CellType.INCORRECT;

            int numCorrect = 0;
            int numMisplaced = 0;

            for (WordleGuess guess : guesses) {
                // a correct cell must stay correct

                if (cell0 == CellType.CORRECT && guess.getChar0() != CellType.CORRECT) {
                    return false;
                }
                if (cell1 == CellType.CORRECT && guess.getChar1() != CellType.CORRECT) {
                    return false;
                }
                if (cell2 == CellType.CORRECT && guess.getChar2() != CellType.CORRECT) {
                    return false;
                }
                if (cell3 == CellType.CORRECT && guess.getChar3() != CellType.CORRECT) {
                    return false;
                }
                if (cell4 == CellType.CORRECT && guess.getChar4() != CellType.CORRECT) {
                    return false;
                }

                cell0 = guess.getChar0();
                cell1 = guess.getChar1();
                cell2 = guess.getChar2();
                cell3 = guess.getChar3();
                cell4 = guess.getChar4();

                // the number of correct cells must be non-decreasing (which is actually caught
                // above...)
                int newNumCorrect = countOfType(CellType.CORRECT, cell0, cell1, cell2, cell3, cell4);
                if (newNumCorrect < numCorrect) {
                    return false;
                }

                // the sum of correct and misplaced cells must be non-decreasing
                int newNumMisplaced = countOfType(CellType.MISPLACED, cell0, cell1, cell2, cell3, cell4);
                if (newNumCorrect + newNumMisplaced < numCorrect + numMisplaced) {
                    return false;
                }

                numCorrect = newNumCorrect;
                numMisplaced = newNumMisplaced;
            }
        }

        return true;
    }

    private static boolean isGuessAllCorrect(WordleGuess guess) {
        return guess.getChar0().equals(CellType.CORRECT)
                && guess.getChar1().equals(CellType.CORRECT)
                && guess.getChar2().equals(CellType.CORRECT)
                && guess.getChar3().equals(CellType.CORRECT)
                && guess.getChar4().equals(CellType.CORRECT);
    }

    private static int countOfType(CellType target, CellType... vals) {
        int count = 0;
        for (CellType val : vals) {
            if (val == target) {
                ++count;
            }
        }
        return count;
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
