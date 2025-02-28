package dev.dpvb.commands;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.WordleEntry;
import dev.dpvb.mongo.services.WordleEntryService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public class WordleEntriesCommand extends Command {

    private final WordleEntryService wes;

    public WordleEntriesCommand() {
        super("wordle-entries", "Get the list of entries for the given Wordle Number");

        this.wes = MongoManager.getInstance().getWordleEntryService();
    }

    @Override
    public SlashCommandData generateSlashCommand() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.INTEGER, "wordlenumber", "The day number to get entries for", true)
                .addOption(OptionType.BOOLEAN, "expanded", "Show the guesses as well", false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        long wordleNumberLong = event.getOption("wordlenumber").getAsLong();
        boolean expanded = event.getOption("expanded").getAsBoolean();

        int wordleNumber = (int) wordleNumberLong;
        if (wordleNumberLong != wordleNumber) {
            event.reply("Invalid wordle number!").queue();
            return;
        }

        List<WordleEntry> wordleEntries = wes.getEntriesByWordleNumber(wordleNumber);

        String connection = expanded ? "\n\n" : "\n";
        Function<WordleEntry, String> toMessage = expanded
                ? entry -> entry.asLongMessage(false)
                : entry -> entry.asShortMessage(false);

        String body = wordleEntries.stream().map(toMessage).collect(Collectors.joining(connection));
        String message = String.format("Wordle Entries for Wordle %,d%n%s", wordleNumber, body);
        event.reply(message).queue();
    }

}
