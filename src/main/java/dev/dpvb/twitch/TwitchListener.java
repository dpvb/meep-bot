package dev.dpvb.twitch;

import dev.dpvb.exceptions.UnauthorizedException;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TwitchListener {

    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final OkHttpClient client;
    private final JDA jda;

    public TwitchListener(JDA jda) {
        this.jda = jda;
        final Dotenv env = Dotenv.load();
        CLIENT_ID = env.get("TWITCH_CLIENT_ID");
        CLIENT_SECRET = env.get("TWITCH_CLIENT_SECRET");
        client = new OkHttpClient();
    }

    public void start() {
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        final LiveChecker task = new LiveChecker();
        exec.scheduleAtFixedRate(task, 1, 1, java.util.concurrent.TimeUnit.MINUTES);
    }

    private @Nullable String getAccessToken() {
        final String grant_type = "client_credentials";

        final HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host("id.twitch.tv")
                .addPathSegment("oauth2")
                .addPathSegment("token")
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .addQueryParameter("grant_type", grant_type);

        final String url = urlBuilder.build().toString();

        final RequestBody body = RequestBody.create("{}", MediaType.parse("application/json"));

        final Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            return json.getString("access_token");
        } catch (Exception e) {
            System.out.println("Could not retrieve access token.");
            return null;
        }
    }

    private boolean isBungohStreaming(String accessToken) {
        final HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.twitch.tv")
                .addPathSegment("helix")
                .addPathSegment("streams")
                .addQueryParameter("user_login", "bungoh");

        final String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Client-Id", CLIENT_ID)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
        } catch (Exception e) {
            System.out.println("Could not retrieve stream data. Network error.");
            return false;
        }

        final int code = response.code();

        if (code == 200) {
            try {
                final JSONObject json = new JSONObject(response.body().string());
                final JSONArray data = json.getJSONArray("data");
                return !data.isEmpty();
            } catch (Exception e) {
                System.out.println("Could not retrieve stream data. JSON error.");
                return false;
            }
        } else if (code == 401) {
            throw new UnauthorizedException("Unauthorized. Regenerate access token.");
        }
        return false;
    }

    private class LiveChecker implements Runnable {

        private String accessToken;
        private boolean lastLive = false;
        private long channelID = 1225215583155650590L;

        public LiveChecker() {
            this.accessToken = getAccessToken();
        }

        @Override
        public void run() {
            // check if access token is null
            if (accessToken == null) {
                accessToken = getAccessToken();
            }

            // get current status of stream
            boolean live;
            try {
                live = isBungohStreaming(accessToken);
            } catch (UnauthorizedException e) {
                accessToken = getAccessToken();
                live = isBungohStreaming(accessToken);
            }

            // Status update
            System.out.println("bungoh stream status: " + (live ? "live" : "offline"));

            // if stream status goes from offline -> online
            if (live && lastLive != live) {
                sendLiveMessage();
                System.out.println("Bungoh is now live!");
            }

            // update lastLive
            lastLive = live;
        }

        public void sendLiveMessage() {
            TextChannel channel = jda.getTextChannelById(channelID);
            if (channel != null) {
                channel.sendMessage("@everyone Bungoh is now live @ https://twitch.tv/bungoh").queue();
            } else {
                System.out.println("Stream alerts channel not found.");
            }
        }
    }

}
