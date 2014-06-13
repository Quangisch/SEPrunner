package gameObject.body;

import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public interface IBodyInitializer {

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
	
	void setFixture(InteractionState state);
	
	void addBoundingBox(InteractionState state, PolygonShape shape);
	
	void resetToPrimaryFixture(BodyType bodyType, float linearDamping, float density, 
			float friction, float restitution, boolean sensor, Shape shape,
			boolean disposeShape);


	void initBody(BodyDef.BodyType type, float density, float friction, float restitution, boolean sensor,
			Shape shape, boolean disposeShape);
	
	PolygonShape getBoundingBox(InteractionState state);
	
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
	 * Get GameObject parent.
	 * @return parent
	 */
	GameObject getParent();
	
	/**
	 * Create new Sensor and add it to body.
	 * @param shapeType
	 * @param shapePoints
	 * @param sensorType
	 * @param priority
	 */
	void addSensor(Shape.Type shapeType, float[] shapePoints, int sensorType, int priority);
	
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
	 * Set Gravity Scale in relation to the world gravity.
	 * @param scale
	 */
	void setGravityScale(float scale);
	
	/**
	 * Creates a DistanceJoint between both bodies.
	 * @param bodyObject
	 */
	
	void joinBodies(BodyObject bodyObject);
	
	/**
	 * Uncouples and destroys Joint.
	 * @return uncoupled GameObject
	 */
	GameObject uncoupleBodies();
	
	List<Sensor> getSensors();

}
