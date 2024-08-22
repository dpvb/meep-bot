package dev.dpvb.listeners;

import java.util.Calendar;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HumpDayListener extends ListenerAdapter {

    private final String HUMP_GIF = "https://tenor.com/hEw3B9yGnxS.gif";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        var weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (event.getMessage().getContentRaw().equals("HUMP DAY")
                && weekday == Calendar.WEDNESDAY) {
            event.getChannel().sendMessage(HUMP_GIF).queue();
        }
    }
}
