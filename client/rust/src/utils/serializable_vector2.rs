use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, PartialEq, Clone, Copy)]
pub struct SerializableVector2 {
    pub x: f32,
    pub y: f32,
}

impl SerializableVector2 {
    pub fn new(x: f32, y: f32) -> Self {
        Self { x, y }
    }
}
