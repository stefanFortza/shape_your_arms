CREATE TABLE IF NOT EXISTS game_state (
    id SERIAL PRIMARY KEY NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS player_data (
    id SERIAL PRIMARY KEY,
    player_id VARCHAR(255) NOT NULL,
    transform_x DOUBLE PRECISION NOT NULL,
    transform_y DOUBLE PRECISION NOT NULL,
    health INT NOT NULL,
    score INT NOT NULL,
    game_state_id INT NOT NULL,
    FOREIGN KEY (game_state_id) REFERENCES game_state(id)
);

CREATE TABLE IF NOT EXISTS bullet_data (
    id SERIAL PRIMARY KEY,
    bullet_id VARCHAR(255) NOT NULL,
    transform_x DOUBLE PRECISION NOT NULL,
    transform_y DOUBLE PRECISION NOT NULL,
    game_state_id INT NOT NULL,
    FOREIGN KEY (game_state_id) REFERENCES game_state(id)
);
