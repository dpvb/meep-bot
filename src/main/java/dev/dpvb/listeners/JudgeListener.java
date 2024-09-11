package dev.dpvb.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class JudgeListener extends ListenerAdapter {

    private static final String PATTERN = "<@1137831321599746158> judge";

    private static final Emoji THUMBS_UP = Emoji.fromUnicode("\uD83D\uDC4D");
    private static final Emoji THUMBS_DOWN = Emoji.fromUnicode("\uD83D\uDC4E");
    private static final Emoji GUN = Emoji.fromUnicode("\uD83D\uDD2B");

    private static final long JUDGE_TIME = 15L;

    private final Set<Message> brownieJudgeMessages;

    public JudgeListener() {
        this.brownieJudgeMessages = new HashSet<>();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Retrieve the message and check to see if it is an inline reply.
        final Message message = event.getMessage();
        if (message.getType() != MessageType.INLINE_REPLY) {
            return;
        }

        // Get the referenced message (the message that will be judged) and stop if it doesn't exist.
        // (this shouldn't happen)
        final Message referencedMessage = message.getReferencedMessage();
        if (referencedMessage == null) {
            return;
        }

        // Check if the message content matches the pattern for judging...
        if (!message.getContentRaw().equals(PATTERN)) {
            return;
        }

        // Get the defendant's mention string.
        final String defendantMention = referencedMessage.getAuthor().getAsMention();

        referencedMessage.reply("Is " + defendantMention + " brainrot? " + GUN.getName()).queue(m -> {
            // Starting the judgement process here.

            // Add reactions for people to vote on.
            m.addReaction(THUMBS_UP).queue();
            m.addReaction(THUMBS_DOWN).queue();

            // Add the message to the set of messages that are being used for voting.
            brownieJudgeMessages.add(m);

            // Edit the message after a certain amount of time to make the final judgement.
            m.editMessage("Making the final judgement...").queueAfter(JUDGE_TIME, TimeUnit.SECONDS, edited -> {
                // Remove the message from the set of messages that are being used for voting.
                brownieJudgeMessages.remove(edited);

                // Get the number of thumbs up and thumbs down reactions.
                int thumbsUpCount = Optional.ofNullable(edited.getReaction(THUMBS_UP)).map(emoji -> emoji.getCount() - 1).orElse(0);
                int thumbsDownCount = Optional.ofNullable(edited.getReaction(THUMBS_DOWN)).map(emoji -> emoji.getCount() - 1).orElse(0);
                int total = thumbsUpCount + thumbsDownCount;

                // Clear all reactions from the message.
                edited.clearReactions().queue();

                if (total < 3) {
                    edited.editMessage("Not enough votes to make a judgement.").queue();
                    return;
                }

                if (thumbsUpCount > thumbsDownCount) {
                    edited.editMessage(defendantMention + " is definitely brainrot...").queue();
                    Optional.ofNullable(referencedMessage.getMember())
                            .ifPresent(member -> member.timeoutFor(1L, TimeUnit.MINUTES).queue());
                    return;
                }

                if (thumbsUpCount < thumbsDownCount) {
                    edited.editMessage(defendantMention + " has been spared for now...").queue();
                    return;
                }

                edited.editMessage("No decision could be made...").queue();
            });
        });
    }

    /**
     * Makes it so users can only have exclusively one thumbs up or thumbs down at a time.
     * @param event The message react add event.
     */
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        // Retrieve the message and check to see if it is a Brownie Voting Message.
        final Message message = event.retrieveMessage().complete();
        if (!brownieJudgeMessages.contains(message)) {
            return;
        }

        // Get the user who is reacting.
        final User reactor = event.retrieveUser().complete();

        // Return if the reactor is a bot.
        if (reactor.isBot()) {
            return;
        }

        // Retrieve the reaction and remove the opposite reaction if it exists.
        final MessageReaction reaction = event.getReaction();
        final Emoji emoji = reaction.getEmoji();
        if (emoji.equals(THUMBS_DOWN) || emoji.equals(THUMBS_UP)) {
            final Emoji oppositeEmoji = emoji.equals(THUMBS_UP) ? THUMBS_DOWN : THUMBS_UP;
            Optional.ofNullable(message.getReaction(oppositeEmoji)).ifPresent(r -> r.removeReaction(reactor).queue());
        }
    }
}
