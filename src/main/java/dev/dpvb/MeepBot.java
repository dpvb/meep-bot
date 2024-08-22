package dev.dpvb;

import dev.dpvb.commands.AmazonCommand;
import dev.dpvb.commands.HeyCommand;
import dev.dpvb.commands.TIMRNCommand;
import dev.dpvb.listeners.*;
import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.MessageStats;
import dev.dpvb.mongo.services.MessageStatsService;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MeepBot {

    private static JDA jda;

    public static void main(String[] args) throws InterruptedException {
        final Dotenv dotenv = Dotenv.load();
        final String TOKEN = dotenv.get("DISCORD_TOKEN");
        jda = JDABuilder
                .createDefault(TOKEN)
                .setActivity(Activity.customStatus("plink"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .build();

        registerEvents();
        registerCommands();

        jda.awaitReady();

        if (args.length == 1) {
            if (args[0].equals("process-messages")) {
                processMessages();
            }
        }
    }

    private static void processMessages() {
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
            final MessageStats messageStats = new MessageStats(username, 0, 0, 0, 0);
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

        System.out.println("Done processing messages. Shutting down Brownie.");
        System.exit(1);
    }

    private static void registerEvents() {
        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new JoinListener());
        jda.addEventListener(new PlinkListener());
        jda.addEventListener(new BuhListener());
        jda.addEventListener(new HumpDayListener());
        jda.addEventListener(new MowListener());
        jda.addEventListener(new InsultListener());
        jda.addEventListener(new MessageListener());
    }

    private static void registerCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("hey", "Say hey!"),
                Commands.slash("thisismern", "This is me right now..."),
                Commands.slash("amazon", "Get a nice message!")
        ).queue();
        jda.addEventListener(new HeyCommand());
        jda.addEventListener(new TIMRNCommand());
        jda.addEventListener(new AmazonCommand());
        jda.retrieveCommands().complete()
                .forEach(command -> System.out.println("Registered command: " + command.getName()));
    }

}
