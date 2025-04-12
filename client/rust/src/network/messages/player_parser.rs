use serde::Deserialize;

#[derive(Deserialize, Debug)]
pub struct Player {
    pub id: String,
    pub x: f32,
    pub y: f32,
    pub rotation: f32,
    pub health: u32,
    pub score: u32,
    pub radius: f32,
}
