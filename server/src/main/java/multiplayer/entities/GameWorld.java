// Rename to GameStateManager
package multiplayer.entities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import com.google.gson.JsonObject;

import multiplayer.entities.game_world_signal_data.OnPlayerJoinedGameWorldData;
import multiplayer.entities.game_world_signal_data.OnPlayerLeftGameWorldData;
import multiplayer.networking.GameMessageBroker;
import multiplayer.networking.GameServerCoordinator;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;
import multiplayer.utils.Signal;

/**
 * Handles all game logic and state
 */
public class GameWorld {
    public Signal<OnPlayerJoinedGameWorldData> playerJoinedGameWorldSignal = new Signal<>();
    public Signal<OnPlayerLeftGameWorldData> playerLeftGameWorldSignal = new Signal<>();

    private final World<Body> world = new World<>();
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final GameMessageBroker messageBroker;
    private final GameServerCoordinator gameServerCoordinator;

    public GameWorld(GameMessageBroker messageBroker, GameServerCoordinator gameServerCoordinator) {
        this.messageBroker = messageBroker;
        this.gameServerCoordinator = gameServerCoordinator;
    }

    public void initSignalHandlers() {
        // Initialize signal handlers if needed
        gameServerCoordinator.clientConnectedSignal.connect(this::onClientConnected);
        gameServerCoordinator.clientDisconnectedSignal.connect(this::onClientDisconnected);
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

        playerLeftGameWorldSignal.emit(new OnPlayerLeftGameWorldData(player));
    }

    public GameState getGameState() {
        // Return a copy of the game state
        Map<String, Player> playersCopy = new HashMap<>(players);
        List<Bullet> bulletsCopy = new ArrayList<>(bullets);
        return new GameState(playersCopy, bulletsCopy);
    }

    public void handlePlayerMove(String playerId, JsonObject message) {
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
            double rotation = message.get("rotation").getAsDouble();

            // Calculate direction vector from rotation
            double dirX = Math.cos(rotation);
            double dirY = Math.sin(rotation);

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
        this.world.update(deltaTime);
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