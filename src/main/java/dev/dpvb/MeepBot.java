package dev.dpvb;

import dev.dpvb.commands.HeyCommand;
import dev.dpvb.commands.InsultsCommand;
import dev.dpvb.commands.StatCheckCommand;
import dev.dpvb.commands.SuggestInsultCommand;
import dev.dpvb.listeners.*;
import dev.dpvb.util.ProcessorUtil;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_WEBHOOKS)
                .build();

        registerEvents();
        registerCommands();

        jda.awaitReady();

        if (args.length == 1) {
            if (args[0].equals("process-messages")) {
                ProcessorUtil.processMessages(jda);
            }
        }
    }

    private static void registerEvents() {
        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new JoinListener());
        jda.addEventListener(new GifListener());
        jda.addEventListener(new InsultListener());
        jda.addEventListener(new MessageStatListener());
    }

    private static void registerCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("hey", "Say hey!"),
                Commands.slash("statcheck", "Get your message stats"),
                Commands.slash("allstats", "Get server message stats"),
                Commands.slash("suggestinsult", "Suggest an insult for Brownie to use")
                        .addOption(OptionType.STRING, "insult", "The insult to suggest", true),
                Commands.slash("insults", "Lists all insults of Brownie's insults")
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        ).queue();
        jda.addEventListener(new HeyCommand());
        jda.addEventListener(new StatCheckCommand());
        jda.addEventListener(new SuggestInsultCommand());
        jda.addEventListener(new InsultsCommand());
        jda.retrieveCommands().complete()
                .forEach(command -> System.out.println("Registered command: " + command.getName()));
    }

}
