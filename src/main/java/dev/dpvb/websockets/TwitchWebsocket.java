package dev.dpvb.websockets;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class TwitchWebsocket extends WebSocketClient {

    private final OkHttpClient http;
    private final Dotenv dotenv;

    public TwitchWebsocket(Dotenv dotenv) {
        super(URI.create("wss://eventsub.wss.twitch.tv/ws"));
        http = new OkHttpClient();
        this.dotenv = dotenv;
    }

    private void subscribe(String sessionID) {
        JSONObject json = new JSONObject();
        json.put("type", "stream.online");
        json.put("version", "1");
        JSONObject condition = new JSONObject();
        condition.put("broadcaster_user_id", "234166094");
        json.put("condition", condition);
        JSONObject transport = new JSONObject();
        transport.put("method", "websocket");
        transport.put("session_id", sessionID);
        json.put("transport", transport);
        final String jsonString = json.toString();
        final MediaType mediaType = MediaType.parse("application/json");
        final RequestBody body = RequestBody.create(jsonString, mediaType);

        System.out.println(jsonString);

        final String TWITCH_CLIENT_ID = dotenv.get("TWITCH_CLIENT_ID");
        // TODO get new token with refresh token
        final String USER_ACCESS_TOKEN = dotenv.get("TWITCH_USER_ACCESS_TOKEN");

        Request request = new Request.Builder()
                .url("https://api.twitch.tv/helix/eventsub/subscriptions")
                .post(body)
                .addHeader("Client-Id", TWITCH_CLIENT_ID)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .build();

        try {
            Response response = http.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (Exception e) {
            System.out.println("Error occured while attempt to subscribe to Twitch Websocket: " + e);
        }

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println(serverHandshake.getHttpStatusMessage());
        System.out.println("connection opened with twitch websocket server");
    }

    @Override
    public void onMessage(String s) {
        JSONObject json = new JSONObject(s);
        JSONObject metadata = json.getJSONObject("metadata");
        String messageType = metadata.getString("message_type");
        if (messageType.equals("session_welcome")) {
            final String sessionID = json.getJSONObject("payload").getJSONObject("session").getString("id");
            subscribe(sessionID);
        }

        System.out.println(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("Connection with Twitch WSS closed: " + s);
    }

    @Override
    public void onError(Exception e) {
        System.out.println("Error occurred with Twitch WSS: " + e);
    }
}
