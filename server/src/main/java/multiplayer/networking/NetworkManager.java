package multiplayer.networking;

import org.java_websocket.WebSocket;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import multiplayer.entities.Bullet;
import multiplayer.entities.Player;

/**
 * Handles all network communication with clients
 */
public class NetworkManager {
    private final Map<String, WebSocket> connections;
    private final Gson gson;

    public NetworkManager(Map<String, WebSocket> connections, Gson gson) {
        this.connections = connections;
        this.gson = gson;
    }

    public void broadcastGameState(Map<String, Player> players, List<Bullet> bullets) {
        JsonObject gameState = new JsonObject();
        gameState.addProperty("type", MessageType.GAME_STATE.getType());
        gameState.add("players", gson.toJsonTree(players));
        gameState.add("bullets", gson.toJsonTree(bullets));

        broadcast(gameState.toString());
    }

    public void sendPlayerHit(String playerId, int damage, int currentHealth) {
        WebSocket conn = connections.get(playerId);
        if (conn != null && conn.isOpen()) {
            JsonObject hitMsg = new JsonObject();
            hitMsg.addProperty("type", MessageType.HIT.getType());
            hitMsg.addProperty("damage", damage);
            hitMsg.addProperty("health", currentHealth);

            conn.send(hitMsg.toString());
        }
    }

    public void broadcastPlayerDeath(String playerId) {
        JsonObject deathMsg = new JsonObject();
        deathMsg.addProperty("type", MessageType.PLAYER_DEATH.getType());
        deathMsg.addProperty("playerId", playerId);

        broadcast(deathMsg.toString());
    }

    public void sendWelcomeMessage(WebSocket conn, String playerId, Player player) {
        JsonObject welcomeMsg = new JsonObject();
        welcomeMsg.addProperty("type", MessageType.WELCOME.getType());
        welcomeMsg.addProperty("id", playerId);
        welcomeMsg.addProperty("x", player.getX());
        welcomeMsg.addProperty("y", player.getY());

        conn.send(welcomeMsg.toString());
        System.out.println("Sent welcome message to " + playerId);
    }

    public void broadcastPlayerLeft(String playerId) {
        JsonObject disconnectMsg = new JsonObject();
        disconnectMsg.addProperty("type", MessageType.PLAYER_LEFT.getType());
        disconnectMsg.addProperty("id", playerId);

        broadcast(disconnectMsg.toString());
    }

    public void broadcast(String message) {
        for (WebSocket conn : connections.values()) {
            if (conn.isOpen()) {
                conn.send(message);
            }
        }
    }

    public void handleMessage(String playerId, String messageText) {
        try {
            // Parse the JSON message
            JsonObject message = JsonParser.parseString(messageText).getAsJsonObject();
            String type = message.get("type").getAsString();

            // Convert the type to an enum
            MessageType messageType = MessageType.fromString(type);

            // Delegate to appropriate handler
            switch (messageType) {
                case GAME_STATE:
                    // Handle game state (if needed)
                    break;

                case HIT:
                    // Handle hit (if needed)
                    break;

                case PLAYER_DEATH:
                    // Handle player death (if needed)
                    break;

                case WELCOME:
                    // Handle welcome (if needed)
                    break;

                case PLAYER_LEFT:
                    // Handle player left (if needed)
                    break;

                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + messageText);
            e.printStackTrace();
        }
    }
}