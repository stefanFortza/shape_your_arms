package stefantacu.shape_your_arms.shape_your_arms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import multiplayer.GameLoop;
import multiplayer.GameServer;
import multiplayer.GameEndpointConfigurator;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@SpringBootApplication
public class ShapeYourArmsApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ShapeYourArmsApplication.class, args);

		// Get the managed GameServer bean
		GameServer gameServer = context.getBean(GameServer.class);

		// Start game loop (in background) with the Spring-managed GameServer
		GameLoop gameLoop = new GameLoop(gameServer);
		new Thread(gameLoop).start();
	}

	@Bean
	public GameServer gameServer() {
		GameServer gameServer = new GameServer();
		GameEndpointConfigurator.setGameServer(gameServer);
		return gameServer;
	}

	@ServerEndpoint(value = "/game", configurator = GameEndpointConfigurator.class)
	public static class GameEndpoint {
		@OnOpen
		public void onOpen(Session session) {
			GameServer gameServer = GameEndpointConfigurator.getGameServer();
			if (gameServer != null) {
				gameServer.handleConnect(session.getId(), session);
			}
		}

		// other methods similarly updated
	}
}
