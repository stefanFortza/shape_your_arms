package multiplayer.entities;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import multiplayer.entities.entities_data.BulletData;

public class Bullet extends GameObject {
    private static int BULLET_ID_COUNTER = 0;

    private String bulletId;
    private String ownerId;
    // private double speed = 500; // pixels per second
    private int damage = 10;
    private double lifetime = 2.0; // seconds until bullet expires

    public Bullet(Transform parentTransform, String ownerId) {
        super(parentTransform.getTranslation());
        Vector2 position = parentTransform.getTranslation();
        Vector2 direction = new Vector2(Math.cos(parentTransform.getRotation().toRadians()),
                Math.sin(parentTransform.getRotation().toRadians()));

        this.bulletId = "bullet" + BULLET_ID_COUNTER++;
        this.ownerId = ownerId;
        this.setAtRestDetectionEnabled(false);
        this.addFixture(Geometry.createCircle(0.1));
        this.setMass(MassType.NORMAL);
        this.setLinearVelocity(direction.multiply(50));
    }

    public BulletData getBulletData() {
        BulletData bulletData = new BulletData(this);
        return bulletData;
    }

    public void setBulletData(BulletData bulletData) {
        this.bulletId = bulletData.getBulletId();
        this.ownerId = bulletData.getOwnerId();
        this.damage = bulletData.getDamage();
        this.lifetime = bulletData.getLifetime();
        this.transform.setTranslation(bulletData.getTransform().getTranslation());
        this.transform.setRotation(bulletData.getTransform().getRotation());
    }

    public void update(float deltaTime) {

        if (isExpired()) {
            return; // Bullet is expired, do not update
        }

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

    public String getBulletId() {
        return bulletId;
    }

}
