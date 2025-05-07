// Rename to GameStateManager
package multiplayer.entities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import multiplayer.entities.entities_data.PlayerData;
import multiplayer.entities.game_world_signal_data.OnPlayerJoinedGameWorldData;
import multiplayer.entities.game_world_signal_data.OnPlayerLeftGameWorldData;
import multiplayer.gui.GameWorldGUI;
import multiplayer.gui.framework.SimulationBody;
import multiplayer.networking.NetworkManager;
import multiplayer.networking.messages.InitialGameStateMessage;
import multiplayer.networking.messages.MessageFactory;
import multiplayer.networking.messages.MessageType;
import multiplayer.networking.messages.move_messages.MoveMessageFromClient;
import multiplayer.networking.GameServerCoordinator;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;
import multiplayer.utils.Signal;

/**
 * Handles all game logic and state
 */
public class GameWorld {
    public Signal<OnPlayerJoinedGameWorldData> playerJoinedGameWorldSignal = new Signal<>(
            "playerJoinedGameWorldSignal");
    public Signal<OnPlayerLeftGameWorldData> playerLeftGameWorldSignal = new Signal<>("playerLeftGameWorldSignal");

    private final World<SimulationBody> world = new World<>();
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final NetworkManager messageBroker;
    private final GameServerCoordinator gameServerCoordinator;
    private final GameWorldGUI gameWorldGUI;
    private float timer = 0;

    public GameWorld(NetworkManager messageBroker, GameServerCoordinator gameServerCoordinator) {
        this.messageBroker = messageBroker;
        this.gameServerCoordinator = gameServerCoordinator;

        gameWorldGUI = new GameWorldGUI(world);
        gameWorldGUI.run();
        System.out.println("GameWorld initialized");
    }

    public void initSignalHandlers() {
        // Initialize signal handlers if needed
        gameServerCoordinator.clientConnectedSignal.connect(this::onClientConnected);
        gameServerCoordinator.clientDisconnectedSignal.connect(this::onClientDisconnected);
        gameServerCoordinator.moveMessageReceivedSignal.connect(this::onMoveMessageReceived);
    }

    public void onClientConnected(OnClientConnectedData data) {

        Player newPlayer = new Player(new Vector2().zero(), data.playerId());
        // Create a new player at a random position
        players.put(data.playerId(), newPlayer);
        this.world.addBody(newPlayer);

        playerJoinedGameWorldSignal.emit(new OnPlayerJoinedGameWorldData(newPlayer, getGameState()));
    }

    public void onClientDisconnected(OnClientDisconnectedData data) {
        Player player = players.remove(data.playerId());

        this.world.removeBody(player);

        playerLeftGameWorldSignal.emit(new OnPlayerLeftGameWorldData(player));
    }

    public GameState getGameState() {
        return new GameState(players, bullets);
    }

    public void onMoveMessageReceived(MoveMessageFromClient message) {
        Vector2 direction = message.getDirection().getNormalized();

        switch (message.getMoveMessageType()) {
            case MOVEMENT_STARTED:
                // Handle player movement start
                System.out.println("Player " + message.getPlayerId() + " started moving in direction: " + direction);
                Player player = players.get(message.getPlayerId());

                if (player == null) {
                    System.out.println("Player not found: " + message.getPlayerId());
                    return;
                }

                // Set the player's linear velocity based on the direction
                player.setLinearVelocity(direction);
                break;
            case MOVEMENT_STOPPED:
                // Handle player movement stop
                System.out.println("Player " + message.getPlayerId() + " stopped moving");
                break;
        }
        // TODO: Handle player movement
        // Player player = players.get(playerId);
        // if (player != null) {
        // double x = message.get("x").getAsDouble();
        // double y = message.get("y").getAsDouble();

        // double rotation = message.has("rotation") ?
        // message.get("rotation").getAsDouble() : player.getRotation();

        // player.setPosition(new Vector2(x, y));
        // player.setRotation(rotation);

        // }
    }

    public void handlePlayerShoot(String playerId, JsonObject message) {
        Player player = players.get(playerId);
        if (player != null) {
            // double rotation = message.get("rotation").getAsDouble();

            // Calculate direction vector from rotation
            // double dirX = Math.cos(rotation);
            // double dirY = Math.sin(rotation);

            // TODO
            // Create bullet at player's position
            // Bullet bullet = new Bullet(
            // playerId,
            // player.getX(),
            // player.getY(),
            // dirX,
            // dirY);

            // bullets.add(bullet);

        }
    }

    public void update(float deltaTime) {
        timer += deltaTime;
        if (timer > 4) {
            timer = 0;

            // InitialGameStateMessage initialGameStateMessage = new
            // InitialGameStateMessage(getGameState());
            // String initialGameState =
            // MessageFactory.serializeMessage(initialGameStateMessage);
            // System.out.println(initialGameState);

            // Broadcast the game state to all clients
            // gameServerCoordinator.broadcastGameState(players, bullets);

        }

        this.gameWorldGUI.gameLoop(deltaTime);
        // this.world.update(deltaTime);
        updateBullets(deltaTime);
    }

    private void updateBullets(float deltaTime) {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(deltaTime);

            // Remove bullets that are out of bounds or expired
            if (bullet.isExpired()) {
                bulletIterator.remove();
            }
        }
    }

}