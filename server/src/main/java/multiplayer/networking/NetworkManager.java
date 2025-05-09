// Rename to GameMessageBroker
package multiplayer.networking;

import org.java_websocket.WebSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import multiplayer.audit.AuditService;
import multiplayer.entities.GameState;
import multiplayer.entities.GameWorld;
import multiplayer.entities.Player;
import multiplayer.entities.game_world_signal_data.OnPlayerJoinedGameWorldData;
import multiplayer.entities.game_world_signal_data.OnPlayerLeftGameWorldData;
import multiplayer.networking.messages.GameStateSyncMessage;
import multiplayer.networking.messages.InitialGameStateMessage;
import multiplayer.networking.messages.MessageFactory;
import multiplayer.networking.messages.MessageType;
import multiplayer.networking.messages.PlayerJoinedMessage;
import multiplayer.networking.messages.PlayerLeftMessage;
import multiplayer.networking.messages.WelcomeMessage;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;

/**
 * Handles all network communication with clients
 */
public class NetworkManager {
    private final Map<String, WebSocket> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private GameWorld gameWorld;
    private GameServerCoordinator gameServerCoordinator;

    public NetworkManager(GameWorld gameWorld, GameServerCoordinator gameServerCoordinator) {
        this.gameWorld = gameWorld;
        this.gameServerCoordinator = gameServerCoordinator;
    }

    public void initSignalHandlers() {
        // Initialize signal handlers if needed
        gameServerCoordinator.clientConnectedSignal.connect(this::onClientConnected);
        gameServerCoordinator.clientDisconnectedSignal.connect(this::onClientDisconnected);

        gameWorld.playerJoinedGameWorldSignal.connect(this::onPlayerJoinedGameWorld);
        gameWorld.playerLeftGameWorldSignal.connect(this::onPlayerLeftGameWorld);
        gameWorld.gameStateSyncSignal.connect(this::onGameStateSync);
    }

    public void onClientConnected(OnClientConnectedData data) {
        connections.put(data.playerId(), data.connection());
    }

    public void onClientDisconnected(OnClientDisconnectedData data) {
        connections.remove(data.playerId());
    }

    public void onPlayerJoinedGameWorld(OnPlayerJoinedGameWorldData data) {
        Player player = data.newPlayer();
        // player.setLinearVelocity(new Vector2(0, 1));

        GameState gameState = data.gameState();
        PlayerJoinedMessage playerJoinedMessage = new PlayerJoinedMessage(player.getPlayerData());
        String joinMsg = MessageFactory.serializeMessage(playerJoinedMessage);

        System.out.println("Player joined: " + player.getId());
        System.out.println("Player message: " + joinMsg);

        sendWelcomeMessage(connections.get(player.getId()), player.getId(), player);

        broadcast(joinMsg, player.getId());

        // Send the initial game state to the new player
        WebSocket conn = connections.get(player.getId());
        if (conn != null && conn.isOpen()) {
            sendInitialGameState(conn, gameState);
        }
    }

    public void onPlayerLeftGameWorld(OnPlayerLeftGameWorldData data) {
        Player player = data.player();
        PlayerLeftMessage playerLeftMessage = new PlayerLeftMessage(player.getPlayerData());

        System.out.println("Player left: " + player.getId());

        broadcast(MessageFactory.serializeMessage(playerLeftMessage), player.getId());
    }

    public void onGameStateSync(GameState gameState) {
        GameStateSyncMessage gameStateSyncMessage = new GameStateSyncMessage(gameState);
        String gameStateSyncMsg = MessageFactory.serializeMessage(gameStateSyncMessage);
        AuditService
                .logAction("Game state sync message: " + gameStateSyncMsg);

        broadcast(gameStateSyncMsg);
    }

    public void sendInitialGameState(WebSocket conn, GameState gameState) {

        // InitialGameStateMessage initialGameStateMessage = new
        // InitialGameStateMessage(gameState);
        GameStateSyncMessage initialGameStateMessage = new GameStateSyncMessage(gameState);
        String initialGameState = MessageFactory.serializeMessage(initialGameStateMessage);

        conn.send(initialGameState);
    }

    public void sendWelcomeMessage(WebSocket conn, String playerId, Player player) {
        WelcomeMessage welcomeMessage = new WelcomeMessage(playerId);
        String welcomeMsg = MessageFactory.serializeMessage(welcomeMessage);

        if (conn != null && conn.isOpen()) {
            conn.send(welcomeMsg);
        }
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