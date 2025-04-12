use godot::builtin::Vector2;
use serde::{
    Deserialize, Serialize,
    ser::{SerializeStruct, Serializer},
};

#[derive(Serialize, Deserialize, Debug, PartialEq, Clone, Copy)]
pub struct SerializableVector2 {
    pub x: f32,
    pub y: f32,
}

impl SerializableVector2 {
    pub fn new(x: f32, y: f32) -> Self {
        Self { x, y }
    }
    pub fn new_from_vector2(vector: &Vector2) -> Self {
        Self {
            x: vector.x,
            y: vector.y,
        }
    }

    pub fn to_vector2(&self) -> Vector2 {
        Vector2::new(self.x, self.y)
    }
}

// impl Serialize for SerializableVector2 {
//     fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
//     where
//         S: Serializer,
//     {
//         // 2 is the number of fields in the struct.
//         let mut state = serializer.serialize_struct("SerializableVector2", 2)?;
//         // Serialize the fields of the struct.
//         state.serialize_field("x", &self.x)?;
//         state.serialize_field("y", &self.y)?;
//         state.end()
//     }
// }
