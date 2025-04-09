// Rename to GameStateManager
package multiplayer.entities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;

import multiplayer.networking.GameMessageBroker;
import multiplayer.networking.GameServerCoordinator;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.utils.Signal;

/**
 * Handles all game logic and state
 */
public class GameWorld {
    public Signal<Player> playerJoinedSignal = new Signal<>();

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
    }

    public void onClientConnected(OnClientConnectedData data) {
        // Create a new player at a random position
        Player newPlayer = new Player(data.playerId(), Math.random() * 800, Math.random() * 600);
        players.put(data.playerId(), newPlayer);

        playerJoinedSignal.emit(newPlayer);
    }

    public void onClientDisconnected(String playerId) {
        players.remove(playerId);
    }

    public void handlePlayerMove(String playerId, JsonObject message) {
        Player player = players.get(playerId);
        if (player != null) {
            double x = message.get("x").getAsDouble();
            double y = message.get("y").getAsDouble();
            double rotation = message.has("rotation") ? message.get("rotation").getAsDouble() : player.getRotation();

            player.setPosition(x, y);
            player.setRotation(rotation);

        }
    }

    public void handlePlayerShoot(String playerId, JsonObject message) {
        Player player = players.get(playerId);
        if (player != null) {
            double rotation = message.get("rotation").getAsDouble();

            // Calculate direction vector from rotation
            double dirX = Math.cos(rotation);
            double dirY = Math.sin(rotation);

            // Create bullet at player's position
            Bullet bullet = new Bullet(
                    playerId,
                    player.getX(),
                    player.getY(),
                    dirX,
                    dirY);

            bullets.add(bullet);

        }
    }

    public void update(float deltaTime) {
        updateBullets(deltaTime);
        checkCollisions();
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

    public void checkCollisions() {
        // Bullet-player collision detection
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            for (Map.Entry<String, Player> entry : players.entrySet()) {
                String playerId = entry.getKey();
                Player player = entry.getValue();

                // Skip if the bullet belongs to this player
                if (bullet.getOwnerId().equals(playerId)) {
                    continue;
                }

                // Check collision
                if (bullet.collidesWith(player)) {
                    // Player takes damage
                    player.takeDamage(bullet.getDamage());

                    // Remove the bullet
                    bulletIterator.remove();

                    // Check if player died and handle respawn
                    if (player.getHealth() <= 0) {
                        player.respawn();
                    }

                    handleBulletCollision(bullet, player);

                    break;
                }
            }
        }
    }

    private void handleBulletCollision(Bullet bullet, Player player) {
    }
}