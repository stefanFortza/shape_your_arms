use godot::classes::{ISprite2D, Sprite2D};
use godot::prelude::*;

#[derive(GodotClass)]
#[class(base=Sprite2D)]
struct Player {
    speed: f64,
    angular_speed: f64,
    #[base]
    base: Base<Sprite2D>,
}

#[godot_api]
impl ISprite2D for Player {
    fn init(base: Base<Sprite2D>) -> Self {
        Self {
            speed: 1000.0,
            angular_speed: std::f64::consts::PI,
            base,
        }
    }

    fn physics_process(&mut self, delta: f64) {
        let direction = self.get_input_direction();
        // godot_print!("{}", delta);
        self.update_position(direction, delta);
        self.rotate_player(delta);
    }
}

impl Player {
    fn get_input_direction(&self) -> Vector2 {
        let mut direction = Vector2::new(0.0, 0.0);

        if Input::singleton().is_action_pressed("move_right") {
            direction.x += 1.0;
        }

        if Input::singleton().is_action_pressed("move_left") {
            direction.x -= 1.0;
        }
        if Input::singleton().is_action_pressed("move_up") {
            direction.y -= 1.0;
        }
        if Input::singleton().is_action_pressed("move_down") {
            direction.y += 1.0;
        }

        if direction.length() > 0.0 {
            direction = direction.normalized();
        }

        direction
    }

    fn update_position(&mut self, direction: Vector2, delta: f64) {
        let mut new_position = self.base().get_position();
        new_position.x += direction.x * self.speed as f32 * delta as f32;
        new_position.y += direction.y * self.speed as f32 * delta as f32;
        self.base_mut().set_position(new_position);
    }

    fn rotate_player(&mut self, delta: f64) {
        let radians = (self.angular_speed * delta) as f32;
        self.base_mut().rotate(radians as f32);
    }
}
