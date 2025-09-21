package dev.dpvb.leaderboards;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class JoshJoinCountLeaderboard extends Leaderboard {
    private static final String WELCOME_CHANNEL_ID = "1180266248626507897";

    private final JDA jda;

    public JoshJoinCountLeaderboard(JDA jda) {
        super("JOSH JOIN COUNT");
        this.jda = jda;
    }

    @Override
    public String ranking() {
        TextChannel welcomeChannel = jda.getTextChannelById(WELCOME_CHANNEL_ID);
        if (welcomeChannel == null) {
            System.err.println("Error: Could not retrieve Welcome Channel");
            return "";
        }

        MessageHistory history = new MessageHistory(welcomeChannel);

        long joshJoinCount = 0;

        List<Message> messages = history.retrievePast(100).complete();
        while (!messages.isEmpty()) {
            long batchCount = messages.stream()
                    // TODO: parameterize this. what if the welcome message changes?
                    .filter(message -> message.getContentRaw().equals("Welcome this new rat: D_Diamonds"))
                    .count();

            joshJoinCount += batchCount;

            messages = history.retrievePast(100).complete();
        }

        return String.format("Josh has joined the server %d times... :face_with_open_eyes_and_hand_over_mouth:\n", joshJoinCount);
    }
}
