class_name MessageTypes extends Node

enum MessageType {
	PLAYER_JOIN,
	PLAYER_MOVE,
	PLAYER_QUIT,
	CHAT_MESSAGE,
	GAME_STATE,
	# Add more message types as needed
}

# Base class for all messages
class Message:
	var type: int

	func _init(message_type: int):
		type = message_type

	func to_dict() -> Dictionary:
		return {"type": get_type_as_string()}

	func get_type_as_string():
		if type == MessageType.PLAYER_MOVE:
			return "PlayerMoveFromClient"
		else:
			pass


# Player join message
class PlayerJoinMessage extends Message:
	var player_name: String
	
	func _init(name: String):
		super._init(MessageType.PLAYER_JOIN)
		player_name = name
	
	func to_dict() -> Dictionary:
		var dict = super.to_dict()
		dict["player_name"] = player_name
		return dict

# Player movement message
class PlayerMoveMessage extends Message:
	var direction: Vector2
	# var velocity: Vector2
	
	func _init(dir: Vector2):
		super._init(MessageType.PLAYER_MOVE)
		direction = dir
		# velocity = vel
	
	func to_dict() -> Dictionary:
		var dict = super.to_dict()
		# dict["direction"] = {"x": direction.x, "y": direction.y}
		dict["direction"] = direction
		# dict["velocity"] = {"x": velocity.x, "y": velocity.y}
		return dict

# Add more message classes as needed
