package gameObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Shape.Type;

public class Sensor {

	protected boolean active;
	protected GameObject link;
	protected Type sensorShapeType;
	protected float[] sensorPoints;
	protected Object sensorData;

	protected int priority;

	public static final int HANDLE_FIRST = 75, HANDLE_SECOND = 50, HANDLE_LAST = 25;

	/**
	 * Create Sensor linked to the GameObject
	 * 
	 * @param parent GameObject linked to
	 * @param shapeType Type of Shape of Sensor
	 * @param shapePoints Points to initianize Shape
	 * @param eventData Data passed to Handler on collision
	 */
	public Sensor(GameObject parent, Shape.Type shapeType, float[] shapePoints, Object eventData) {
		this(parent, shapeType, shapePoints, eventData, HANDLE_SECOND);
	}

	/**
	 * Create Sensor linked to the GameObject
	 * 
	 * @param parent GameObject linked to
	 * @param shapeType Type of Shape of Sensor
	 * @param shapePoints Points to initianize Shape
	 * @param eventData Data passed to Handler on collision
	 * @param priority Determine on Sensor-Sensor collision which Sensor is activated
	 */
	public Sensor(GameObject parent, Shape.Type shapeType, float[] shapePoints, Object eventData, int priority) {
		active = true;
		sensorData = eventData;
		this.priority = priority;
		setShape(shapeType, shapePoints);
		setGameObject(parent);
	}

	/**
	 * @return true if collision is handled
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Set if collision is handled
	 * 
	 * @param active true, if collision raises event
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the GameObject linked to
	 */
	public GameObject getGameObject() {
		return link;
	}

	/**
	 * Set the GameObject linked to
	 * 
	 * @param link the GameObject
	 */
	protected void setGameObject(GameObject link) {
		if (this.link == link) return;
		if (this.link != null) this.link.removeSensor(this);
		this.link = link;
		if (this.link != null) this.link.addSensor(this);
	}

	/**
	 * Set the shape of Sensor
	 * 
	 * @param sensorShapeType Shape of Sensor
	 */
	protected void setShape(Shape.Type shapeType, float[] points) {
		this.sensorShapeType = shapeType;
		this.sensorPoints = points;

		if (getGameObject() != null) //
			getGameObject().setCurrentState(getGameObject().getCurrentState(), true);
	}

	/**
	 * FixtureDef for internal use
	 * 
	 * @return FixtureDef
	 */
	public FixtureDef getFixtureDef() {
		Shape p = null;
		switch (sensorShapeType) {
		case Chain:
			p = new ChainShape();
			((ChainShape) p).createChain(sensorPoints);
			break;
		case Circle:
			p = new CircleShape();
			((CircleShape) p).setPosition(new Vector2(sensorPoints[0], sensorPoints[1]));
			((CircleShape) p).setRadius(sensorPoints[2]);
			break;
		case Edge:
			p = new EdgeShape();
			((EdgeShape) p).set(sensorPoints[0], sensorPoints[1], sensorPoints[2], sensorPoints[3]);
			break;
		case Polygon:
			p = new PolygonShape();
			((PolygonShape) p).set(sensorPoints);
			break;
		default:
			throw new NullPointerException();
		}

		FixtureDef fixture = new FixtureDef();
		fixture.isSensor = true;
		fixture.shape = p;
		return fixture;
	}

	/**
	 * custom event data
	 * 
	 * @return event data
	 */
	public Object getEventData() {
		return sensorData;
	}

	/**
	 * Set custom event data
	 * 
	 * @param eventData the event data
	 */
	public void setEventData(Object eventData) {
		this.sensorData = eventData;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

}
