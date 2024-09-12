package dev.dpvb.commands;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.services.InsultSuggestionService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class InsultsCommand extends Command {

    private final InsultSuggestionService iss;

    public InsultsCommand() {
        super("insults", "List all of Brownie's insults!");
        iss = MongoManager.getInstance().getInsultSuggestionService();
    }

    @Override
    public SlashCommandData generateSlashCommand() {
        return Commands.slash(getName(), getDescription())
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(getName())) {
            // loop through approved messages and store them in a string
            StringBuilder sb = new StringBuilder();
            sb.append("# Approved Insults\n");
            iss.getApprovedInsults().forEach(insult -> sb.append("- ").append(insult.getInsult()).append("\n"));
            event.reply(sb.toString()).queue();
        }
    }
}
