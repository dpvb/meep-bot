package dev.dpvb.util;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.MessageStats;
import dev.dpvb.mongo.services.MessageStatsService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessorUtil {

    public static void processMessages(JDA jda) {
        System.out.println("Processing messages...");
        final long guildID = 1129622683546554479L;
        final Guild guild = jda.getGuildById(guildID);
        if (guild == null) {
            System.out.println("Couldn't find MEEP guild.");
            System.exit(1);
        }

        final List<TextChannel> textChannels = guild.getTextChannels();
        final List<Message> allMessages = new ArrayList<>();

        // Retrieve all messages from all text channels
        for (TextChannel textChannel : textChannels) {
            final MessageHistory history = new MessageHistory(textChannel);
            List<Message> messages;
            do {
                messages = history.retrievePast(100).complete();
                allMessages.addAll(messages);
                System.out.println("Added " + messages.size() + " messages from " + textChannel.getName());
            } while (!messages.isEmpty());
        }

        // Retrieve all users and prep message stats.
        final Map<String, MessageStats> messageStatMap = new HashMap<>();
        final List<User> users = guild.loadMembers().get().stream().map(Member::getUser).collect(Collectors.toList());

        for (User user : users) {
            final String username = user.getName();
            final String discordID = user.getId();
            final MessageStats messageStats = new MessageStats(discordID, username, 0, 0, 0, 0, 0);
            messageStatMap.put(username, messageStats);
        }

        System.out.println("Retrieved all users.");

        // Loop through all messages and apply correct stats
        for (final Message m : allMessages) {
            final String sender = m.getAuthor().getName();
            final String contentRaw = m.getContentRaw();

            if (!messageStatMap.containsKey(sender)) {
                continue;
            }

            if (contentRaw.equals("plink")) {
                messageStatMap.get(sender).plinks++;
            } else if (contentRaw.equals("buh")) {
                messageStatMap.get(sender).buhs++;
            } else if (contentRaw.equalsIgnoreCase("mow")) {
                messageStatMap.get(sender).mows++;
            } else if (contentRaw.equals("HUMP DAY")) {
                messageStatMap.get(sender).humpDays++;
            }

            messageStatMap.get(sender).totalMessages++;
        }

        System.out.println("Processed all messages.");

        final MessageStatsService mss = MongoManager.getInstance().getMessageStatsService();

        // Apply stats to MONGO
        for (MessageStats messageStats : messageStatMap.values()) {
            mss.setStats(messageStats);
        }

        System.out.println("Applied stats to MongoDB.");

        System.out.println("Done processing messages.");
    }

    public static void processUsers(JDA jda) {
        System.out.println("processing users...");
        final long guildID = 1129622683546554479L;
        final Guild guild = jda.getGuildById(guildID);

        if (guild == null) {
            System.out.println("Couldn't find MEEP guild.");
            System.exit(1);
        }

        guild.loadMembers().get().stream().map(Member::getUser).forEach(user -> {
            System.out.println("User: " + user.getName() + " ID: " + user.getId());
        });

        System.exit(1);
    }

}
