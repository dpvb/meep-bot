package dev.dpvb.listeners;

import dev.dpvb.listeners.gif.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Set;

public class GifListener extends ListenerAdapter {

    private final Set<Gif> GIFS = Set.of(
            new BuhGif(),
            new HumpDayGif(),
            new MowGif(),
            new PlinkGif()
    );

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        GIFS.forEach(gif -> gif.send(content, event.getChannel()));
    }

}
