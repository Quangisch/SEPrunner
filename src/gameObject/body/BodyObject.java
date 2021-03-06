package gameObject.body;

import gameObject.GameObject;
import gameObject.interaction.InteractionState;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Disposable;

import core.GameProperties;
/**
 * Container class for Box2d Body Object with Sensor and Shape Lists. 
 * @author SEP SS14 GruppeA
 *
 */
public class BodyObject implements IBodyInitializer, 
		Disposable, Runnable, IIdentifiable {

//	private GameWorld gameWorld;

	// BODY
	private Body body;
	private Fixture primaryFixture;

	private List<Sensor> sensors;
	private Map<InteractionState, PolygonShape> boundingBoxMap;
	
	private GameObject parent, jointObject;
	private BodyObjectType bodyObjectType = BodyObjectType.Unspecified;
	private Joint joint;

	public BodyObject(World world, Vector2 position, GameObject parent) {		
		this(world, position);
		this.parent = parent;
	}
	
	public BodyObject(World world, Vector2 position) {
		sensors = new LinkedList<Sensor>();
		boundingBoxMap = new HashMap<InteractionState, PolygonShape>();

		// init bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		body = world.createBody(bodyDef);
		body.setFixedRotation(true);
		
		body.setUserData(this);
	}
	
	@Override
	public Fixture setFixture(float density, float friction, float restitution, boolean sensor, Shape shape,
			boolean disposeShape) {
		for (Fixture f : body.getFixtureList())
			body.destroyFixture(f);
		return addFixture(density, friction, restitution, sensor, shape, disposeShape);
	}

	@Override
	public Fixture addFixture(float density, float friction, float restitution, boolean sensor, Shape shape,
			boolean disposeShape) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.isSensor = sensor;
		fixtureDef.shape = shape;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		Fixture fix = addFixture(fixtureDef);
		if (disposeShape) shape.dispose();
		return fix;
	}
	
	@Override
	public Fixture addFixture(FixtureDef fixtureDef) {
		return body.createFixture(fixtureDef);
	}
	
	@Override
	public void setFixture(InteractionState state) {
		PolygonShape shape = boundingBoxMap.get(state);
		Vector2 v[] = new Vector2[shape.getVertexCount()];
		for (int i = 0; i < v.length; i++) {
			v[i] = new Vector2();
			shape.getVertex(i, v[i]);
		}
		((PolygonShape) primaryFixture.getShape()).set(v);
	}
	
	@Override
	public void resetToPrimaryFixture(BodyType bodyType, float linearDamping, float density, float friction, float restitution, boolean sensor, Shape shape,
			boolean disposeShape) {
		body.setType(bodyType);
		body.setLinearDamping(linearDamping);
		primaryFixture = setFixture(density, friction, restitution, sensor, shape, disposeShape);
	}

	@Override
	public void initBody(BodyDef.BodyType type, float density, float friction, float restitution, boolean sensor,
			Shape shape, boolean disposeShape) {

		body.setFixedRotation(true);
		setFixture(density, friction, restitution, sensor, shape, disposeShape);
	}
	
	@Override
	public GameObject getParent() {
		return parent;
	}
	
	@Override
	public void setGravityScale(float scale) {
		body.setGravityScale(scale);
	}

	@Override
	public void addSensor(Shape.Type shapeType, float[] shapePoints, int sensorType, int priority) {
		addSensor(new Sensor(this, shapeType, shapePoints, sensorType, priority));
	}
	
	public void addSensor(Sensor sensor) {
		if (sensors.contains(sensor)) return;

		sensors.add(sensor);
		sensor.setBodyObject(this);
		
		addFixture(sensor.getFixtureDef()).setUserData(sensor);
	}

	@Override
	public boolean removeSensor(Sensor sensor) {
		if (sensor.getBodyObject() == this) 
			sensor.setBodyObject(null);
		
//		TODO Remove Sensor Fixture
		if (sensors.remove(sensor)) 
			;
		return false;
	}

	@Override
	public void addBoundingBox(InteractionState state, PolygonShape shape) {
		if(shape != null)
			boundingBoxMap.put(state, shape);
		else
			System.err.println(this.getClass()+"@addBoundingBox(...) : shape == null");
	}
	
	@Override
	public PolygonShape getBoundingBox(InteractionState state) {
		return boundingBoxMap.get(state);
	}
	
	@Override
	public float getX() {
		return GameProperties.meterToPixel(body.getPosition().x);
	}

	@Override
	public float getY() {
		return GameProperties.meterToPixel(body.getPosition().y);
	}
	
	@Override
	public void applyImpulse(Vector2 impulse) {
		body.applyLinearImpulse(impulse, getLocalCenterInWorld(), true);
	}

	@Override
	public Vector2 getWorldPosition() {
		return body.getPosition();
	}

	@Override
	public Vector2 getPosition() {
		return new Vector2(getX(), getY());
	}

	@Override
	public Vector2 getLocalCenterInWorld() {
		return body.getWorldPoint(body.getLocalCenter());
	}

	// DISPOSABLE
	private boolean disposeNextStep = false;
	private boolean disposed = false;

	public void dispose() {
		disposeNextStep = true;
	}

	public boolean willDisposed() {
		return disposeNextStep;
	}

	public void disposeUnsafe() {
		if (disposed) return;
		disposed = true;

		body.getWorld().destroyBody(body);

		for (Sensor s : sensors)
			if (s != null) s.dispose();
		sensors.clear();

		try{
			for (InteractionState iS : boundingBoxMap.keySet())
				boundingBoxMap.get(iS).dispose();
		} catch (NullPointerException e) {
//			e.printStackTrace(); TODO
		} finally {
			boundingBoxMap.clear();
		}
	}
	
	@Override
	public void run() {

	}
	
	@Override
	public BodyObjectType getBodyObjectType() {
		return bodyObjectType;
	}

	@Override
	public void setBodyObjectType(BodyObjectType bodyObjectType) {
		this.bodyObjectType = bodyObjectType;
	}

	@Override
	public void joinBodies(final BodyObject bodyObject) {
		if(joint != null)
			uncoupleBodies();
			
		DistanceJointDef jointDef = new DistanceJointDef();
		jointDef.bodyA = body;
		jointDef.bodyB = bodyObject.body;
		jointDef.length = 0.5f;
		joint = parent.getGameWorld().getWorld().createJoint(jointDef);
		jointObject = bodyObject.getParent();
	}
	
	public GameObject getJointGameObject() {
		return jointObject;
	}
	
	public int getRotation() {
		return (int)Math.toDegrees(body.getAngle());
	}
	
	public void setPositionInMeter(Vector2 position) {
		body.setTransform(position.x, position.y, body.getAngle());
	}
	
	public void moveYInMeter(float dy) {
		body.setTransform(body.getPosition().x, body.getPosition().y + dy, body.getAngle());
	}
	
	public void setRotation(float rotation) {
		body.setTransform(body.getPosition().x, body.getPosition().y, (float)Math.toRadians(rotation));
	}
	
	public void resetVelocity() {
		body.setAngularVelocity(0);
		body.setLinearVelocity(new Vector2());
	}
	
	@Override
	public GameObject uncoupleBodies() {
		if(joint != null) {
			final GameObject g = jointObject;
			parent.getGameWorld().getWorld().destroyJoint(joint);
			joint = null;
			jointObject = null;
			return g;
		}
		return null;
	}
	
	public void setBullet() {
		body.setBullet(true);
	}
	
	@Override
	public List<Sensor> getSensors() {
		return sensors;
	}
}
