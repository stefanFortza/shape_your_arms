package multiplayer.entities;

import org.dyn4j.geometry.Vector2;

import multiplayer.entities.entities_data.BulletData;

public class Bullet extends GameObject {
    private String ownerId;
    // private double speed = 500; // pixels per second
    private int damage = 10;
    private double lifetime = 2.0; // seconds until bullet expires

    public Bullet(Vector2 position, String ownerId, Vector2 direction) {
        super(position);
        this.ownerId = ownerId;
    }

    public BulletData getBulletData() {
        BulletData bulletData = new BulletData(this);
        return bulletData;
    }

    public void update(float deltaTime) {

        // Vector2 newPosition = this.position.add(direction.multiply(speed *
        // deltaTime));
        // this.position = newPosition;

        // // Move bullet
        // this.x += dirX * speed * deltaTime;
        // this.y += dirY * speed * deltaTime;

        // Decrease lifetime
        lifetime -= deltaTime;
    }

    public boolean isExpired() {
        return lifetime <= 0; // || x < 0 || x > 1000 || y < 0 || y > 1000; // Adjust bounds as needed
    }

    public Vector2 getDirection() {
        double angle = this.getTransform().getRotation().toRadians();
        return new Vector2(Math.cos(angle), Math.sin(angle));
    }

    // Getters
    public String getOwnerId() {
        return ownerId;
    }

    public int getDamage() {
        return damage;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public double getLifetime() {
        return lifetime;
    }

    public void setLifetime(double lifetime) {
        this.lifetime = lifetime;
    }

}
