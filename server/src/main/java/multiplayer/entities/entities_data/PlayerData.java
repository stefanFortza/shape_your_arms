package multiplayer.entities.entities_data;

import org.dyn4j.geometry.Transform;

import multiplayer.entities.Player;

public class PlayerData {
    private String playerId;
    private Transform transform;

    private int health = 100;
    private int score = 0;

    public PlayerData() {
    }

    public PlayerData(Player player) {
        this.playerId = player.getId();
        this.transform = player.getTransform();
        this.health = player.getHealth();
        this.score = player.getScore();
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

}
