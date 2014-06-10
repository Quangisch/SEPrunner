package gameObject.body;

public interface IIdentifiable {

	/**
	 * Get GameObjectType. Relevant for Collision Handling and Callbacks.
	 * @return gameObjectType
	 */
	GameObjectType getGameObjectType();

	/**
	 * Set GameObjectType. Relevant Relevant for Collision Handling and Callbacks.
	 * @param gameObjectType
	 */
	void setGameObjectType(GameObjectType gameObjectType);
	
}
