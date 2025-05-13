package stefantacu.shape_your_arms.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    private final JdbcTemplate jdbcTemplate;

    // Constructor injection is generally recommended for required dependencies
    public AuthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello, %s!", name);
    }

    @GetMapping("/status")
    public String getStatus() {
        return "Application is running!";
    }

    // Example endpoint to fetch customers
    @GetMapping("/customers")
    public List<Map<String, Object>> getCustomers() {
        // Ensure the 'customers' table exists and has data from your CommandLineRunner
        return jdbcTemplate.queryForList("SELECT id, first_name, last_name FROM customers");
    }

    // Example endpoint to fetch a specific customer by first name
    @GetMapping("/customers/search")
    public List<Map<String, Object>> getCustomerByFirstName(@RequestParam String firstName) {
        return jdbcTemplate.queryForList("SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
                firstName);
    }
}