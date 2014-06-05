package gameObject;

import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

public interface ICollisionable {

	/** Handle collision on impact.
	 * 
	 * @param start true on new impact, false on ending collision
	 * @param sender Sensor owned by <code>this</code> that raised the event,
	 *            maybe <code>null</code>
	 * @param other the other Object
	 * @param otherSensor the hit Sensor owned by <code>other</code>, maybe
	 *            <code>null</code>
	 * @return return whether the handling is done or should be passed to the
	 *         other */
	public boolean handleCollision(boolean start, Sensor mySender, GameObject other, Sensor otherSensor);

	/** Get x coordinate as float in pixel
	 * 
	 * @return x */
	public float getX();

	/** Get y coordinate as float in pixel
	 * 
	 * @return y */
	public float getY();

//	/** Check whether body is on ground.
//	 * 
//	 * @return grounded */
//	public boolean isGrounded();
//
//	/** Set grounded for body.
//	 * 
//	 * @param grounded */
//	public void setGrounded(boolean grounded);

	/**
	 * Get corresponding game world
	 * @return
	 */
	public GameWorld getGameWorld();

	/** Get Body Position in meter.
	 * 
	 * @return position */
	public Vector2 getWorldPosition();

	/** Get Body Position in pixel.
	 * 
	 * @return position */
	public Vector2 getPosition();

	/** Get Local Center in World in meter.
	 * 
	 * @return position */
	public Vector2 getLocalCenterInWorld();

}
