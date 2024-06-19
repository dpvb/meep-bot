package dev.dpvb.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BuhListener extends ListenerAdapter {

    private final String BUH_GIF = "https://tenor.com/view/buh-b-u-h-cat-silly-%D0%BA%D0%BE%D1%82-gif-2242355352608793340";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("buh")) {
            event.getChannel().sendMessage(BUH_GIF).queue();
        }
    }
}
