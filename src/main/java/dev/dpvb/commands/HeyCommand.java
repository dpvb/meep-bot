package dev.dpvb.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HeyCommand extends Command {

    public HeyCommand() {
        super("hey", "Say hey!");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(getName())) {
            event.reply("Hey there!").queue();
        }
    }
}
