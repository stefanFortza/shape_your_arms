package multiplayer.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.world.World;

public class PhysicsWorld {
    private World<Body> world;

    // Conversion factor between game units and physics units
    public static final double SCALE = 0.01;

    public PhysicsWorld() {
        // Create a new physics world with default gravity (0,0)
        world = new World<Body>();
        world.setGravity(World.ZERO_GRAVITY);

        // Add contact listener
        // world.addContactListener(new ContactAdapter<Body>() {
        // @Override
        // public void collision(org.dyn4j.world.ContactCollisionData<Body> collision) {
        // // Handle collisions if needed
        // Body body1 = collision.getBody1();
        // Body body2 = collision.getBody2();

        // Object userData1 = body1.getUserData();
        // Object userData2 = body2.getUserData();

        // // You can handle collision responses here if needed
        // }
        // });
    }

    public void update(double deltaTime) {
        // Update the physics world
        world.update(deltaTime);
    }

    public World<Body> getWorld() {
        return world;
    }
}