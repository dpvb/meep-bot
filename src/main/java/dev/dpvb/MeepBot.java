package dev.dpvb;

import dev.dpvb.commands.HeyCommand;
import dev.dpvb.listeners.MessageListener;
import dev.dpvb.listeners.ReadyListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class MeepBot {

    public static void main(String[] args) throws InterruptedException {
        final Dotenv dotenv = Dotenv.load();
        final String TOKEN = dotenv.get("DISCORD_TOKEN");
        final JDA jda = JDABuilder
                .createDefault(TOKEN)
                .setActivity(Activity.customStatus("bully flare sim"))
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("hey", "Say hey!")
        ).queue();

        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new MessageListener());

        jda.addEventListener(new HeyCommand());

        jda.awaitReady();
    }

}
