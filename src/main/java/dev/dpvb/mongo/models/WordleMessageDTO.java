package dev.dpvb.mongo.models;

import java.util.Arrays;
import java.util.List;

import dev.dpvb.wordle.WordleMessage;

public class WordleMessageDTO {

    public int wordleNumber;
    public int guessCount;
    public boolean hardMode;
    public List<String> guesses;

    public WordleMessageDTO() {
    }

    public WordleMessageDTO(int wordleNumber, int guessCount, boolean hardMode, String[] guesses) {
        this.wordleNumber = wordleNumber;
        this.guessCount = guessCount;
        this.hardMode = hardMode;
        this.guesses = Arrays.asList(guesses);
    }

    public WordleMessageDTO(WordleMessage message) {
        this.wordleNumber = message.getWordleNumber();
        this.guessCount = message.getGuessCount();
        this.hardMode = message.isHardMode();
        this.guesses = Arrays.asList(message.getGuesses());
    }

}
