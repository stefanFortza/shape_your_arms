use serde::{Deserialize, Serialize};

// "transform":{"cost":1.0,"sint":0.0,"x":0.0,"y":0.0},"health":100,"score":0}}

#[derive(Deserialize, Serialize, Debug)]
#[serde(rename_all = "camelCase")]
pub struct Transform {
    pub cost: f32,
    pub sint: f32,
    pub x: f32,
    pub y: f32,
}
