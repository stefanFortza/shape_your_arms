use godot::classes::web_socket_peer::State;
use godot::classes::{IWebSocketPeer, Node, Timer, WebSocketPeer};
use godot::global::Error;
use godot::prelude::*;

#[derive(GodotClass)]
#[class(base=Node)]
pub struct NetworkClient {
    #[base]
    base: Base<Node>,

    #[export]
    websocket_url: GString,

    socket: Gd<WebSocketPeer>,
    player_id: i32,
}

#[godot_api]
impl INode for NetworkClient {
    fn init(base: Base<Node>) -> Self {
        Self {
            player_id: -1,
            websocket_url: GString::new(),
            socket: WebSocketPeer::new_gd(),
            base,
        }
    }

    fn ready(&mut self) {
        // Default URL if not set in the editor
        if self.websocket_url.is_empty() {
            self.websocket_url = "ws://localhost:8887".into();
        }

        // Initiate connection to the given URL
        let err: Error = self.socket.connect_to_url(&self.websocket_url);

        if err != Error::OK {
            godot_print!("Unable to connect");
            self.base_mut().set_process(false);
        } else {
            // // Wait for the socket to connect
            // let mut timer = Timer::new_alloc();
            // timer.set_wait_time(2.0);
            // timer.set_one_shot(true);
            // self.base_mut().add_child(&timer);
            // timer.start();
            // Note: In Rust with godot-rust, we can't use await directly like in GDScript
            // You might want to implement a state machine or use signals for connection handling
        }
    }

    fn process(&mut self, _delta: f64) {
        // Poll the socket for incoming data
        self.socket.poll();

        // Get the current state of the socket
        let state = self.socket.get_ready_state();

        match state {
            // WebSocketPeer.STATE_OPEN means the socket is connected and ready to send and receive data
            State::OPEN => {
                while self.socket.get_available_packet_count() > 0 {
                    let packet = self.socket.get_packet();
                    let message = packet.get_string_from_utf8();

                    self.signals().on_message_received().emit(message);
                }
            }
            // WebSocketPeer.STATE_CLOSING means the socket is closing
            State::CLOSING => {
                // Continue polling for clean close
            }
            // WebSocketPeer.STATE_CLOSED means the connection has fully closed
            State::CLOSED => {
                let code = self.socket.get_close_code();
                godot_print!(
                    "WebSocket closed with code: {}. Clean: {}",
                    code,
                    code != -1
                );
                self.base_mut().set_process(false); // Stop processing
            }
            _ => {}
        }
    }
}

#[godot_api]
impl NetworkClient {
    #[signal]
    fn on_message_received(message: GString);
}
