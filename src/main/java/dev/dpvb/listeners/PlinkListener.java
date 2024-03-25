package dev.dpvb.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PlinkListener extends ListenerAdapter {

    private final String PLINK_GIF = "https://tenor.com/view/plink-wide-cat-plink-cat-meow-gif-27396868";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("plink")) {
            event.getChannel().sendMessage(PLINK_GIF).queue();
        }
    }
}
