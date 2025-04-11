package multiplayer.entities;

public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize() {
        double magnitude = magnitude();
        if (magnitude != 0) {
            return new Vector2(x / magnitude, y / magnitude);
        } else {
            return new Vector2(0, 0); // Handle zero vector case
        }
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 multiply(double scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public double dotProduct(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }
}