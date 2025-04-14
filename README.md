# Godot-Rust Multiplayer Game

A multiplayer game project that combines Godot 4 with Rust for the client and Java Spring Boot for the server.

## Project Structure

The project is divided into two main components:

- **Client**: A Godot 4 game using the [godot-rust](https://github.com/godot-rust/gdext) bindings
- **Server**: A Java Spring Boot application that handles multiplayer functionality

### Client

The client is built with Godot 4 and uses Rust for game logic via GDExtension. The client features:

- Rust integration with Godot using gdext
- WebSocket-based networking to communicate with the server
- Structured message system for multiplayer communication
- Main menu and game UI scenes

#### Directory Structure

- `client/godot/` - Godot project files
- `client/rust/` - Rust code for game logic
- `client/workflow.txt` - Overview of the network messaging flow

### Server

The server is a Java Spring Boot application that manages the multiplayer game state. It features:

- WebSocket server for real-time communication
- Game loop for physics and state updates
- Player and game entity management
- JSON-based message passing

#### Directory Structure

- `server/src/main/java/multiplayer/` - Core multiplayer functionality
- `server/src/main/java/stefantacu/shape_your_arms/` - Spring Boot application

## Getting Started

### Prerequisites

- [Godot 4.x](https://godotengine.org/)
- [Rust](https://www.rust-lang.org/) (with cargo)
- [Java 21 JDK](https://openjdk.org/)
- [Maven](https://maven.apache.org/)

### Building the Client

1. Navigate to the rust directory:
   ```
   cd client/rust
   ```

2. Build the Rust library:
   ```
   cargo build
   ```

3. Open the Godot project:
   ```
   cd ../godot
   godot project.godot
   ```

### Running the Server

1. Navigate to the server directory:
   ```
   cd server
   ```

2. Build and run with Maven:
   ```
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```
   mvnw.cmd spring-boot:run
   ```

## Communication Flow

As described in `workflow.txt`:

```
Server → WebSocket → NetworkClient → Rust NetworkManager → 
JSON Parsing & Deserialization → Typed Data → Godot Signals → Game Objects

Game Objects → Godot Signals → Typed Data → Rust NetworkManager → 
JSON Serialization → NetworkClient → WebSocket → Server
```

## License

[License information here]