use godot::prelude::*;

use crate::utils::{serializable_vector2::SerializableVector2, transform::Transform};

use super::messages::message_type::MessageType;

const PIXELS_PER_METER: f32 = 64.0; // Default value, can be adjusted

#[derive(Debug, Copy, Clone, Default)]
pub struct MetersPixelsConverter {
    pixels_per_meter: f32,
}

impl MetersPixelsConverter {
    pub fn new(pixels_per_meter: f32) -> Self {
        Self { pixels_per_meter }
    }

    pub fn default_converter() -> Self {
        Self {
            pixels_per_meter: PIXELS_PER_METER,
        }
    }

    pub fn pixels_to_meters(&self, pixels: f32) -> f32 {
        pixels / self.pixels_per_meter
    }

    pub fn meters_to_pixels(&self, meters: f32) -> f32 {
        meters * self.pixels_per_meter
    }

    pub fn vector_pixels_to_meters(&self, pixels_vector: Vector2) -> Vector2 {
        Vector2::new(
            self.pixels_to_meters(pixels_vector.x),
            self.pixels_to_meters(pixels_vector.y),
        )
    }

    pub fn vector_meters_to_pixels(&self, meters_vector: Vector2) -> Vector2 {
        Vector2::new(
            self.meters_to_pixels(meters_vector.x),
            self.meters_to_pixels(meters_vector.y),
        )
    }

    pub fn transform_pixels_to_meters(&self, transform: Transform) -> Transform {
        Transform {
            x: self.pixels_to_meters(transform.x),
            y: self.pixels_to_meters(transform.y),
            cost: transform.cost,
            sint: transform.sint,
        }
    }
    pub fn transform_meters_to_pixels(&self, transform: Transform) -> Transform {
        Transform {
            x: self.meters_to_pixels(transform.x),
            y: -self.meters_to_pixels(transform.y),
            cost: transform.cost,
            sint: transform.sint,
        }
    }

    pub fn convert_message_coordinates_to_pixels(&self, message: &MessageType) -> MessageType {
        let mut new_message = message.clone();
        match &mut new_message {
            MessageType::GameStateSync { players, bullets } => {
                for player in players.values_mut() {
                    player.transform = self.transform_meters_to_pixels(player.transform);
                }
                for bullet in bullets.values_mut() {
                    bullet.transform = self.transform_meters_to_pixels(bullet.transform);
                }
            }
            MessageType::PlayerMoveFromClient { direction, .. } => {
                *direction = SerializableVector2::new_from_vector2(
                    &self.vector_meters_to_pixels(direction.to_vector2()),
                );
            }
            MessageType::PlayerMouseDirectionFromClient {
                mouse_direction, ..
            } => {
                *mouse_direction = SerializableVector2::new_from_vector2(
                    &self.vector_meters_to_pixels(mouse_direction.to_vector2()),
                );
            }
            _ => {}
        }
        new_message
    }

    // Consider if you need to handle screen coordinates vs. world coordinates
    // The Java Camera class has offsetX and offsetY, and flips the Y-axis.
    // If you need similar logic for a camera view, you might add methods like:

    // pub fn world_to_screen_pixels(&self, world_pos_pixels: Vector2, screen_width: f32, screen_height: f32, camera_offset_pixels: Vector2) -> Vector2 {
    //     // Example: Y-axis flipped, origin at center of screen
    //     Vector2::new(
    //         world_pos_pixels.x + screen_width / 2.0 + camera_offset_pixels.x,
    //         -world_pos_pixels.y + screen_height / 2.0 - camera_offset_pixels.y,
    //     )
    // }

    // pub fn screen_to_world_pixels(&self, screen_pos_pixels: Vector2, screen_width: f32, screen_height: f32, camera_offset_pixels: Vector2) -> Vector2 {
    //     // Example: Y-axis flipped, origin at center of screen
    //     Vector2::new(
    //         screen_pos_pixels.x - screen_width / 2.0 - camera_offset_pixels.x,
    //         -(screen_pos_pixels.y - screen_height / 2.0 + camera_offset_pixels.y),
    //     )
    // }

    // pub fn world_meters_to_screen_pixels(&self, world_pos_meters: Vector2, screen_width_pixels: f32, screen_height_pixels: f32, camera_offset_meters: Vector2) -> Vector2 {
    //     let world_pos_pixels = self.vector_meters_to_pixels(world_pos_meters);
    //     let camera_offset_pixels = self.vector_meters_to_pixels(camera_offset_meters);
    //     // Example: Y-axis flipped, origin at center of screen
    //     Vector2::new(
    //         world_pos_pixels.x + screen_width_pixels / 2.0 + camera_offset_pixels.x,
    //         -world_pos_pixels.y + screen_height_pixels / 2.0 - camera_offset_pixels.y,
    //     )
    // }

    // pub fn screen_pixels_to_world_meters(&self, screen_pos_pixels: Vector2, screen_width_pixels: f32, screen_height_pixels: f32, camera_offset_meters: Vector2) -> Vector2 {
    //     let camera_offset_pixels = self.vector_meters_to_pixels(camera_offset_meters);
    //     // Example: Y-axis flipped, origin at center of screen
    //     let world_pos_pixels = Vector2::new(
    //         screen_pos_pixels.x - screen_width_pixels / 2.0 - camera_offset_pixels.x,
    //         -(screen_pos_pixels.y - screen_height_pixels / 2.0 + camera_offset_pixels.y),
    //     );
    //     self.vector_pixels_to_meters(world_pos_pixels)
    // }
}

#[cfg(test)]
mod tests {
    use super::*;
    use approx::assert_relative_eq; // For floating point comparisons

    #[test]
    fn test_default_converter_pixels_to_meters() {
        let converter = MetersPixelsConverter::default_converter();
        assert_relative_eq!(converter.pixels_to_meters(16.0), 1.0);
        assert_relative_eq!(converter.pixels_to_meters(32.0), 2.0);
        assert_relative_eq!(converter.pixels_to_meters(0.0), 0.0);
        assert_relative_eq!(converter.pixels_to_meters(8.0), 0.5);
    }

    #[test]
    fn test_default_converter_meters_to_pixels() {
        let converter = MetersPixelsConverter::default_converter();
        assert_relative_eq!(converter.meters_to_pixels(1.0), 16.0);
        assert_relative_eq!(converter.meters_to_pixels(2.0), 32.0);
        assert_relative_eq!(converter.meters_to_pixels(0.0), 0.0);
        assert_relative_eq!(converter.meters_to_pixels(0.5), 8.0);
    }

    #[test]
    fn test_custom_converter_pixels_to_meters() {
        let converter = MetersPixelsConverter::new(100.0); // 100 pixels per meter
        assert_relative_eq!(converter.pixels_to_meters(100.0), 1.0);
        assert_relative_eq!(converter.pixels_to_meters(50.0), 0.5);
    }

    #[test]
    fn test_custom_converter_meters_to_pixels() {
        let converter = MetersPixelsConverter::new(50.0); // 50 pixels per meter
        assert_relative_eq!(converter.meters_to_pixels(1.0), 50.0);
        assert_relative_eq!(converter.meters_to_pixels(0.5), 25.0);
    }

    #[test]
    fn test_vector_pixels_to_meters() {
        let converter = MetersPixelsConverter::default_converter();
        let pixel_vec = Vector2::new(32.0, 48.0);
        let meter_vec = converter.vector_pixels_to_meters(pixel_vec);
        assert_relative_eq!(meter_vec.x, 2.0);
        assert_relative_eq!(meter_vec.y, 3.0);
    }

    #[test]
    fn test_vector_meters_to_pixels() {
        let converter = MetersPixelsConverter::default_converter();
        let meter_vec = Vector2::new(1.5, 2.5);
        let pixel_vec = converter.vector_meters_to_pixels(meter_vec);
        assert_relative_eq!(pixel_vec.x, 24.0);
        assert_relative_eq!(pixel_vec.y, 40.0);
    }
}
