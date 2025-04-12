package multiplayer.entities;

public class Player extends GameObject {
    private String id;
    private int health = 100;
    private int score = 0;

    // Size for collision detection
    private double radius = 20;

    public Player(Vector2 position, String id) {
        super(position);
        this.id = id;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0)
            this.health = 0;
    }

    public void respawn() {
        this.health = 100;
        this.position = Vector2.getRandomVectorInRange(0, 800, 0, 600);
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

    @Override
    public void update(float deltaTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public boolean collidesWith(GameObject other) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'collidesWith'");
    }

}
