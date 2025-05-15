package multiplayer.entities.entities_data;

import org.dyn4j.geometry.Transform;

import multiplayer.entities.Bullet;

public class BulletData {
    private String bulletId;
    private String ownerId;
    private Transform transform;

    private int damage = 10;
    private double lifetime = 2.0; // seconds until bullet expires

    public BulletData() {
    }

    public BulletData(Bullet bullet) {
        this.bulletId = bullet.getBulletId();
        this.ownerId = bullet.getOwnerId();
        this.transform = bullet.getTransform();
        this.damage = bullet.getDamage();
        this.lifetime = bullet.getLifetime();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public int getDamage() {
        return damage;
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

    public void setBulletId(String bulletId) {
        this.bulletId = bulletId;
    }

}
