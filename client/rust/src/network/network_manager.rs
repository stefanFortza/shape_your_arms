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

    #[base]
    base: Base<Node>,
}

#[godot_api]
impl INode for NetwornkManager {
    fn init(base: Base<Node>) -> Self {
        Self {
            player_id: None,
            base,
        }
    }

    fn ready(&mut self) {
        godot_print!("NetworkManager is ready");

        let input_controller = self.get_input_controller();
        if let Some(mut input_controller) = input_controller {
            godot_print!("InputController found");

            input_controller
                .signals()
                .input_direction_changed()
                .connect_obj(&self.to_gd(), Self::on_input_direction_changed);
        } else {
            godot_print!("Player node not found");
        }
    }
}

#[godot_api]
pub impl NetwornkManager {
    #[signal]
    pub fn message_serialized(message: GString);

    #[signal]
    pub fn welcome_message_received(player_id: GString);

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
                MessageType::Welcome { player_id } => {
                    godot_print!("Welcome message received with player ID: {}", player_id);
                    let player_id = player_id.to_godot();
                    self.player_id = Some(player_id.clone());
                    self.signals().welcome_message_received().emit(player_id);
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
                    godot_print!("Other message type received");
                }
            }
        } else {
            godot_error!("Failed to deserialize message");
        }
        // Handle incoming messages
        // godot_print!("Message received: {}", message);
    }

    #[func]
    fn on_input_direction_changed(&mut self, direction: Vector2) {
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
    fn get_input_controller(&self) -> Option<Gd<InputController>> {
        let input_controller = self
            .base()
            .try_get_node_as::<InputController>("../../InputController");

        return input_controller;
    }
}
