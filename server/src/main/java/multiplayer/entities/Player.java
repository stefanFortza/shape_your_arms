package multiplayer.entities;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import multiplayer.entities.entities_data.PlayerData;

public class Player extends GameObject {
    private String id;
    private int health = 100;
    private int score = 0;
    private boolean isMoving = false;
    private Vector2 direction;
    private float shootCooldown = 0;

    public Player(Vector2 position, String id) {
        super(position);
        this.id = id;
        this.setAtRestDetectionEnabled(false);

        // this.radius = 0.5; // Set the radius of the player

        // this.addFixture(Geometry.createRectangle(1.0, 1.5));
        this.addFixture(Geometry.createCircle(0.35));
        this.setMass(MassType.NORMAL);

        this.getFixture(0).setFriction(0);
        this.getFixture(0).setRestitution(0);
    }

    public PlayerData getPlayerData() {
        PlayerData playerData = new PlayerData(this);
        return playerData;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0)
            this.health = 0;
    }

    public void respawn() {
        this.health = 100;
        this.transform.setTranslation(new Vector2().zero());
    }

    public void incrementScore() {
        this.score++;
    }

    public int getHealth() {
        return health;
    }

    public int getScore() {
        return score;
    }

    public double getRadius() {
        return radius;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void startMoving(Vector2 direction) {
        this.isMoving = true;
        this.direction = direction.getNormalized();
    }

    public void stopMoving() {
        this.isMoving = false;
        this.direction = new Vector2(0, 0);
    }

    public void setRotationByDirection(Vector2 direction) {
        // Calculate the angle based on the direction vector
        double angle = Math.atan2(direction.y, direction.x);
        this.transform.setRotation(angle);
    }

    public boolean canShoot() {
        return shootCooldown <= 0;
    }

    public void shoot() {
        System.out.println("Shooting!");
        this.shootCooldown = 1.0f; // Set the cooldown to 1 second
    }

    @Override
    public void update(float deltaTime) {
        if (isMoving) {
            Vector2 newVeclocity = new Vector2(this.direction).multiply(300).multiply(deltaTime);

            // Update the player's position based on the direction
            this.setLinearVelocity(newVeclocity);
        } else {
            // Stop the player's movement
            this.setLinearVelocity(new Vector2(0, 0));
        }

        // Update the shoot cooldown
        if (this.shootCooldown > 0) {
            this.shootCooldown -= deltaTime;
        }

        // this.getTransform().setRotation()
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public float getShootCooldown() {
        return shootCooldown;
    }
}
