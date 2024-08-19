package dev.dpvb.listeners;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.Message;
import dev.dpvb.mongo.services.MessageService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    private final MessageService messageService;

    public MessageListener() {
         messageService = MongoManager.getInstance().getMessageService();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final String username = event.getAuthor().getName();
        final String message = event.getMessage().getContentRaw();
        final String timeCreated = event.getMessage().getTimeCreated().toString();

        messageService.create(new Message(username, message, timeCreated));
    }
}
