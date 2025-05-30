package dev.dpvb.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ConkCommand extends Command {

    public ConkCommand() { super("conk", "That's Conk Creet Baybee"); }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) { event.reply("https://imgur.com/a/bOH5iaI").queue(); }

}
