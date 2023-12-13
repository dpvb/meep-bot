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
import spark.Filter;
import spark.Spark;

import java.util.HashMap;

import static spark.Spark.before;

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

    private static void enableCORS(final String origin, final String methods, final String headers) {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }

    private static void setupServer() {
        Spark.port(4567);
        enableCORS("*", "*", "*");
        Spark.get("/status", (req, res) -> "Online");
    }

}
