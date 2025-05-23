package multiplayer;

import java.util.Random;

import multiplayer.networking.GameServerCoordinator;

public class GameLoop implements Runnable {
    private boolean running = true;
    private float deltaTime;
    private GameServerCoordinator gameServerInstance;

    public GameLoop(GameServerCoordinator gameServerCoordinator) {
        this.gameServerInstance = gameServerCoordinator;
    }

    @Override
    public void run() {
        // Initialize deltaTime
        deltaTime = 0f;
        Random random = new Random(); // Create once outside the loop

        long lastFrameTime = System.currentTimeMillis();

        while (running) {
            long currentTime = System.currentTimeMillis();
            deltaTime = (currentTime - lastFrameTime) / 1000f;
            lastFrameTime = currentTime;

            // System.out.println(deltaTime);

            // Update game using deltaTime from the previous frame
            gameServerInstance.update(deltaTime);

            // Simulate some work with occasional delay
            if (random.nextFloat() < .5f) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Cap frame rate to ~60 FPS
            long frameTime = System.currentTimeMillis() - currentTime;
            long sleepTime = 16 - frameTime; // ~60 FPS target

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        gameServerInstance.stop();
        running = false;
    }
}
