package dev.dpvb.commands;

import dev.dpvb.util.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class BanJoshCommand extends Command {

    public BanJoshCommand() {
        super("ban-josh", "ban josh for being a rat");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        final Guild guild = event.getGuild();
        if (guild == null) {
            return;
        }

        final Member joshMember = guild.getMemberById(Constants.JOSH_ID);
        if (joshMember == null) {
            return;
        }

        DefaultGuildChannelUnion defaultChannel = guild.getDefaultChannel();
        if (defaultChannel == null) {
            return;
        }


        defaultChannel.createInvite()
                .setUnique(true)
                .setMaxUses(10)
                .queue(invite -> joshMember
                        .getUser()
                        .openPrivateChannel()
                        .queue(privateChannel -> privateChannel
                                .sendMessage("rofl youre about to get banned: " + invite.getUrl())
                                .queue()));

        joshMember.ban(0, TimeUnit.MINUTES).queueAfter(5L, TimeUnit.SECONDS);
        guild.unban(joshMember).queueAfter(10L, TimeUnit.SECONDS);

        event.reply("you banned josh!").queue();
    }
}
