use godot::classes::{CharacterBody2D, ICharacterBody2D, Input};
use godot::prelude::*;

#[derive(GodotClass)]
#[class(base=CharacterBody2D)]
pub struct Player {
    last_direction: Vector2,
    #[base]
    base: Base<CharacterBody2D>,
}

#[godot_api]
impl Player {
    #[signal]
    pub fn player_direction_changed(direction: Vector2);

    #[func]
    pub fn get_input_direction(&mut self) -> Vector2 {
        let mut direction = Vector2::new(0.0, 0.0);

        if Input::singleton().is_action_pressed("move_right") {
            direction.x += 1.0;
        }
        if Input::singleton().is_action_pressed("move_left") {
            direction.x -= 1.0;
        }
        if Input::singleton().is_action_pressed("move_up") {
            direction.y += 1.0;
        }
        if Input::singleton().is_action_pressed("move_down") {
            direction.y -= 1.0;
        }

        if direction.length() > 0.0 {
            direction = direction.normalized();
        }

        direction
    }
}

#[godot_api]
impl ICharacterBody2D for Player {
    fn init(base: Base<CharacterBody2D>) -> Self {
        Self {
            last_direction: Vector2::ZERO,
            base,
        }
    }

    fn ready(&mut self) {}

    fn physics_process(&mut self, _delta: f64) {
        let direction = self.get_input_direction();

        if direction != self.last_direction {
            self.last_direction = direction;
            self.signals().player_direction_changed().emit(direction);
        }
    }
}
