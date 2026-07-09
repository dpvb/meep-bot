package dev.dpvb.jobs;

import dev.dpvb.util.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.time.Duration;
import java.util.List;

public class JoshNicknameJob extends Job {

    private final JDA jda;
    private final List<String> NICKNAMES = List.of(
            "rat man",
            "pluh",
            "brownie enjoyer"
    );

    public JoshNicknameJob(JDA jda) {
        super(true);
        this.jda = jda;
    }

    @Override
    public void run() {
        final Guild guild = jda.getGuildById(Constants.MEEP_GUILD_ID);
        if (guild == null) {
            return;
        }

        guild.retrieveMemberById(Constants.JOSH_ID).queue(josh -> {
            if (josh == null) {
                return;
            }
            final String nickname = NICKNAMES.get((int) (Math.random() * NICKNAMES.size()));
            josh.modifyNickname(nickname).queue();
        });

    }

    @Override
    protected long getInitialDelay() {
        return 0;
    }

    @Override
    protected long getDelay() {
        return Duration.ofHours(1).toMillis();
    }
}
