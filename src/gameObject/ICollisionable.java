package gameObject;

import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

public interface ICollisionable {

	/** Handle collision on impact.
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

	/** Get x coordinate as float in pixel
	 * 
	 * @return x */
	float getX();

	/** Get y coordinate as float in pixel
	 * 
	 * @return y */
	float getY();

//	/** Check whether body is on ground.
//	 * 
//	 * @return grounded */
//	boolean isGrounded();
//
//	/** Set grounded for body.
//	 * 
//	 * @param grounded */
//	void setGrounded(boolean grounded);

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
	 * 
	 * @return position */
	Vector2 getPosition();

	/** Get Local Center in World in meter.
	 * 
	 * @return position */
	Vector2 getLocalCenterInWorld();
	
	/**
	 * Apply Impulse at Center of BodyObject.
	 * @param impulse
	 */
	void applyImpulse(Vector2 impulse);

}
