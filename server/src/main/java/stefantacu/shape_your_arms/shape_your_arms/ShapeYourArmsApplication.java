package stefantacu.shape_your_arms.shape_your_arms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

import multiplayer.GameLoop;
import multiplayer.networking.GameServerCoordinator;

@SpringBootApplication
public class ShapeYourArmsApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ShapeYourArmsApplication.class, args);

		// // Get the managed GameServer bean
		// GameServerCoordinator gameServer =
		// context.getBean(GameServerCoordinator.class);

		// Start game loop (in background) with the Spring-managed GameServer
		GameLoop gameLoop = context.getBean(GameLoop.class);

		// Start the game loop in the background thread
		context.getBean(TaskExecutor.class).execute(gameLoop);

		// Alternatively, run directly (blocking):
		// gameLoop.run();
	}

	@Bean
	public GameServerCoordinator gameServerCoordinator() {
		GameServerCoordinator gameServer = new GameServerCoordinator();
		return gameServer;
	}

	@Bean
	public GameLoop gameLoop(GameServerCoordinator coordinator) {
		return new GameLoop(coordinator);
	}

	@Bean
	public TaskExecutor gameLoopExecutor(GameLoop gameLoop) {
		return new TaskExecutor() {
			private Thread thread;

			@Override
			public void execute(Runnable task) {
				thread = new Thread(gameLoop);
				thread.start();
			}

			@PreDestroy
			public void shutdown() {
				// Add shutdown method to GameLoop
				gameLoop.stop();
				if (thread != null) {
					thread.interrupt();
				}
			}
		};
	}
}
