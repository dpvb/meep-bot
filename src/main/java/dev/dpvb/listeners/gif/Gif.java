package dev.dpvb.listeners.gif;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public abstract class Gif {

    private final String trigger;
    private final boolean ignoreTriggerCase;

    public Gif(String trigger, boolean ignoreTriggerCase) {
        this.trigger = trigger;
        this.ignoreTriggerCase = ignoreTriggerCase;
    }

    public abstract String getGifURL();

    public void send(String content, MessageChannelUnion channel) {
        if (matchesTrigger(content)) {
            channel.sendMessage(getGifURL()).queue();
        }
    }

    private boolean matchesTrigger(String message) {
        if (ignoreTriggerCase) {
             message = message.toLowerCase();
        }
        return message.equals(trigger);
    }
}
