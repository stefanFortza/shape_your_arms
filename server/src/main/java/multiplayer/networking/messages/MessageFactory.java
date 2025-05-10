package multiplayer.networking.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import multiplayer.networking.messages.move_messages.MoveMessageFromClient;
import multiplayer.networking.messages.move_messages.MoveMessageFromServer;
import multiplayer.networking.messages.move_messages.MoveMessageType;
import multiplayer.networking.messages.move_messages.MoveMessageTypeAdapter;

public class MessageFactory {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(MoveMessageType.class, new MoveMessageTypeAdapter())
            .create();

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
                // Manually parse the MoveMessageFromClient
                return gson.fromJson(json, MoveMessageFromClient.class);

            case PLAYER_MOVE_FROM_SERVER:
                return gson.fromJson(json, MoveMessageFromServer.class);

            case PLAYER_SHOOT:
                return gson.fromJson(json, ShootMessageFromClient.class);

            case INITIAL_GAME_STATE:
                return gson.fromJson(json, InitialGameStateMessage.class);

            case PLAYER_MOUSE_DIRECTION_FROM_CLIENT:
                return gson.fromJson(json, PlayerMouseDirectionFromClientMessage.class);

            default:
                throw new IllegalArgumentException("Unknown message type: " + type);
        }
    }
}