use godot::classes::input::MouseMode;
use godot::classes::{Input, viewport};
use godot::global::MouseButton;
use godot::prelude::*;

use crate::entities::player::Player;

use super::game_world_manager::GameWorldManager;

#[derive(GodotClass)]
#[class(base=Node2D)]
pub struct InputController {
    last_direction: Vector2,
    last_mouse_direction: Vector2,
    game_world_manager: OnReady<Gd<GameWorldManager>>,
    local_player: Option<Gd<Player>>,
    #[base]
    base: Base<Node2D>,
}

#[godot_api]
impl InputController {
    #[signal]
    pub fn move_input_direction_changed(direction: Vector2);

    #[signal]
    pub fn mouse_input_direction_changed(direction: Vector2);

    #[signal]
    pub fn mouse_clicked();

    #[func]
    pub fn on_local_player_instantiated(&mut self, player: Gd<Player>) {
        self.local_player = Some(player);
    }

    #[func]
    pub fn get_move_input_direction(&mut self) -> Vector2 {
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
    #[func]
    pub fn get_mouse_input_direction(&mut self) -> Vector2 {
        if let Some(player) = &self.local_player {
            let mut direction =
                self.base().get_global_mouse_position() - player.get_global_position();

            if direction.length() > 0.0 {
                direction = direction.normalized();
            }

            direction = (direction * 100.0).round() / 100.0;
            direction.y = -direction.y;
            // godot_print!("{} ", direction);
            return direction;
        }
        return Vector2::ZERO;
    }

    fn is_mouse_inside_window(&self) -> bool {
        let viewport = self.base().get_viewport().unwrap();
        let mouse_pos = viewport.get_mouse_position();
        let rect = viewport.get_visible_rect();

        rect.contains_point(mouse_pos)
    }
}

#[godot_api]
impl INode2D for InputController {
    fn init(base: Base<Node2D>) -> Self {
        Self {
            local_player: None,
            game_world_manager: OnReady::from_node(".."),
            last_direction: Vector2::ZERO,
            last_mouse_direction: Vector2::ZERO,
            base,
        }
    }

    fn ready(&mut self) {
        // Input::singleton().set_mouse_mode(MouseMode::CONFINED);
        // Input::singleton().ismo

        let this = self.to_gd();

        self.game_world_manager
            .signals()
            .local_player_instantiated()
            .connect_obj(&this, Self::on_local_player_instantiated);
    }

    fn physics_process(&mut self, _delta: f64) {
        if !self.is_mouse_inside_window() {
            return;
        }

        let input = Input::singleton();

        // Check for left mouse button click
        if input.is_mouse_button_pressed(MouseButton::LEFT) {
            self.signals().mouse_clicked().emit();
        }

        let direction = self.get_move_input_direction();

        let mouse_direction = self.get_mouse_input_direction();
        if mouse_direction != self.last_mouse_direction {
            self.last_mouse_direction = mouse_direction;
            self.signals()
                .mouse_input_direction_changed()
                .emit(mouse_direction);
        }

        if direction != self.last_direction {
            self.last_direction = direction;
            self.signals()
                .move_input_direction_changed()
                .emit(direction);
        }
    }
}
