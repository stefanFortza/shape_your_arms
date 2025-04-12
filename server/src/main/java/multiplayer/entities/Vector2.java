package multiplayer.entities;

public class Vector2 {
    public double x;
    public double y;

    public static Vector2 getRandomVector01() {
        return new Vector2(Math.random(), Math.random());
    }

    public static Vector2 getRandomVectorInRange(double min1, double max1, double min2, double max2) {
        double x = min1 + Math.random() * (max1 - min1);
        double y = min2 + Math.random() * (max2 - min2);
        return new Vector2(x, y);
    }

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

    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public double distance(Vector2 other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double angleBetween(Vector2 other) {
        double dotProduct = this.dotProduct(other);
        double magnitudes = this.magnitude() * other.magnitude();
        if (magnitudes == 0) {
            return 0; // Handle zero vector case to avoid division by zero
        }
        return Math.acos(dotProduct / magnitudes); // Returns angle in radians
    }

    public Vector2 multiply(double scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public double dotProduct(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}