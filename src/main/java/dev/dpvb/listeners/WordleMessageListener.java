package dev.dpvb.listeners;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.WordleEntry;
import dev.dpvb.mongo.services.WordleEntryService;
import dev.dpvb.util.Constants;
import dev.dpvb.wordle.WordleMessage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

public class WordleMessageListener extends ListenerAdapter {

    private final WordleEntryService wes;

    public WordleMessageListener() {
        this.wes = MongoManager.getInstance().getWordleEntryService();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        long channelId = event.getChannel().getIdLong();
        if (channelId != Constants.Wordle.CHANNEL_ID) {
            return;
        }

        User author = event.getAuthor();
        String content = event.getMessage().getContentRaw();

        Optional<WordleMessage> wordleMessageOp = WordleMessage.messageFrom(content);
        if (wordleMessageOp.isEmpty()) {
            return;
        }

        WordleEntry wordleEntry = new WordleEntry(author.getId(), author.getName(), wordleMessageOp.get());
        wes.addOrUpdateEntry(wordleEntry);
    }

}
