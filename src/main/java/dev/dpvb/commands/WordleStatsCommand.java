package dev.dpvb.commands;

import dev.dpvb.mongo.models.WordleEntry;
import dev.dpvb.wordle.WordleMessage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordleStatsCommand extends WordleCommand {

    public WordleStatsCommand() {
        super("wordle-stats", "Get a summary of score counts for the given user");
    }

    @Override
    public SlashCommandData generateSlashCommand() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.USER, "user", "The user to get the winning stats for", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping userOption = event.getOption("user");
        User user = userOption != null ? userOption.getAsUser() : event.getUser();

        List<WordleEntry> userEntries = this.wes.getEntriesByDiscordID(user.getId());
        Map<Integer, List<WordleEntry>> grouped = userEntries.stream().collect(
                Collectors.groupingBy(e -> e.getMessage().getGuessCount()));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s has the following Wordle record:%n%n", user.getEffectiveName()));
        for (int i = 1; i <= 6; ++i) {
            int count = grouped.getOrDefault(i, Collections.emptyList()).size();
            String suffix = count != 1 ? "s" : "";

            sb.append(String.format("- %,d game%s won in %d guesses%n", count, suffix, i));
        }

        int lossCount = grouped.getOrDefault(WordleMessage.LOSE_COUNT, Collections.emptyList()).size();
        String lossSuffix = lossCount != 1 ? "s" : "";
        sb.append(String.format("- %,d game%s lost", lossCount, lossSuffix));

        event.reply(sb.toString()).queue();
    }

}
