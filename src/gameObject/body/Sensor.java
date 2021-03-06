package gameObject.body;

import misc.BodyFunctions;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

public class Sensor implements Disposable, ISensorTypes {

	protected boolean active;
	protected BodyObject link;
	protected Shape.Type sensorShapeType;
	protected float[] sensorPoints;
	protected int sensorType;

	protected int priority;

	public static final int HANDLE_FIRST = 75, HANDLE_SECOND = 50, HANDLE_LAST = 25;

	/** Create Sensor linked to the BodyObject
	 * 
	 * @param parent BodyObject linked to
	 * @param shapeType Type of Shape of Sensor
	 * @param shapePoints Points to initianize Shape
	 * @param eventData Data passed to Handler on collision */
	protected Sensor(BodyObject parent, Shape.Type shapeType, float[] shapePoints, int sensorType) {
		this(parent, shapeType, shapePoints, sensorType, HANDLE_SECOND);
	}

	/** Create Sensor linked to the BodyObject
	 * 
	 * @param parent BodyObject linked to
	 * @param shapeType Type of Shape of Sensor
	 * @param shapePoints Points to initianize Shape
	 * @param eventData Data passed to Handler on collision
	 * @param priority Determine on Sensor-Sensor collision which Sensor is
	 *            activated */
	protected Sensor(BodyObject parent, Shape.Type shapeType, float[] shapePoints, int sensorType, int priority) {
		active = true;
		this.sensorType = sensorType;
		this.priority = priority;
		setShape(shapeType, shapePoints);
		setBodyObject(parent);
	}
	
	/** Set the BodyObject linked to
	 * 
	 * @param link the BodyObject */
	protected void setBodyObject(BodyObject link) {
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

	/** @return the BodyObject linked to */
	public BodyObject getBodyObject() {
		return link;
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
