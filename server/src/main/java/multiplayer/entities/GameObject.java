package multiplayer.entities;

public abstract class GameObject {
    protected Vector2 position;
    private double rotation;

    public GameObject(Vector2 position) {
        this.position = new Vector2(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public abstract void update(float deltaTime);

    public abstract boolean collidesWith(GameObject other);
}