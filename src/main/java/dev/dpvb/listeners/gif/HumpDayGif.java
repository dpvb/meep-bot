package dev.dpvb.listeners.gif;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.Calendar;
import java.util.TimeZone;

public class HumpDayGif extends Gif {

    public HumpDayGif() {
        super("HUMP DAY", false);
    }

    @Override
    public String getGifURL() {
        return "https://tenor.com/hEw3B9yGnxS.gif";
    }

    @Override
    public void send(String content, MessageChannelUnion channel) {
        int day = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.WEDNESDAY) {
            super.send(content, channel);
        }
    }
}
