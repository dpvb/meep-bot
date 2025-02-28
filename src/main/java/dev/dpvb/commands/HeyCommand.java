package dev.dpvb.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class HeyCommand extends Command {

    public HeyCommand() {
        super("hey", "Say hey!");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.reply("Hey there!").queue();
    }
}
