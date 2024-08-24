package dev.dpvb.commands;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.MessageStats;
import dev.dpvb.mongo.services.MessageStatsService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

public class StatCheckCommand extends ListenerAdapter {

    private final MessageStatsService mss;

    public StatCheckCommand() {
        this.mss = MongoManager.getInstance().getMessageStatsService();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String eName = event.getName();
        if (eName.equals("statcheck")) {
            event.reply(getStatCheckMessage(event.getUser().getName())).queue();
        } else if (eName.equals("allstats")) {
            event.reply(getAllStatsMessage()).queue();
        }
    }

    private String getStatCheckMessage(String username) {
        MessageStats stats = mss.getUserMessageStats(username);
        if (stats == null) {
            return "Who are you?";
        }

        int plinks = stats.getPlinks();
        int buhs = stats.getBuhs();
        int mows = stats.getMows();
        int totalMessages = stats.getTotalMessages();

        return String.format(
                "You've plinked %d time%s, buh'd %d time%s, and mowed %d time%s. You have %d total meow%s.",
                plinks, sForNum(plinks), buhs, sForNum(buhs), mows, sForNum(mows), totalMessages,
                sForNum(totalMessages));
    }

    private String getAllStatsMessage() {
        Optional<CountContainer> total = mss.getAllMessageStats().stream().map(CountContainer::new)
                .reduce(CountContainer::combine);

        return total.map(s -> String.format(
                "The whole server has plinked %d time%s, buh'd %d time%s, and mowed %d time%s. There'%s been %d total meow%s.",
                s.plinks, sForNum(s.plinks), s.buhs, sForNum(s.buhs), s.mows, sForNum(s.mows),
                s.totalMessages == 1 ? "s" : "ve", s.totalMessages, sForNum(s.totalMessages)))
                .orElse("There are literally no messages...");
    }

    private String sForNum(int n) {
        if (n == 1)
            return "";
        return "s";
    }

    private static class CountContainer {

        private final int totalMessages;
        private final int plinks;
        private final int buhs;
        private final int mows;

        public CountContainer(MessageStats ms) {
            this.totalMessages = ms.getTotalMessages();
            this.plinks = ms.getPlinks();
            this.buhs = ms.getBuhs();
            this.mows = ms.getMows();
        }

        private CountContainer(int totalMessages, int plinks, int buhs, int mows) {
            this.totalMessages = totalMessages;
            this.plinks = plinks;
            this.buhs = buhs;
            this.mows = mows;
        }

        public static CountContainer combine(CountContainer l, CountContainer r) {
            return new CountContainer(
                    l.totalMessages + r.totalMessages,
                    l.plinks + r.plinks,
                    l.buhs + r.buhs,
                    l.mows + r.mows);
        }

    }

}
