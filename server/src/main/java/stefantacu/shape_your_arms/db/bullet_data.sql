CREATE TABLE IF NOT EXISTS bullet_data (
    id Integer PRIMARY KEY,
    bullet_id VARCHAR(255) NOT NULL,
    transform_x DOUBLE PRECISION NOT NULL,
    transform_y DOUBLE PRECISION NOT NULL,
    cost DOUBLE PRECISION NOT NULL,
    sint DOUBLE PRECISION NOT NULL,
    game_state_id INT NOT NULL,
    FOREIGN KEY (game_state_id) REFERENCES game_state(id)
);