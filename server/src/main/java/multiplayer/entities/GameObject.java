package multiplayer.entities;

import org.dyn4j.geometry.Vector2;

import multiplayer.gui.framework.SimulationBody;

public abstract class GameObject extends SimulationBody {

    public GameObject(Vector2 position) {
        this.transform.setTranslation(position);
    }

    public abstract void update(float deltaTime);
}