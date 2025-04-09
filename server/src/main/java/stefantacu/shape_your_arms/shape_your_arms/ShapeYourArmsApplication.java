package stefantacu.shape_your_arms.shape_your_arms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import multiplayer.GameLoop;
import multiplayer.networking.GameServerCoordinator;

@SpringBootApplication
public class ShapeYourArmsApplication {

	public static void main(String[] args) {
		// ConfigurableApplicationContext context =
		// SpringApplication.run(ShapeYourArmsApplication.class, args);

		// // Get the managed GameServer bean
		// GameServerCoordinator gameServer =
		// context.getBean(GameServerCoordinator.class);

		// // Start game loop (in background) with the Spring-managed GameServer
		// GameLoop gameLoop = new GameLoop(gameServer);
		// new Thread(gameLoop).start();

		GameServerCoordinator gameServer = new GameServerCoordinator();

		// Start game loop (in background) with the Spring-managed GameServer
		GameLoop gameLoop = new GameLoop(gameServer);
		gameLoop.run();
		// new Thread(gameLoop).start();
	}

	// @Bean
	// public GameServerCoordinator gameServerCoordinator() {
	// GameServerCoordinator gameServer = new GameServerCoordinator();
	// return gameServer;
	// }
}
