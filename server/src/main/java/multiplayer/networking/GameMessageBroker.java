// Rename to GameMessageBroker
package multiplayer.networking;

import org.java_websocket.WebSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import multiplayer.entities.Bullet;
import multiplayer.entities.GameState;
import multiplayer.entities.GameWorld;
import multiplayer.entities.Player;
import multiplayer.entities.game_world_signal_data.OnPlayerJoinedGameWorldData;
import multiplayer.entities.game_world_signal_data.OnPlayerLeftGameWorldData;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;

/**
 * Handles all network communication with clients
 */
public class GameMessageBroker {
    private final Map<String, WebSocket> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private GameWorld gameWorld;
    private GameServerCoordinator gameServerCoordinator;

    public GameMessageBroker(GameWorld gameWorld, GameServerCoordinator gameServerCoordinator) {
        this.gameWorld = gameWorld;
        this.gameServerCoordinator = gameServerCoordinator;
    }

    public void initSignalHandlers() {
        // Initialize signal handlers if needed
        gameServerCoordinator.clientConnectedSignal.connect(this::onClientConnected);
        gameServerCoordinator.clientDisconnectedSignal.connect(this::onClientDisconnected);

        gameWorld.playerJoinedGameWorldSignal.connect(this::onPlayerJoinedGameWorld);
        gameWorld.playerLeftGameWorldSignal.connect(this::onPlayerLeftGameWorld);
    }

    public void onClientConnected(OnClientConnectedData data) {
        connections.put(data.playerId(), data.connection());
    }

    public void onClientDisconnected(OnClientDisconnectedData data) {
        connections.remove(data.playerId());
    }

    public void onPlayerJoinedGameWorld(OnPlayerJoinedGameWorldData data) {
        Player player = data.newPlayer();
        GameState gameState = data.gameState();
        // Notify all clients about the new player
        JsonObject joinMsg = new JsonObject();
        joinMsg.addProperty("type", MessageType.PLAYER_JOINED.getType());
        joinMsg.addProperty("id", player.getId());
        joinMsg.addProperty("x", player.getX());
        joinMsg.addProperty("y", player.getY());

        System.out.println("Player joined: " + player.getId());

        sendWelcomeMessage(connections.get(player.getId()), player.getId(), player);

        broadcast(joinMsg.toString(), player.getId());

        // Send the initial game state to the new player
        WebSocket conn = connections.get(player.getId());
        if (conn != null && conn.isOpen()) {
            sendInitialGameState(conn, gameState);
        }
    }

    public void onPlayerLeftGameWorld(OnPlayerLeftGameWorldData data) {
        Player player = data.player();
        // Notify all clients about the player leaving
        JsonObject leaveMsg = new JsonObject();
        leaveMsg.addProperty("type", MessageType.PLAYER_LEFT.getType());
        leaveMsg.addProperty("id", player.getId());

        System.out.println("Player left: " + player.getId());

        broadcast(leaveMsg.toString(), player.getId());
    }

    public void broadcastPlayerJoinedGameWorld(String playerId, Player player) {
        JsonObject joinMsg = new JsonObject();
        joinMsg.addProperty("type", MessageType.PLAYER_JOINED.getType());
        joinMsg.addProperty("id", playerId);
        joinMsg.addProperty("x", player.getX());
        joinMsg.addProperty("y", player.getY());

        broadcast(joinMsg.toString(), playerId);
    }

    public void broadcastGameState(Map<String, Player> players, List<Bullet> bullets) {
        JsonObject gameState = new JsonObject();
        gameState.addProperty("type", MessageType.GAME_STATE.getType());
        gameState.add("players", gson.toJsonTree(players));
        gameState.add("bullets", gson.toJsonTree(bullets));

        broadcast(gameState.toString());
    }

    public void sendInitialGameState(WebSocket conn, GameState gameState) {
        JsonObject initialGameState = new JsonObject();
        initialGameState.addProperty("type", MessageType.INITIAL_GAME_STATE.getType());
        initialGameState.add("players", gson.toJsonTree(gameState.players()));
        initialGameState.add("bullets", gson.toJsonTree(gameState.bullets()));

        conn.send(initialGameState.toString());
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

    public void broadcast(String message, String excludePlayerId) {
        for (Map.Entry<String, WebSocket> entry : connections.entrySet()) {
            String playerId = entry.getKey();
            WebSocket conn = entry.getValue();

            if (!playerId.equals(excludePlayerId) && conn.isOpen()) {
                conn.send(message);
            }
        }
    }

    public void broadcast(String message) {
        for (WebSocket conn : connections.values()) {
            if (conn.isOpen()) {
                conn.send(message);
            }
        }
    }

    public void setGameWorld(GameWorld gameStateManager) {
        this.gameWorld = gameStateManager;
    }
}