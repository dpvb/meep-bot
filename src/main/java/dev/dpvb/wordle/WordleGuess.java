package dev.dpvb.wordle;

import static dev.dpvb.util.Constants.LARGE_BLACK_SQUARE;
import static dev.dpvb.util.Constants.LARGE_WHITE_SQUARE;
import static dev.dpvb.util.Constants.LARGE_YELLOW_SQUARE;
import static dev.dpvb.util.Constants.LARGE_GREEN_SQUARE;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WordleGuess {

    private CellType char0;
    private CellType char1;
    private CellType char2;
    private CellType char3;
    private CellType char4;

    public WordleGuess() {
    }

    public static Optional<WordleGuess> fromString(String guessStr) {
        IntStream codePoints = guessStr.codePoints();
        List<Optional<CellType>> cellTypeOps = codePoints.mapToObj(CellType::fromCodePoint).toList();

        if (cellTypeOps.size() < 5) {
            return Optional.empty();
        }

        cellTypeOps = cellTypeOps.subList(0, 5);

        List<CellType> cellTypes = cellTypeOps.stream()
                .filter(Predicate.not(Optional::isEmpty))
                .map(Optional::get)
                .toList();

        if (cellTypes.size() != 5) {
            return Optional.empty();
        }

        WordleGuess guess = new WordleGuess();

        for (int index = 0; index < 5; ++index) {
            guess.setGuessChar(index, cellTypes.get(index));
        }
        return Optional.of(guess);
    }

    String toGuessString() {
        return Stream.of(char0, char1, char2, char3, char4).map(CellType::toSquare).collect(Collectors.joining());
    }

    public CellType getChar0() {
        return this.char0;
    }

    public void getChar0(CellType char0) {
        this.char0 = char0;
    }

    public CellType getChar1() {
        return this.char1;
    }

    public void getChar1(CellType char1) {
        this.char1 = char1;
    }

    public CellType getChar2() {
        return this.char2;
    }

    public void getChar2(CellType char2) {
        this.char2 = char2;
    }

    public CellType getChar3() {
        return this.char3;
    }

    public void getChar3(CellType char3) {
        this.char3 = char3;
    }

    public CellType getChar4() {
        return this.char4;
    }

    public void getChar4(CellType char4) {
        this.char4 = char4;
    }

    private void setGuessChar(int index, CellType type) {
        switch (index) {
            case 0 -> this.char0 = type;
            case 1 -> this.char1 = type;
            case 2 -> this.char2 = type;
            case 3 -> this.char3 = type;
            case 4 -> this.char4 = type;
        }
    }

    enum CellType {
        INCORRECT,
        MISPLACED,
        CORRECT;

        public static Optional<CellType> fromCodePoint(int codePoint) {
            Optional<CellType> type = switch (codePoint) {
                case LARGE_WHITE_SQUARE, LARGE_BLACK_SQUARE -> Optional.of(INCORRECT);
                case LARGE_YELLOW_SQUARE -> Optional.of(MISPLACED);
                case LARGE_GREEN_SQUARE -> Optional.of(CORRECT);
                default -> Optional.empty();
            };
            return type;
        }

        private String toSquare() {
            int codePoint = switch (this) {
                case INCORRECT -> LARGE_BLACK_SQUARE;
                case MISPLACED -> LARGE_YELLOW_SQUARE;
                case CORRECT -> LARGE_GREEN_SQUARE;
            };
            return Character.toString(codePoint);
        }
    }

}
