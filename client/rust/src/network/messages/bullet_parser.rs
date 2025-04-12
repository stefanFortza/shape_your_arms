use serde::Deserialize;

#[derive(Deserialize, Debug)]
pub struct Bullet {
    pub owner_id: String,
    pub x: f64,
    pub y: f64,
    pub dir_x: f64,
    pub dir_y: f64,
    #[serde(default = "default_speed")]
    pub speed: f64,
    #[serde(default = "default_damage")]
    pub damage: i32,
    #[serde(default = "default_lifetime")]
    pub lifetime: f64,
    #[serde(default = "default_radius")]
    pub radius: f64,
}

fn default_speed() -> f64 {
    500.0
}

fn default_damage() -> i32 {
    10
}

fn default_lifetime() -> f64 {
    2.0
}

fn default_radius() -> f64 {
    5.0
}
