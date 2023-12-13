package dev.dpvb;

import dev.dpvb.commands.HeyCommand;
import dev.dpvb.listeners.JoinListener;
import dev.dpvb.listeners.MessageListener;
import dev.dpvb.listeners.ReadyListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import spark.Spark;

public class MeepBot {

    private static JDA jda;

    public static void main(String[] args) throws InterruptedException {
        final Dotenv dotenv = Dotenv.load();
        final String TOKEN = dotenv.get("DISCORD_TOKEN");
        jda = JDABuilder
                .createDefault(TOKEN)
                .setActivity(Activity.customStatus("mega pace"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        registerEvents();
        registerCommands();
        setupServer();

        jda.awaitReady();
    }

    private static void registerEvents() {
        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new MessageListener());
        jda.addEventListener(new JoinListener());
    }

    private static void registerCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("hey", "Say hey!")
        ).queue();
        jda.addEventListener(new HeyCommand());
    }

    private static void setupServer() {
        Spark.port(4567);
        Spark.get("/status", (req, res) -> "Online");
    }

}
