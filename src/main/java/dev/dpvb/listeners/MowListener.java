package dev.dpvb.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MowListener extends ListenerAdapter {

    private final String MOW_GIF = "https://tenor.com/view/kittens-meowing-kitty-kittie-adorable-gif-15674064";
    private final String CURSED_MOW_GIF = "https://media.discordapp.net/attachments/815039360322764863/1274787550455795722/kittens-meowing-ezgif.com-effects.gif?ex=66c385ec&is=66c2346c&hm=a3be14bdf04494d55e1e948ab6d0e8fa6d6b382eaa439911cf132b241b19cc7d&=&width=622&height=358";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("mow")) {
            if (Math.random() < 0.05) {
                event.getChannel().sendMessage(CURSED_MOW_GIF).queue();
                return;
            }
            event.getChannel().sendMessage(MOW_GIF).queue();
        }
    }
}
