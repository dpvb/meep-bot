package dev.dpvb.commands;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.services.InsultSuggestionService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InsultsCommand extends ListenerAdapter {

    private final InsultSuggestionService iss;

    public InsultsCommand() {
        iss = MongoManager.getInstance().getInsultSuggestionService();
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("insults")) {
            // loop through approved messages and store them in a string
            StringBuilder sb = new StringBuilder();
            sb.append("# Approved Insults\n");
            iss.getApprovedInsults().forEach(insult -> sb.append("- ").append(insult.getInsult()).append("\n"));
            event.reply(sb.toString()).queue();
        }
    }
}
