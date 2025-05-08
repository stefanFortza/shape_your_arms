use godot::classes::{CharacterBody2D, ICharacterBody2D, Input};
use godot::prelude::*;

use crate::network::messages::player_data::PlayerData;
use crate::utils::transform;

#[derive(GodotClass)]
#[class(base=CharacterBody2D)]
pub struct Player {
    last_direction: Vector2,
    #[base]
    base: Base<CharacterBody2D>,
}

#[godot_api]
pub impl Player {
    pub fn apply_network_state(&mut self, player_data: PlayerData) {
        godot_print!("Applying network state: {:?}", player_data);
        let transform = player_data.transform;
        self.base_mut()
            .set_position(Vector2::new(transform.x, transform.y));

        let rotation = transform.sint.atan2(transform.cost);

        self.base_mut().set_rotation(rotation);
    }

    #[func]
    pub fn applu_local_input(&mut self) {}
}

#[godot_api]
impl ICharacterBody2D for Player {
    fn init(base: Base<CharacterBody2D>) -> Self {
        Self {
            last_direction: Vector2::ZERO,
            base,
        }
    }
}
