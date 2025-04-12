extends Node


enum MessageType {
	GAME_STATE,
	PLAYER_MOVE,
	HIT,
	PLAYER_DEATH,
	WELCOME,
	PLAYER_LEFT
}

# Base class for all network messages
class NetworkMessage:
	var type: MessageType
	
	func _init(message_type: MessageType):
		type = message_type
	

# Game state message containing players and bullets
class GameStateMessage extends NetworkMessage:
	var players: Dictionary
	var bullets: Array
	
	func _init(data: Dictionary):
		super._init(MessageType.GAME_STATE)
		players = data.get("players", {})
		bullets = data.get("bullets", [])

class MoveMessage extends NetworkMessage:
	var player_id: String
	var position: Vector2
	
	func _init(id: String, pos: Vector2):
		super._init(MessageType.PLAYER_MOVE)
		player_id = id
		position = pos

# Hit message with damage information
# class HitMessage extends NetworkMessage:
# 	var damage: int
	
# 	func _init(damage_amount: int):
# 		super._init("Hit")
# 		damage = damage_amount

# # Player death message
# class PlayerDeathMessage extends NetworkMessage:
# 	var player_id: String
# 	var reason: String
	
# 	func _init(id: String, death_reason: String):
# 		super._init("PlayerDeath")
# 		player_id = id
# 		reason = death_reason

# # Welcome message with player info
# class WelcomeMessage extends NetworkMessage:
# 	var id: String
# 	var x: float
# 	var y: float
	
# 	func _init(player_id: String, pos_x: float, pos_y: float):
# 		super._init("Welcome")
# 		id = player_id
# 		x = pos_x
# 		y = pos_y

# # Player left message
# class PlayerLeftMessage extends NetworkMessage:
# 	var player_id: String
	
# 	func _init(id: String):
# 		super._init("PlayerLeft")
# 		player_id = id

# Define strongly typed signals
signal on_game_state_received(message: GameStateMessage)
# signal on_hit_received(message: HitMessage)
# signal on_player_death_received(message: PlayerDeathMessage)
# signal on_welcome_received(message: WelcomeMessage)
# signal on_player_left_received(message: PlayerLeftMessage)

# Called when the node enters the scene tree for the first time.


func _on_network_client_on_message_received(message_str: String) -> void:
	print("Message received: ", message_str)
	
	# Parse the JSON message
	var json = JSON.new()
	var error = json.parse(message_str)
	if error != OK:
		print("JSON Parse Error: ", json.get_error_message())
		return
	
	var data = json.get_data()
	
	# Check message type and emit the appropriate signal with strongly typed message
	if "type" not in data:
		print("No 'type' field in message")
		return
		
	match data["type"]:
		"GameState":
			var game_state = GameStateMessage.new(data)
			on_game_state_received.emit(game_state)
		
		# "Hit":
		# 	if "damage" in data:
		# 		var hit_msg = HitMessage.new(data["damage"])
		# 		on_hit_received.emit(hit_msg)
		# 	else:
		# 		print("Hit message missing 'damage' field")
		
		# "PlayerDeath":
		# 	if "player_id" in data and "reason" in data:
		# 		var death_msg = PlayerDeathMessage.new(data["player_id"], data["reason"])
		# 		on_player_death_received.emit(death_msg)
		# 	else:
		# 		print("PlayerDeath message missing required fields")
		
		# "Welcome":
		# 	if "id" in data and "x" in data and "y" in data:
		# 		var welcome_msg = WelcomeMessage.new(data["id"], data["x"], data["y"])
		# 		on_welcome_received.emit(welcome_msg)
		# 	else:
		# 		print("Welcome message missing required fields")
		
		# "PlayerLeft":
		# 	if "player_id" in data:
		# 		var left_msg = PlayerLeftMessage.new(data["player_id"])
		# 		on_player_left_received.emit(left_msg)
		# 	else:
		# 		print("PlayerLeft message missing 'player_id' field")
		
		_:
			print("Unknown message type: ", data["type"])
