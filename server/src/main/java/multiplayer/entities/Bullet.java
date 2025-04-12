package multiplayer.entities;

public class Bullet extends GameObject {
    private String ownerId;
    private Vector2 direction;
    private double speed = 500; // pixels per second
    private int damage = 10;
    private double lifetime = 2.0; // seconds until bullet expires
    private double radius = 5; // collision radius

    public Bullet(Vector2 position, String ownerId, Vector2 direction) {
        super(position);
        this.ownerId = ownerId;
        this.direction = direction.normalize();
    }

    public void update(float deltaTime) {

        Vector2 newPosition = this.position.add(direction.multiply(speed * deltaTime));
        this.position = newPosition;

        // // Move bullet
        // this.x += dirX * speed * deltaTime;
        // this.y += dirY * speed * deltaTime;

        // Decrease lifetime
        lifetime -= deltaTime;
    }

    public boolean isExpired() {
        return lifetime <= 0; // || x < 0 || x > 1000 || y < 0 || y > 1000; // Adjust bounds as needed
    }

    // Getters
    public String getOwnerId() {
        return ownerId;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public boolean collidesWith(GameObject other) {
        if (!(other instanceof Player)) {
            return false; // Only check collision with players
        }
        Player player = (Player) other;

        Vector2 diff = this.position.subtract(player.getPosition());

        // Simple circle collision detection
        double distance = diff.magnitude();

        return distance < (this.radius + player.getRadius());
    }
}
