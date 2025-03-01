package dev.dpvb.commands;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.InsultSuggestion;
import dev.dpvb.mongo.services.InsultSuggestionService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;

public class SuggestInsultCommand extends Command {

    private final InsultSuggestionService iss;

    private final static String INSULT_SUGGESTION_INGESTION_CHANNEL_ID = "1280670129939681357";
    private final static String PENDING_IMAGE = "https://media.discordapp.net/stickers/1279818877848522825.webp?size=160&quality=lossless";
    private final static String APPROVED_IMAGE = "https://media1.tenor.com/m/y1QFa-1vyKYAAAAC/plink-wide-cat.gif";
    private final static String DENIED_IMAGE = "https://media1.tenor.com/m/Hx5yUAxxYvwAAAAC/buh-b-u-h.gif";

    public SuggestInsultCommand() {
        super("suggestinsult", "Suggest an insult for Brownie to use!");
        iss = MongoManager.getInstance().getInsultSuggestionService();
    }

    @Override
    public SlashCommandData generateSlashCommand() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "insult", "The insult to suggest", true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        final String insult = event.getOption("insult").getAsString();
        event.reply("Suggestion sent!").queue();

        // Add the insult to the database.
        final String username = event.getMember().getUser().getName();
        final Date date = new Date();
        final String approvalStatus = "pending";
        final InsultSuggestion insultSuggestion = new InsultSuggestion(username, approvalStatus, insult, date);
        String objectIdAsString = iss.addInsultSuggestion(insultSuggestion).asObjectId().getValue().toString();

        // Send the suggestion to the ingestion channel.
        final TextChannel isiChannel = event.getJDA().getTextChannelById(INSULT_SUGGESTION_INGESTION_CHANNEL_ID);
        if (isiChannel == null) {
            System.err.println("Insult suggestion channel was not found...");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Pending Mow", null);
        eb.setColor(Color.yellow);
        eb.setDescription("<@" + event.getMember().getId() + "> - " + insult);
        eb.setImage(PENDING_IMAGE);
        eb.setFooter(objectIdAsString, null);
        MessageEmbed me = eb.build();
        isiChannel.sendMessageEmbeds(me).addActionRow(
                Button.primary("approve", "Approve"),
                Button.danger("deny", "Deny")).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        final MessageEmbed oldEmbed = event.getMessage().getEmbeds().get(0);
        final String insultID = oldEmbed.getFooter().getText();
        final InsultSuggestion insultSuggestion = iss.getInsultSuggestion(insultID);
        final EmbedBuilder eb = new EmbedBuilder();
        if (event.getComponentId().equals("approve")) {
            insultSuggestion.setApprovalStatus("approved");
            eb.setTitle("Approved!");
            eb.setColor(Color.green);
            eb.setImage(APPROVED_IMAGE);
            event.reply("Suggestion approved!").setEphemeral(true).queue();
        } else if (event.getComponentId().equals("deny")) {
            insultSuggestion.setApprovalStatus("denied");
            eb.setTitle("Denied!");
            eb.setColor(Color.red);
            eb.setImage(DENIED_IMAGE);
            event.reply("Suggestion denied!").setEphemeral(true).queue();
        }
        eb.addField("Reviewer", "<@" + event.getUser().getId() + ">", false);
        iss.updateInsultSuggestion(insultID, insultSuggestion);
        final String description = oldEmbed.getDescription();
        eb.setDescription(description);
        final MessageEmbed me = eb.build();
        event.getMessage().editMessageEmbeds(me).queue();
        event.getMessage().editMessageComponents().queue();
    }
}
