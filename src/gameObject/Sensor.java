package gameObject;

import misc.BodyFunctions;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

public class Sensor implements Disposable {

	protected boolean active;
	protected GameObject link;
	protected Shape.Type sensorShapeType;
	protected float[] sensorPoints;
	protected int sensorType;

	protected int priority;

	public static final int HANDLE_FIRST = 75, HANDLE_SECOND = 50, HANDLE_LAST = 25;

	/** Create Sensor linked to the GameObject
	 * 
	 * @param parent GameObject linked to
	 * @param shapeType Type of Shape of Sensor
	 * @param shapePoints Points to initianize Shape
	 * @param eventData Data passed to Handler on collision */
	public Sensor(GameObject parent, Shape.Type shapeType, float[] shapePoints, int sensorType) {
		this(parent, shapeType, shapePoints, sensorType, HANDLE_SECOND);
	}

	/** Create Sensor linked to the GameObject
	 * 
	 * @param parent GameObject linked to
	 * @param shapeType Type of Shape of Sensor
	 * @param shapePoints Points to initianize Shape
	 * @param eventData Data passed to Handler on collision
	 * @param priority Determine on Sensor-Sensor collision which Sensor is
	 *            activated */
	public Sensor(GameObject parent, Shape.Type shapeType, float[] shapePoints, int sensorType, int priority) {
		active = true;
		this.sensorType = sensorType;
		this.priority = priority;
		setShape(shapeType, shapePoints);
		setGameObject(parent);
	}

	/** @return true if collision is handled */
	public boolean isActive() {
		return active;
	}

	/** Set if collision is handled
	 * 
	 * @param active true, if collision raises event */
	public void setActive(boolean active) {
		this.active = active;
	}

	/** @return the GameObject linked to */
	public GameObject getGameObject() {
		return link;
	}

	/** Set the GameObject linked to
	 * 
	 * @param link the GameObject */
	protected void setGameObject(GameObject link) {
		if (this.link == link) return;
		if (this.link != null) this.link.removeSensor(this);
		this.link = link;
		if (this.link != null) this.link.addSensor(this);
	}

	/** Set the shape of Sensor
	 * 
	 * @param sensorShapeType Shape of Sensor */
	protected void setShape(Shape.Type shapeType, float[] points) {
		this.sensorShapeType = shapeType;
		this.sensorPoints = points;
		
		// TODO Fixture aktualisieren
	}

	/** FixtureDef for internal use
	 * 
	 * @return FixtureDef */
	public FixtureDef getFixtureDef() {
		FixtureDef fixture = new FixtureDef();
		fixture.isSensor = true;
		fixture.shape = BodyFunctions.getShape(sensorShapeType, sensorPoints);
		return fixture;
	}

	/** custom event data
	 * 
	 * @return event data */
	public int getSensorType() {
		return sensorType;
	}

	/** Set custom event data
	 * 
	 * @param eventData the event data */
	public void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}

	/** @return the priority */
	public int getPriority() {
		return priority;
	}

	/** @param priority the priority to set */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public void dispose() {
		link = null;
		sensorPoints = null;
	}

}
