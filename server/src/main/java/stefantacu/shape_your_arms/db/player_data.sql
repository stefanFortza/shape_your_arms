CREATE TABLE IF NOT EXISTS player_data (
    id Integer PRIMARY KEY,
    player_id VARCHAR(255) NOT NULL,
    transform_x DOUBLE PRECISION NOT NULL,
    transform_y DOUBLE PRECISION NOT NULL,
    cost DOUBLE PRECISION NOT NULL,
    sint DOUBLE PRECISION NOT NULL,
    health INT NOT NULL,
    score INT NOT NULL,
    game_state_id INT NOT NULL,
    FOREIGN KEY (game_state_id) REFERENCES game_state(id)
);