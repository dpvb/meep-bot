package dev.dpvb.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class GoatCommand extends Command {

    public GoatCommand() {
        super("goat", "Shows everyone who the real goat is");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.reply(":goat:").queue();
    }

}
