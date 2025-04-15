package multiplayer.entities;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

public abstract class GameObject extends Body {

    public GameObject(Vector2 position) {
        this.transform.setTranslation(position);
    }

    public abstract void update(float deltaTime);
}