package dev.dpvb;

import dev.dpvb.commands.AmazonCommand;
import dev.dpvb.commands.HeyCommand;
import dev.dpvb.commands.TIMRNCommand;
import dev.dpvb.listeners.JoinListener;
import dev.dpvb.listeners.PlinkListener;
import dev.dpvb.listeners.ReadyListener;
import dev.dpvb.twitch.TwitchListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

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

        new TwitchListener(jda).start();

        jda.awaitReady();
    }

    private static void registerEvents() {
        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new JoinListener());
        jda.addEventListener(new PlinkListener());
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
    }


}
