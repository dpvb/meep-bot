package dev.dpvb.util;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.MessageStats;
import dev.dpvb.mongo.models.WordleEntry;
import dev.dpvb.mongo.services.MessageStatsService;
import dev.dpvb.mongo.services.WordleEntryService;
import dev.dpvb.wordle.WordleMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.DayOfWeek;
import java.util.*;

public class ProcessorUtil {

    public static void processMessages(JDA jda) {
        System.out.println("Processing messages...");

        final Guild guild = getGuild(jda);

        final List<TextChannel> textChannels = guild.getTextChannels();
        final List<Message> allMessages = getAllMessagesFromChannels(textChannels);

        // Retrieve all users and prep message stats.
        final Map<String, MessageStats> messageStatMap = new HashMap<>();
        final List<User> users = guild.loadMembers().get().stream().map(Member::getUser).toList();

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
            } else if (contentRaw.equals("HUMP DAY") && m.getTimeCreated().getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
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
        System.out.println("Processing users...");

        final Guild guild = getGuild(jda);

        guild.loadMembers().get().stream()
                .map(Member::getUser)
                .forEach(user -> {
                    System.out.println("User: " + user.getName() + " ID: " + user.getId());
                });

        System.out.println("Done processing users.");

        System.exit(0); // exit success
    }

    public static void processWordle(JDA jda) {
        System.out.println("Processing wordle messages...");

        final Guild guild = getGuild(jda);
        final WordleEntryService wes = MongoManager.getInstance().getWordleEntryService();

        TextChannel wordleChannel = guild.getTextChannelById(Constants.Wordle.CHANNEL_ID);
        if (wordleChannel == null) {
            System.err.println("Couldn't find Wordle text channel.");
            System.exit(1);
        }

        List<Message> wordleMessages = getAllMessagesFromChannels(List.of(wordleChannel));
        for (Message wordleMessage : wordleMessages) {
            User author = wordleMessage.getAuthor();
            String content = wordleMessage.getContentRaw();

            Optional<WordleMessage> wordleMessageOp = WordleMessage.messageFrom(content);
            if (wordleMessageOp.isEmpty()) {
                return;
            }

            WordleEntry wordleEntry = new WordleEntry(author.getId(), author.getName(), wordleMessageOp.get());
            wes.addOrUpdateEntry(wordleEntry);
        }

        System.out.println("Done processing wordle messages.");
    }

    private static Guild getGuild(JDA jda) {
        final long guildID = 1129622683546554479L;
        final Guild guild = jda.getGuildById(guildID);
        if (guild == null) {
            System.err.println("Couldn't find MEEP guild.");
            System.exit(1);
        }
        return guild;
    }

    private static List<Message> getAllMessagesFromChannels(List<TextChannel> textChannels) {
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

        return allMessages;
    }

}
