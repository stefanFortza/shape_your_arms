package stefantacu.shape_your_arms.db;

import org.dyn4j.geometry.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import multiplayer.entities.GameState;
import multiplayer.entities.entities_data.BulletData;
import multiplayer.entities.entities_data.PlayerData;
import multiplayer.networking.messages.GameStateSyncMessage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DbService {

    private static final Logger log = LoggerFactory.getLogger(DbService.class);
    private final JdbcTemplate jdbcTemplate;

    public DbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // players, bullets, game_state, bombs
    @PostConstruct
    public void initialize() {
        try {
            String basePath = "src/main/java/stefantacu/shape_your_arms/db/";
            String gameStateSql = Files.readString(Paths.get(basePath + "game_state.sql"));
            String playerDataSql = Files.readString(Paths.get(basePath + "player_data.sql"));
            String bulletDataSql = Files.readString(Paths.get(basePath + "bullet_data.sql"));

            jdbcTemplate.execute(gameStateSql);
            jdbcTemplate.execute(playerDataSql);
            jdbcTemplate.execute(bulletDataSql);
        } catch (Exception e) {
            log.error("Failed to initialize database tables", e);
        }
    }

    public void saveGameState(GameState gameState) {
        GameStateSyncMessage gameStateSyncMessage = new GameStateSyncMessage(gameState);

        String insertGameStateSql;
        try {
            insertGameStateSql = Files
                    .readString(Paths.get("src/main/java/stefantacu/shape_your_arms/db/insert_game_state.sql"));
        } catch (Exception e) {
            log.error("Failed to read insert_game_state.sql", e);
            return;
        }

        Integer id = jdbcTemplate.queryForObject(insertGameStateSql, Integer.class);

        if (id == null) {
            log.error("Failed to retrieve game state id after insertion.");
            return;
        }

        for (PlayerData playerData : gameStateSyncMessage.getPlayers().values()) {
            savePlayerData(playerData, id);
        }

        for (BulletData bulletData : gameStateSyncMessage.getBullets().values()) {
            saveBulletData(bulletData, id);
        }

    }

    public void savePlayerData(PlayerData playerData, int gameStateId) {
        String sql = "INSERT INTO player_data (player_id, transform_x, transform_y, cost, sint, health, score, game_state_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, playerData.getPlayerId(), playerData.getTransform().getTranslationX(),
                playerData.getTransform().getTranslationY(), playerData.getTransform().getRotation().getCost(),
                playerData.getTransform().getRotation().getSint(), playerData.getHealth(), playerData.getScore(),
                gameStateId);
    }

    public void saveBulletData(BulletData bulletData, int gameStateId) {
        String sql = "INSERT INTO bullet_data (bullet_id, transform_x, transform_y, cost, sint, game_state_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, bulletData.getBulletId(), bulletData.getTransform().getTranslationX(),
                bulletData.getTransform().getTranslationY(), bulletData.getTransform().getRotation().getCost(),
                bulletData.getTransform().getRotation().getSint(),
                gameStateId);
    }

    public Optional<GameStateSyncMessage> getLastGameState() {
        // 1. Query game_state
        String gameStateSql = "select * from game_state where id = (select max(id) from  game_state)";
        Integer gameStateId = jdbcTemplate.queryForObject(gameStateSql, (rs, rowNum) -> {
            return rs.getInt("id");
        });

        if (gameStateId == null) {
            log.error("Failed to retrieve game state id.");
            return Optional.empty();
        }

        GameStateSyncMessage gs = new GameStateSyncMessage();

        // 2. Query players
        Map<String, PlayerData> players = getPlayersByGameStateId(gameStateId);

        Map<String, BulletData> bullets = getBulletsByGameStateId(gameStateId);

        // 4. Set players and bullets to gameState
        gs.setPlayers(players);
        gs.setBullets(bullets);

        return Optional.ofNullable(gs);
    }

    public HashMap<String, PlayerData> getPlayersByGameStateId(int gameStateId) {
        String playerSql = "SELECT * FROM player_data WHERE game_state_id = ?";

        List<PlayerData> players = jdbcTemplate.query(playerSql, new Object[] { gameStateId }, (rs, rowNum) -> {
            PlayerData pd = new PlayerData();
            pd.setPlayerId(rs.getString("player_id"));

            Transform transform = new Transform();
            transform.setTranslation(rs.getDouble("transform_x"), rs.getDouble("transform_y"));
            double cost = rs.getDouble("cost");
            double sint = rs.getDouble("sint");
            double theta = Math.atan2(sint, cost);
            transform.setRotation(theta);

            pd.setTransform(transform);
            pd.setHealth(rs.getInt("health"));
            pd.setScore(rs.getInt("score"));
            // pd.set... (populate fields from rs)
            return pd;
        });

        HashMap<String, PlayerData> playerMap = new HashMap<>();
        for (PlayerData player : players) {
            playerMap.put(player.getPlayerId(), player);
        }

        return playerMap;
    }

    public HashMap<String, BulletData> getBulletsByGameStateId(int gameStateId) {
        String bulletSql = "SELECT * FROM bullet_data WHERE game_state_id = ?";

        List<BulletData> bullets = jdbcTemplate.query(bulletSql, new Object[] { gameStateId }, (rs, rowNum) -> {
            BulletData bd = new BulletData();
            bd.setBulletId(rs.getString("bullet_id"));

            Transform transform = new Transform();
            transform.setTranslation(rs.getDouble("transform_x"), rs.getDouble("transform_y"));
            double cost = rs.getDouble("cost");
            double sint = rs.getDouble("sint");
            double theta = Math.atan2(sint, cost);
            transform.setRotation(theta);

            bd.setTransform(transform);
            // bd.setDamage(rs.getInt("damage"));
            return bd;
        });

        HashMap<String, BulletData> bulletMap = new HashMap<>();
        for (BulletData bullet : bullets) {
            bulletMap.put(bullet.getBulletId(), bullet);
        }

        return bulletMap;
    }

}