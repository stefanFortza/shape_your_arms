package multiplayer;

import org.java_websocket.WebSocket;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
        gameState.addProperty("type", "gameState");
        gameState.add("players", gson.toJsonTree(players));
        gameState.add("bullets", gson.toJsonTree(bullets));

        broadcast(gameState.toString());
    }

    public void sendPlayerHit(String playerId, int damage, int currentHealth) {
        WebSocket conn = connections.get(playerId);
        if (conn != null && conn.isOpen()) {
            JsonObject hitMsg = new JsonObject();
            hitMsg.addProperty("type", "hit");
            hitMsg.addProperty("damage", damage);
            hitMsg.addProperty("health", currentHealth);

            conn.send(hitMsg.toString());
        }
    }

    public void broadcastPlayerDeath(String playerId) {
        JsonObject deathMsg = new JsonObject();
        deathMsg.addProperty("type", "playerDeath");
        deathMsg.addProperty("playerId", playerId);

        broadcast(deathMsg.toString());
    }

    public void sendWelcomeMessage(WebSocket conn, String playerId, Player player) {
        JsonObject welcomeMsg = new JsonObject();
        welcomeMsg.addProperty("type", "welcome");
        welcomeMsg.addProperty("id", playerId);
        welcomeMsg.addProperty("x", player.getX());
        welcomeMsg.addProperty("y", player.getY());

        conn.send(welcomeMsg.toString());
    }

    public void broadcastPlayerLeft(String playerId) {
        JsonObject disconnectMsg = new JsonObject();
        disconnectMsg.addProperty("type", "playerLeft");
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
}