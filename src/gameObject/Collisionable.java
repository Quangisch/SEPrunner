package gameObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public interface Collisionable {
	
	/**
	 * 
	 * @param world Kollisionsebene
	 * @param type Beweglichkeit
	 * @param position Positionsvektor
	 * @param density Dichte
	 * @param friction Reibungskoeffizient
	 * @param restitution Elastizitätskoeffizient
	 * @param sensor Durchlässigkeit
	 * @param shape geometrische Form
	 */
	public void initBody(BodyDef.BodyType type, float density, float friction, float restitution,
			boolean sensor, Shape shape, boolean disposeShape);
	
	
	public Fixture addFixture(float density, float friction, float restitution, boolean sensor, Shape shape, boolean disposeShape);
	public Fixture setFixture(float density, float friction, float restitution, boolean sensor, Shape shape, boolean disposeShape);
	
	public void applyForce(Vector2 force, boolean wake);
	
	/**
	 * Handle an incoming collision
	 * @param sender Sensor owned by <code>this</code> that raised the event, maybe <code>null</code>
	 * @param other the other Object
	 * @param otherSensor the hit Sensor owned by <code>other</code>, maybe <code>null</code> 
	 * @return return whether the handling is done or should be passed to the other
	 */
	public boolean handleCollision(Sensor mySender, GameObject other, Sensor otherSensor);
	
	/**
	 * Get x coordinate as float in pixel
	 * @return x
	 */
	public float getX();
	
	/**
	 * Get y coordinate as float in pixel
	 * @return y
	 */
	public float getY();
	
	/**
	 * Check whether body is on ground.
	 * @return grounded
	 */
	public boolean isGrounded();
	
	/**
	 * Set grounded for body.
	 * @param grounded
	 */
	public void setGrounded(boolean grounded);
	
	/**
	 * Get corresponding world.
	 * @return world
	 */
	public World getWorld();
	
	/**
	 * Get Body Position in meter.
	 * @return position
	 */
	public Vector2 getWorldPosition();
	
	/**
	 * Get Body Position in pixel.
	 * @return position
	 */
	public Vector2 getPosition();

}
