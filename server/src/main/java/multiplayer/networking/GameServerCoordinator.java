package multiplayer.networking;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import multiplayer.entities.GameWorld;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;
import multiplayer.utils.Signal;

public class GameServerCoordinator {
    // WebSocket server
    private WebSocketHandler socketServer;
    private final int PORT = 8887;

    // Game logic components
    private final GameWorld gameWorld;
    private final GameMessageBroker networkManager;

    // Signals
    public Signal<OnClientConnectedData> clientConnectedSignal = new Signal<>();

    public GameServerCoordinator() {

        // Initialize components
        this.networkManager = new GameMessageBroker(null, this);
        this.gameWorld = new GameWorld(networkManager, this);
        this.networkManager.setGameWorld(gameWorld);

        networkManager.initSignalHandlers();
        gameWorld.initSignalHandlers();

        // Set up signals

        socketServer = new WebSocketHandler(PORT, this);
        socketServer.setReuseAddr(true);
        socketServer.start();
        System.out.println("WebSocket server starting on port " + PORT);

        // Connect signals to handlers
        socketServer.clientConnectedSignal.connect(this::onClientConnected);
        socketServer.clientDisconnectedSignal.connect(this::onClientDisconnected);
    }

    public void update(float deltaTime) {
        // Update game world (bullets, collisions, etc)
        gameWorld.update(deltaTime);
    }

    public void onClientConnected(OnClientConnectedData data) {
        clientConnectedSignal.emit(data);
        System.out.println("Player connected: " + data.playerId());
    }

    public void onClientDisconnected(OnClientDisconnectedData data) {
        // // Remove player from game world and network manager
        // gameWorld.onClientDisconnected(playerId);
        // networkManager.onClientDisconnected(playerId);

        // System.out.println("Player disconnected: " + playerId);
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
}