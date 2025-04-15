package multiplayer.physics;

import org.dyn4j.world.World;

import multiplayer.gui.framework.SimulationBody;

public class PhysicsWorld extends World<SimulationBody> {

    // Conversion factor between game units and physics units
    public static final double SCALE = 0.01;

    public PhysicsWorld() {
        // Create a new physics world with default gravity (0,0)
        this.setGravity(World.ZERO_GRAVITY);

    }
}