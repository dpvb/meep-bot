package dev.dpvb.commands;

import dev.dpvb.mongo.models.WordleEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WordleEntriesCommand extends WordleCommand {

    public WordleEntriesCommand() {
        super("wordle-entries", "Get the list of entries for the given Wordle Number");
    }

    @Override
    public SlashCommandData generateSlashCommand() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "wordlenumber", "The day number to get entries for", true)
                .addOption(OptionType.BOOLEAN, "expanded", "Show the guesses as well", false)
                .addOption(OptionType.USER, "user", "Show only this user's entry", false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long wordleNumberLong = event.getOption("wordlenumber").getAsLong();

        boolean expanded = false;
        OptionMapping expandedOption = event.getOption("expanded");
        if (expandedOption != null) {
            expanded = expandedOption.getAsBoolean();
        }

        Optional<User> userOp = Optional.ofNullable(event.getOption("user")).map(OptionMapping::getAsUser);

        int wordleNumber = (int) wordleNumberLong;
        if (wordleNumberLong != wordleNumber) {
            event.reply("Invalid wordle number!").queue();
            return;
        }

        List<WordleEntry> wordleEntries = this.wes.getEntriesByWordleNumber(wordleNumber);
        if (userOp.isPresent()) {
            String userId = userOp.get().getId();
            wordleEntries = wordleEntries.stream().filter(e -> userId.equals(e.getDiscordID())).toList();
        }

        if (wordleEntries.isEmpty()) {
            event.reply(String.format("There are no entries for Wordle %,d", wordleNumber)).queue();
            return;
        }

        String connection = expanded ? "\n\n" : "\n";
        Function<WordleEntry, String> toMessage = expanded
                ? entry -> entry.asLongMessage(false)
                : entry -> entry.asShortMessage(false, "- ");

        String body = wordleEntries.stream().map(toMessage).collect(Collectors.joining(connection));
        String message = String.format("Wordle Entries for Wordle %,d%n%s", wordleNumber, body);
        event.reply(message).queue();
    }

}
