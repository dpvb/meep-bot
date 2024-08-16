package dev.dpvb.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MowListener extends ListenerAdapter {

    private final String MOW_GIF = "https://tenor.com/view/kittens-meowing-kitty-kittie-adorable-gif-15674064";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("mow")) {
            event.getChannel().sendMessage(MOW_GIF).queue();
        }
    }
}
