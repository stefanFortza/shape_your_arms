use serde::{Deserialize, Serialize};

use crate::utils::transform::Transform;

#[derive(Deserialize, Serialize, Debug)]
#[serde(rename_all = "camelCase")]
pub struct BulletData {
    pub bullet_id: String,
    pub owner_id: String,
    pub transform: Transform,
    pub lifetime: f32,
    pub damage: i32,
}
