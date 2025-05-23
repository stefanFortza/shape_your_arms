package stefantacu.shape_your_arms.shape_your_arms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import multiplayer.GameLoop;
import multiplayer.networking.GameServerCoordinator;
import stefantacu.shape_your_arms.db.DbService;

// @SpringBootApplication
@SpringBootApplication(scanBasePackages = "stefantacu.shape_your_arms")
public class ShapeYourArmsApplication {

	private static final Logger log = LoggerFactory.getLogger(ShapeYourArmsApplication.class);

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");

		ConfigurableApplicationContext context = SpringApplication.run(ShapeYourArmsApplication.class, args);

		// // Start game loop (in background) with the Spring-managed GameServer
		GameLoop gameLoop = context.getBean(GameLoop.class);

		// Start the game loop in the background thread
		context.getBean(TaskExecutor.class).execute(gameLoop);

	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Bean
	public GameServerCoordinator gameServerCoordinator(DbService dbService) {
		GameServerCoordinator gameServer = new GameServerCoordinator(dbService);
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
