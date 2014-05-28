package gameObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

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
			boolean sensor, Shape shape);
	
	
	public void addFixture(float density, float friction, float restitution, boolean sensor, Shape shape);
	public void setFixture(float density, float friction, float restitution, boolean sensor, Shape shape);
	
	public void applyForce(Vector2 force, boolean wake);
	
	/**
	 * Set UserData for corresponding body-Object with type and subtype.
	 * @param type
	 * @param subType
	 */
	public void setGameObjectData(int type, int subType);
	public void setGameObjectData(GameObjectData gameObjectData);
	public GameObjectData getGameObjectData();
	
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
	
}
