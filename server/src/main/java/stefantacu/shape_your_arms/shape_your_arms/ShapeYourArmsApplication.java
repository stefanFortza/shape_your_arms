package stefantacu.shape_your_arms.shape_your_arms;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import multiplayer.GameLoop;
import multiplayer.networking.GameServerCoordinator;

// @SpringBootApplication
@SpringBootApplication(scanBasePackages = "stefantacu.shape_your_arms")
public class ShapeYourArmsApplication implements CommandLineRunner {

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

	@Override
	public void run(String... strings) throws Exception {

		jdbcTemplate.execute("DROP TABLE IF EXISTS customers");
		jdbcTemplate.execute("CREATE TABLE customers(" +
				"id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" "))
				.collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

		// Uses JdbcTemplate's batchUpdate operation to bulk load data
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		// log.info("Querying for customer records where first_name = 'Josh':");
		// jdbcTemplate.query(
		// "SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
		// (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"),
		// rs.getString("last_name")),
		// "Josh")
		// .forEach(customer -> log.info(customer.toString()));
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
