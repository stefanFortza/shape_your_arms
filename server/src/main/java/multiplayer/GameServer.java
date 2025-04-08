package multiplayer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameServer {
    // WebSocket server
    private GameSocketServer socketServer;
    private final int PORT = 8887;

    // Game state
    private final Map<String, WebSocket> connections = new ConcurrentHashMap<>();
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Bullet> bullets = new ArrayList<>();

    // Game logic components
    private final GameWorld gameWorld;
    private final NetworkManager networkManager;

    // JSON handling
    private final Gson gson = new Gson();

    public GameServer() {
        // Initialize components
        this.gameWorld = new GameWorld(players, bullets);
        this.networkManager = new NetworkManager(connections, gson);

        // Start WebSocket server in a separate thread
        startWebSocketServer();
    }

    private void startWebSocketServer() {
        socketServer = new GameSocketServer(PORT);
        socketServer.start();
        System.out.println("WebSocket server starting on port " + PORT);
    }

    public void update(float deltaTime) {
        // Update game world (bullets, collisions, etc)
        gameWorld.update(deltaTime);

        // Broadcast updated state to all clients
        networkManager.broadcastGameState(players, bullets);
    }

    // Client connection handling
    public void handleConnect(String playerId, WebSocket connection) {
        connections.put(playerId, connection);

        // Create new player
        Player newPlayer = gameWorld.createPlayer(playerId);

        // Send welcome message
        networkManager.sendWelcomeMessage(connection, playerId, newPlayer);

        System.out.println("Player connected: " + playerId);
    }

    public void handleDisconnect(String playerId) {
        connections.remove(playerId);
        players.remove(playerId);

        // Notify other players
        networkManager.broadcastPlayerLeft(playerId);

        System.out.println("Player disconnected: " + playerId);
    }

    public void handleMessage(String playerId, String messageText) {
        try {
            // Parse the JSON message
            JsonObject message = JsonParser.parseString(messageText).getAsJsonObject();
            String type = message.get("type").getAsString();

            // Delegate to appropriate handler
            switch (type) {
                case "move":
                    gameWorld.handlePlayerMove(playerId, message);
                    break;

                case "shoot":
                    gameWorld.handlePlayerShoot(playerId, message);
                    break;

                default:
                    System.out.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + messageText);
            e.printStackTrace();
        }
    }

    /**
     * Inner class that implements the WebSocket server functionality
     */
    private class GameSocketServer extends WebSocketServer {

        public GameSocketServer(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            String playerId = UUID.randomUUID().toString();
            conn.setAttachment(playerId); // Store playerId with the connection
            handleConnect(playerId, conn);
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            String playerId = conn.getAttachment();
            if (playerId != null) {
                handleDisconnect(playerId);
            }
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            String playerId = conn.getAttachment();
            if (playerId != null) {
                handleMessage(playerId, message);
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.println("WebSocket error:");
            ex.printStackTrace();

            if (conn != null) {
                String playerId = conn.getAttachment();
                if (playerId != null) {
                    handleDisconnect(playerId);
                }
            }
        }

        @Override
        public void onStart() {
            System.out.println("WebSocket server started successfully on port " + PORT);
            setConnectionLostTimeout(30);
        }
    }
}