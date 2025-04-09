package multiplayer.entities;

public class Player {
    private String id;
    private double x;
    private double y;
    private double rotation;
    private int health = 100;
    private int score = 0;

    // Size for collision detection
    private double radius = 20;

    public Player(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0)
            this.health = 0;
    }

    public void respawn() {
        this.health = 100;
        this.x = Math.random() * 800;
        this.y = Math.random() * 600;
    }

    public void incrementScore() {
        this.score++;
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRotation() {
        return rotation;
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
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

}
