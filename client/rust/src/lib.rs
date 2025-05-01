use godot::prelude::*;

mod entities;
mod main_game;
mod main_menu;
mod network;
mod utils;

struct MyExtension;

#[gdextension]
unsafe impl ExtensionLibrary for MyExtension {}
