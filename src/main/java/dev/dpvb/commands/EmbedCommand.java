package dev.dpvb.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public class EmbedCommand extends Command {

    public EmbedCommand() {
        super("embed", "Generates an embed message");
    }

    @Override
    public SlashCommandData generateSlashCommand() {
        return Commands.slash(getName(), getDescription())
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(getName())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("placeholder embed...");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }
}
