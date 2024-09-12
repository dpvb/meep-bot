package dev.dpvb;

import dev.dpvb.commands.*;
import dev.dpvb.leaderboards.LeaderboardManager;
import dev.dpvb.listeners.*;
import dev.dpvb.util.ProcessorUtil;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MeepBot {

    private static JDA jda;

    public static void main(String[] args) throws InterruptedException {
        final String TOKEN = Environment.getDiscordToken();
        final String STATUS = Environment.getEnvironment().equals("prod") ? "buh" : "dev";

        jda = JDABuilder
                .createDefault(TOKEN)
                .setActivity(Activity.customStatus(STATUS))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_WEBHOOKS)
                .build();

        registerEvents();
        registerCommands();

        jda.awaitReady();

        // initialize leaderboard manager
        LeaderboardManager.init(jda);

        if (args.length == 1) {
            if (args[0].equals("process-messages")) {
                ProcessorUtil.processMessages(jda);
            } else if (args[0].equals("process-users")) {
                ProcessorUtil.processUsers(jda);
            }
        }
    }

    private static void registerEvents() {
        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new JoinListener());
        jda.addEventListener(new GifListener());
        jda.addEventListener(new InsultListener());
        jda.addEventListener(new MessageStatListener());
        // jda.addEventListener(new JudgeListener());
    }

    private static void registerCommands() {
        // Add the commands...
        Set<Command> commands = new HashSet<>();
        commands.add(new HeyCommand());
        commands.add(new SuggestInsultCommand());
        commands.add(new InsultsCommand());
//        commands.add(new EmbedCommand());

        // Register the slash commands with Discord
        jda.updateCommands()
                .addCommands(commands.stream()
                        .map(Command::generateSlashCommand)
                        .collect(Collectors.toList()))
                .queue();

        // Register the event listeners
        commands.forEach(command -> jda.addEventListener(command));
        // Confirm all commands have been added.
        jda.retrieveCommands()
                .complete()
                .forEach(command -> System.out.println("Registered command: " + command.getName()));
    }

    public static class Environment {
        private static final Dotenv dotenv = Dotenv.load();

        public static String getDiscordToken() {
            return dotenv.get("DISCORD_TOKEN");
        }

        public static String getMongoURI() {
            return dotenv.get("MONGO_URI");
        }

        public static String getEnvironment() {
            return dotenv.get("ENVIRONMENT");
        }
    }

}
