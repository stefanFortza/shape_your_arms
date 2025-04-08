package multiplayer;

import java.util.*;
import com.google.gson.JsonObject;

/**
 * Handles all game logic and state
 */
public class GameWorld {
    private final Map<String, Player> players;
    private final List<Bullet> bullets;

    public GameWorld(Map<String, Player> players, List<Bullet> bullets) {
        this.players = players;
        this.bullets = bullets;
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

                    break;
                }
            }
        }
    }

    public Player createPlayer(String playerId) {
        // Create a new player at a random position
        Player newPlayer = new Player(playerId, Math.random() * 800, Math.random() * 600);
        players.put(playerId, newPlayer);
        return newPlayer;
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
}