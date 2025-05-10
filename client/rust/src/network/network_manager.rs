use godot::classes::{INode, Input, Node};
use godot::prelude::*;

use crate::entities::player::Player;
use crate::main_game::input_controller::{self, InputController};
use crate::utils::serializable_vector2::SerializableVector2;

use super::messages::message_type::MessageType;

#[derive(GodotClass)]
#[class(base=Node)]
pub struct NetwornkManager {
    player_id: Option<GString>,
    input_controller: OnReady<Gd<InputController>>,

    #[base]
    base: Base<Node>,
}

#[godot_api]
impl INode for NetwornkManager {
    fn init(base: Base<Node>) -> Self {
        Self {
            input_controller: OnReady::from_node("../../InputController"),
            player_id: None,
            base,
        }
    }

    fn ready(&mut self) {
        godot_print!("NetworkManager is ready");

        let this = self.to_gd();

        self.input_controller
            .signals()
            .move_input_direction_changed()
            .connect_obj(&this, Self::on_move_input_direction_changed);

        self.input_controller
            .signals()
            .mouse_input_direction_changed()
            .connect_obj(&this, Self::on_mouse_input_direction_changed);
    }
}

#[godot_api]
pub impl NetwornkManager {
    #[signal]
    pub fn message_serialized(message: GString);

    #[signal]
    pub fn welcome_message_received(welcome_message: GString);

    #[signal]
    pub fn player_joined_message_received(player_joined_message: GString);

    #[signal]
    pub fn player_left_message_received(player_joined_message: GString);

    #[signal]
    pub fn game_state_sync_received(game_state_sync: GString);

    #[func]
    fn on_message_received(&mut self, message: GString) {
        // godot_print!("Message received: {:?}", message);
        let parsed_message = MessageType::from_json(message.to_string().as_str());
        if let Ok(parsed_message) = parsed_message {
            match parsed_message {
                MessageType::Welcome {
                    player_id,
                    player_data: _,
                } => {
                    godot_print!("Welcome message received with player ID: {:?}", player_id);
                    let player_id = player_id.to_godot();
                    self.player_id = Some(player_id.clone());
                    self.signals().welcome_message_received().emit(message);
                }
                MessageType::GameStateSync {
                    players: _,
                    bullets: _,
                } => {
                    self.signals().game_state_sync_received().emit(message);
                }
                MessageType::PlayerJoined { player_data: _ } => {
                    self.signals()
                        .player_joined_message_received()
                        .emit(message);
                }
                MessageType::PlayerLeft { player_data: _ } => {
                    self.signals().player_left_message_received().emit(message);
                }

                // Handle other message types here
                _ => {
                    godot_print!("Other message type received : {:?}", parsed_message);
                }
            }
        } else {
            godot_error!("Failed to deserialize message : {:?}", message);
        }
        // Handle incoming messages
        // godot_print!("Message received: {}", message);
    }

    #[func]
    fn on_move_input_direction_changed(&mut self, direction: Vector2) {
        // Handle player movement

        let move_message_type = if direction != Vector2::ZERO {
            "movementStarted"
        } else {
            "movementStopped"
        };

        let message = MessageType::PlayerMoveFromClient {
            player_id: self.player_id.clone().unwrap_or_default().to_string(),
            direction: SerializableVector2::new_from_vector2(&direction),
            move_message_type: move_message_type.to_string(),
        };

        if let Ok(json) = MessageType::to_json(&message) {
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize player movement");
        }
    }

    #[func]
    fn on_mouse_input_direction_changed(&mut self, direction: Vector2) {
        // Handle mouse input
        let message = MessageType::PlayerMouseDirectionFromClient {
            player_id: self.player_id.clone().unwrap_or_default().to_string(),
            mouse_direction: SerializableVector2::new_from_vector2(&direction),
        };

        if let Ok(json) = MessageType::to_json(&message) {
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize mouse input");
        }
    }
}
