// Rename to GameStateManager
package multiplayer.entities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import com.google.gson.JsonObject;

import multiplayer.audit.AuditService;
import multiplayer.entities.game_world_signal_data.OnPlayerJoinedGameWorldData;
import multiplayer.entities.game_world_signal_data.OnPlayerLeftGameWorldData;
import multiplayer.gui.GameWorldGUI;
import multiplayer.gui.framework.SimulationBody;
import multiplayer.networking.NetworkManager;
import multiplayer.networking.messages.move_messages.MoveMessageFromClient;
import multiplayer.networking.GameServerCoordinator;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;
import multiplayer.utils.Signal;

/**
 * Handles all game logic and state
 */
public class GameWorld {
    public static final Logger logger = LogManager.getLogger(GameWorld.class);

    public Signal<OnPlayerJoinedGameWorldData> playerJoinedGameWorldSignal = new Signal<>(
            "playerJoinedGameWorldSignal");
    public Signal<OnPlayerLeftGameWorldData> playerLeftGameWorldSignal = new Signal<>("playerLeftGameWorldSignal");

    public Signal<GameState> gameStateSyncSignal = new Signal<>("gameStateSyncSignal");

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
        logger.debug("GameWorld GUI initialized");
        AuditService.logAction("GameWorld GUI initialized");
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
        Player player = players.get(message.getPlayerId());

        if (player == null) {
            logger.warn("Player not found: " + message.getPlayerId());
            return;
        }

        switch (message.getMoveMessageType()) {
            case MOVEMENT_STARTED:
                // Handle player movement start
                AuditService
                        .logAction("Player " + player.getId() + " started moving in direction: " + direction);

                player.startMoving(direction);

                break;
            case MOVEMENT_STOPPED:
                // Handle player movement stop
                AuditService
                        .logAction("Player " + player.getId() + " stopped moving in direction: " + direction);
                System.out.println("Player " + message.getPlayerId() + " stopped moving");

                player.stopMoving();

                break;
        }
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
        // timer += deltaTime;
        // if (timer > 4) {
        // timer = 0;
        gameStateSyncSignal.emit(getGameState());
        // }

        this.gameWorldGUI.gameLoop(deltaTime);
        updatePlayers(deltaTime);
        updateBullets(deltaTime);

    }

    private void updatePlayers(float deltaTime) {
        for (Player player : players.values()) {
            player.update(deltaTime);
        }
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