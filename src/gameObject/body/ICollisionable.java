package gameObject.body;

import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public interface ICollisionable {

	/**
	 * Destroys all existing fixtures in body and set new one. 
	 * 
	 * @param density
	 * @param friction
	 * @param restitution
	 * @param sensor
	 * @param shape
	 * @param disposeShape
	 * @return
	 */
	Fixture setFixture(float density, float friction, float restitution, 
			boolean sensor, Shape shape, boolean disposeShape);
	
	/**
	 * Add Fixture to body.
	 * 
	 * @param density
	 * @param friction
	 * @param restitution
	 * @param sensor
	 * @param shape
	 * @param disposeShape
	 * @return
	 */
	Fixture addFixture(float density, float friction, float restitution, 
			boolean sensor, Shape shape, boolean disposeShape);
	
	/**
	 * Add Fixture to body.
	 * 
	 * @param density
	 * @param friction
	 * @param restitution
	 * @param sensor
	 * @param shape
	 * @param disposeShape
	 * @return
	 */
	Fixture addFixture(FixtureDef fixtureDef);
	
	/** 
	 * Handle collision on impact.
	 * 
	 * @param start true on new impact, false on ending collision
	 * @param mySensor Sensor owned by <code>this</code> that raised the event,
	 *            maybe <code>null</code>
	 * @param other the other Object
	 * @param otherSensor the hit Sensor owned by <code>other</code>, maybe
	 *            <code>null</code>
	 * @return return whether the handling is done or should be passed to the
	 *         other */
	boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor);

	/**
	 * Add Sensor to body.
	 * @param sensor to be added
	 */
	void addSensor(Sensor sensor);
	
	/**
	 * Remove Sensor from body.
	 * @param sensor to be removed
	 * @return whether sensor was removed
	 */
	boolean removeSensor(Sensor sensor);
	
	/** 
	 * Get x coordinate as float in pixel
	 * @return x 
	 */
	float getX();

	/** 
	 * Get y coordinate as float in pixel 
	 * @return y 
	 */
	float getY();
	
	/**
	 * Get corresponding game world
	 * @return
	 */
	GameWorld getGameWorld();

	/** Get Body Position in meter.
	 * 
	 * @return position */
	Vector2 getWorldPosition();

	/** Get Body Position in pixel.
	 * @return position */
	Vector2 getPosition();

	/** 
	 * Get Local Center in World in meter.
	 * @return position */
	Vector2 getLocalCenterInWorld();
	
	/**
	 * Apply Impulse at Center of BodyObject.
	 * @param impulse
	 */
	void applyImpulse(Vector2 impulse);
	
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
	
	/**
	 * Set Gravity Scale in relation to the world gravity.
	 * @param scale
	 */
	void setGravityScale(float scale);

}
