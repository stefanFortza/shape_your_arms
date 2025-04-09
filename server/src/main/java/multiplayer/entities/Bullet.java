package multiplayer.entities;

public class Bullet {
    private String ownerId;
    private double x;
    private double y;
    private double dirX;
    private double dirY;
    private double speed = 500; // pixels per second
    private int damage = 10;
    private double lifetime = 2.0; // seconds until bullet expires
    private double radius = 5; // collision radius

    public Bullet(String ownerId, double x, double y, double dirX, double dirY) {
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;

        // Normalize direction vector
        double length = Math.sqrt(dirX * dirX + dirY * dirY);
        this.dirX = dirX / length;
        this.dirY = dirY / length;
    }

    public void update(float deltaTime) {
        // Move bullet
        this.x += dirX * speed * deltaTime;
        this.y += dirY * speed * deltaTime;

        // Decrease lifetime
        lifetime -= deltaTime;
    }

    public boolean isExpired() {
        return lifetime <= 0 || x < 0 || x > 1000 || y < 0 || y > 1000; // Adjust bounds as needed
    }

    public boolean collidesWith(Player player) {
        // Simple circle collision detection
        double dx = this.x - player.getX();
        double dy = this.y - player.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < (this.radius + player.getRadius());
    }

    // Getters
    public String getOwnerId() {
        return ownerId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDamage() {
        return damage;
    }
}
