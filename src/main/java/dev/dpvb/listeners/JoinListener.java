package dev.dpvb.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        final Guild guild = event.getGuild();
        final long roleID = 1180259292125859941L;
        final Role role = guild.getRoleById(roleID);
        if (role == null) {
            System.err.println("The MEEPER role was not found!");
            return;
        }

        guild.addRoleToMember(event.getMember(), role).queue();

        final String memberName = event.getMember().getEffectiveName();
        Optional.ofNullable(guild.getDefaultChannel())
                .ifPresent(defaultGuildChannelUnion -> defaultGuildChannelUnion.asTextChannel()
                        .sendMessage("Welcome this new rat: " + memberName).queue());
    }
}
