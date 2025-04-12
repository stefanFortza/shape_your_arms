package multiplayer.networking.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageFactory {
    private static final Gson gson = new Gson();

    public static String serializeMessage(GameMessage message) {
        // Use Gson to convert the message to JSON
        String json = gson.toJson(message);
        return json;
    }

    public static GameMessage parseMessage(String json) {
        // First parse as JsonObject to get the type
        JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
        String type = jsonObj.get("type").getAsString();
        MessageType messageType = MessageType.fromString(type);

        // Then parse into the specific class
        switch (messageType) {
            case PLAYER_MOVE_FROM_CLIENT:
                return gson.fromJson(json, MoveMessageFromClient.class);
            case PLAYER_MOVE_FROM_SERVER:
                return gson.fromJson(json, MoveMessageFromServer.class);

            case PLAYER_SHOOT:
                return gson.fromJson(json, ShootMessage.class);
            // case GAME_STATE:
            // return gson.fromJson(json, GameStateMessage.class);
            // case HIT:
            // return gson.fromJson(json, HitMessage.class);
            // case PLAYER_DEATH:
            // return gson.fromJson(json, PlayerDeathMessage.class);
            // case WELCOME:
            // return gson.fromJson(json, WelcomeMessage.class);
            // case PLAYER_LEFT:
            // return gson.fromJson(json, PlayerLeftMessage.class);
            // case PLAYER_JOINED:
            // return gson.fromJson(json, PlayerJoinedMessage.class);
            // case INITIAL_GAME_STATE:
            // return gson.fromJson(json, InitialGameStateMessage.class);
            default:
                throw new IllegalArgumentException("Unknown message type: " + type);
        }

        // switch (type) {
        // case "move":
        // return gson.fromJson(json, MoveMessage.class);
        // case "shoot":
        // return gson.fromJson(json, ShootMessage.class);
        // default:
        // throw new IllegalArgumentException("Unknown message type: " + type);
        // }
    }
}