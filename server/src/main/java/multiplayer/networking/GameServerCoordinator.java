package multiplayer.networking;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import multiplayer.entities.GameWorld;
import multiplayer.networking.messages.GameMessage;
import multiplayer.networking.messages.MessageFactory;
import multiplayer.networking.messages.MoveMessageFromClient;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;
import multiplayer.networking.web_socket_signal_data.OnMessageReceivedData;
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
    public Signal<OnClientDisconnectedData> clientDisconnectedSignal = new Signal<>();

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
        socketServer.messageReceivedSignal.connect(this::onMessageReceived);
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
        clientDisconnectedSignal.emit(data);
        // // Remove player from game world and network manager
        // gameWorld.onClientDisconnected(playerId);
        // networkManager.onClientDisconnected(playerId);

        System.out.println("Player disconnected: " + data.playerId());
    }

    public void onMessageReceived(OnMessageReceivedData data) {
        String playerId = data.playerId();
        String messageText = data.message();
        WebSocket conn = data.connection();

        try {
            GameMessage parsedMessage = MessageFactory.parseMessage(messageText);

            if (parsedMessage instanceof MoveMessageFromClient message) {
                System.out.println("Move message received: " + message.getDirection());
            }
            // } else if (parsedMessage instanceof ShootMessage message) {
            // System.out.println("Shoot message received: " + message.getRotation());
            // }

            // // Delegate to appropriate handler
            // switch (type) {
            // case "move":
            // gameWorld.handlePlayerMove(playerId, parsedMessage);
            // break;

            // case "shoot":
            // gameWorld.handlePlayerShoot(playerId, parsedMessage);
            // break;

            // default:
            // System.out.println("Unknown message type: " + type);
            // }
        } catch (Exception e) {
            System.err.println("Error processing message: " + messageText);
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            socketServer.stop();
        } catch (InterruptedException e) {
            System.err.println("Error stopping WebSocket server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}