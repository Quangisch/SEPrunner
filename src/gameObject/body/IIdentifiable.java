package gameObject.body;

public interface IIdentifiable {

	/**
	 * Get GameObjectType. Relevant for Collision Handling and Callbacks.
	 * @return gameObjectType
	 */
	BodyObjectType getBodyObjectType();

	/**
	 * Set GameObjectType. Relevant Relevant for Collision Handling and Callbacks.
	 * @param bodyObjectType
	 */
	void setBodyObjectType(BodyObjectType bodyObjectType);
	
}
