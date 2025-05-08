use godot::classes::Input;
use godot::prelude::*;

#[derive(GodotClass)]
#[class(base=Node)]
pub struct InputController {
    last_direction: Vector2,
    #[base]
    base: Base<Node>,
}

#[godot_api]
impl InputController {
    #[signal]
    pub fn input_direction_changed(direction: Vector2);

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
impl INode for InputController {
    fn init(base: Base<Node>) -> Self {
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
            self.signals().input_direction_changed().emit(direction);
        }
    }
}
