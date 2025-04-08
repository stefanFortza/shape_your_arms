package multiplayer;

import org.glassfish.tyrus.server.Server;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameServer {
    // WebSocket server
    private Server server;
    private final int PORT = 8887;

    // Game state
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
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
        this.networkManager = new NetworkManager(sessions, gson);

        // Start WebSocket server in a separate thread
        startWebSocketServer();
    }

    private void startWebSocketServer() {
        new Thread(() -> {
            try {
                server = new Server("localhost", PORT, "/websocket", null, GameEndpoint.class);
                server.start();
                System.out.println("WebSocket server started on port " + PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void update(float deltaTime) {
        // Update game world (bullets, collisions, etc)
        gameWorld.update(deltaTime);

        // Broadcast updated state to all clients
        networkManager.broadcastGameState(players, bullets);
    }

    // Client connection handling
    public void handleConnect(String playerId, Session session) {
        sessions.put(playerId, session);

        // Create new player
        Player newPlayer = gameWorld.createPlayer(playerId);

        // Send welcome message
        networkManager.sendWelcomeMessage(session, playerId, newPlayer);

        System.out.println("Player connected: " + playerId);
    }

    public void handleDisconnect(String playerId) {
        sessions.remove(playerId);
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

    // WebSocket endpoint
    @ServerEndpoint("/game")
    public static class GameEndpoint {
        private static GameServer gameServer;

        @OnOpen
        public void onOpen(Session session) {
            if (gameServer == null) {
                try {
                    gameServer = SpringContextUtil.getBean(GameServer.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (gameServer != null) {
                gameServer.handleConnect(session.getId(), session);
            }
        }

        @OnClose
        public void onClose(Session session) {
            if (gameServer != null) {
                gameServer.handleDisconnect(session.getId());
            }
        }

        @OnMessage
        public void onMessage(String message, Session session) {
            if (gameServer != null) {
                gameServer.handleMessage(session.getId(), message);
            }
        }

        @OnError
        public void onError(Session session, Throwable throwable) {
            System.err.println("Error for session " + session.getId());
            throwable.printStackTrace();
        }
    }
}