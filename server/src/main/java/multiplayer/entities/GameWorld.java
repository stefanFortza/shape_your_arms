// Rename to GameStateManager
package multiplayer.entities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import multiplayer.audit.AuditService;
import multiplayer.entities.entities_data.BulletData;
import multiplayer.entities.entities_data.PlayerData;
import multiplayer.entities.game_world_signal_data.OnPlayerJoinedGameWorldData;
import multiplayer.entities.game_world_signal_data.OnPlayerLeftGameWorldData;
import multiplayer.gui.GameWorldGUI;
import multiplayer.gui.framework.SimulationBody;
import multiplayer.networking.NetworkManager;
import multiplayer.networking.messages.GameStateSyncMessage;
import multiplayer.networking.messages.PlayerMouseDirectionFromClientMessage;
import multiplayer.networking.messages.PlayerShootMessageFromClient;
import multiplayer.networking.messages.SaveGameStateMessage;
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
    public Signal<GameState> gameStateSavedSignal = new Signal<>("gameStateSavedSignal");

    private final World<SimulationBody> world = new World<>();
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Map<String, Bullet> bullets = new ConcurrentHashMap<>();
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
        gameServerCoordinator.playerMouseDirectionReceivedSignal.connect(this::onPlayerMouseDirectionReceived);
        gameServerCoordinator.shootMessageReceivedSignal.connect(this::onShootMessageReceived);
        gameServerCoordinator.saveGameStateReceivedSignal.connect(this::onSaveMessageReceived);
        gameServerCoordinator.gameStateLoadedFromDatabaseSignal
                .connect(this::onGameStateLoadedFromDatabase);
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

    public void onPlayerMouseDirectionReceived(PlayerMouseDirectionFromClientMessage message) {

        Player player = players.get(message.getPlayerId());
        if (player != null) {
            player.setRotationByDirection(message.getMouseDirection());
            // System.out.println("Player " + message.getPlayerId() + " mouse direction: " +
            // message.getMouseDirection());
        } else {
            logger.warn("Player not found: " + message.getPlayerId());
        }

    }

    public void onShootMessageReceived(PlayerShootMessageFromClient message) {

        Player player = players.get(message.getPlayerId());
        if (player == null) {
            logger.warn("Player not found: " + message.getPlayerId());
            return;
        }
        if (!player.canShoot()) {
            System.out.println("Player " + message.getPlayerId() + " cannot shoot yet");
            return;
        }

        System.out.println("Player " + player.getShootCooldown() + " shoot cooldown");

        player.shoot();
        Bullet bullet = new Bullet(player.getTransform(), message.getPlayerId());
        bullets.put(bullet.getBulletId(), bullet);
        this.world.addBody(bullet);
        System.out.println("Player " + message.getPlayerId() + " shot a bullet");
    }

    public void onSaveMessageReceived(SaveGameStateMessage message) {
        // Save game state to database
        AuditService.logAction("Saving game state");
        System.out.println("Saving game state");
        gameStateSavedSignal.emit(getGameState());
    }

    public void onGameStateLoadedFromDatabase(GameStateSyncMessage gameState) {
        // Load game state from database
        System.out.println("Loading game state from database");
        AuditService.logAction("Loading game state from database");

        for (PlayerData playerData : gameState.getPlayers().values()) {
            Player player = players.get(playerData.getPlayerId());
            if (player != null) {
                player.setPlayerData(playerData);
            }
        }

        for (BulletData bulletData : gameState.getBullets().values()) {
            Bullet bullet = bullets.get(bulletData.getBulletId());
            if (bullet != null) {
                bullet.setBulletData(bulletData);
            }
        }

    }

    public void update(float deltaTime) {
        gameStateSyncSignal.emit(getGameState());
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
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets.values()) {
            bullet.update(deltaTime);
            if (bullet.isExpired()) {
                this.world.removeBody(bullet);
                bulletsToRemove.add(bullet);
            }
        }
        for (Bullet bullet : bulletsToRemove) {
            bullets.remove(bullet.getBulletId());
        }
    }

}