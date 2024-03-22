package dev.dpvb.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TIMRNCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("thisismern")) {
            event.reply("https://tenor.com/view/sleepy-tired-exhausted-this-is-me-right-now-gif-12203858").queue();
        }
    }
}
