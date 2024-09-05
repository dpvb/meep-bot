package dev.dpvb.listeners.gif;

public class MowGif extends Gif {

    private static final String MOW_GIF = "https://tenor.com/view/kittens-meowing-kitty-kittie-adorable-gif-15674064";
    private static final String CURSED_MOW_GIF = "https://media.discordapp.net/attachments/815039360322764863/1274787550455795722/kittens-meowing-ezgif.com-effects.gif?ex=66c385ec&is=66c2346c&hm=a3be14bdf04494d55e1e948ab6d0e8fa6d6b382eaa439911cf132b241b19cc7d&=&width=622&height=358";

    private static final double CURSED_CHANCE = 0.05;

    public MowGif() {
        super("mow", true);
    }

    @Override
    public String getGifURL() {
        if (Math.random() < CURSED_CHANCE) {
            return CURSED_MOW_GIF;
        }
        return MOW_GIF;
    }

}
