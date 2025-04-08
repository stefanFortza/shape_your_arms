package multiplayer;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Handles all network communication with clients
 */
public class NetworkManager {
    private final Map<String, Session> sessions;
    private final Gson gson;

    public NetworkManager(Map<String, Session> sessions, Gson gson) {
        this.sessions = sessions;
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
        Session session = sessions.get(playerId);
        if (session != null) {
            JsonObject hitMsg = new JsonObject();
            hitMsg.addProperty("type", "hit");
            hitMsg.addProperty("damage", damage);
            hitMsg.addProperty("health", currentHealth);

            try {
                session.getBasicRemote().sendText(hitMsg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastPlayerDeath(String playerId) {
        JsonObject deathMsg = new JsonObject();
        deathMsg.addProperty("type", "playerDeath");
        deathMsg.addProperty("playerId", playerId);

        broadcast(deathMsg.toString());
    }

    public void sendWelcomeMessage(Session session, String playerId, Player player) {
        JsonObject welcomeMsg = new JsonObject();
        welcomeMsg.addProperty("type", "welcome");
        welcomeMsg.addProperty("id", playerId);
        welcomeMsg.addProperty("x", player.getX());
        welcomeMsg.addProperty("y", player.getY());

        try {
            session.getBasicRemote().sendText(welcomeMsg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastPlayerLeft(String playerId) {
        JsonObject disconnectMsg = new JsonObject();
        disconnectMsg.addProperty("type", "playerLeft");
        disconnectMsg.addProperty("id", playerId);

        broadcast(disconnectMsg.toString());
    }

    public void broadcast(String message) {
        for (Session session : sessions.values()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}