package gameObject;

import gameObject.interaction.IInteractionStates.InteractionState;
import gameWorld.GameWorld;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

import core.ingame.GameProperties;
/**
 * Container class for Box2d Body Object with Sensor and Shape Lists. 
 * @author SEP SS14 GruppeA
 *
 */
public class BodyObject implements  ICollisionable, IGameObjectTypes, ISensorTypes,
		Disposable, Runnable {

	private int gameObjectType = GameObjectTypes.UNSPECIFIED;
	private GameWorld gameWorld;

	// BODY
	private Body body;
	private Fixture primaryFixture;

	private List<Sensor> sensors;
	private PolygonShape[] boundingBoxes;

	public BodyObject(GameWorld gameWorld, Vector2 position) {		
		this.gameWorld = gameWorld;
		sensors = new LinkedList<Sensor>();
		boundingBoxes = new PolygonShape[InteractionState.values().length];

		// init bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		body = gameWorld.getWorld().createBody(bodyDef);
		body.setUserData(this);
		body.setFixedRotation(true);
	}

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

	public Fixture addFixture(FixtureDef fixtureDef) {
		return body.createFixture(fixtureDef);
	}

	public Fixture setFixture(float density, float friction, float restitution, boolean sensor, Shape shape,
			boolean disposeShape) {
		for (Fixture f : body.getFixtureList())
			body.destroyFixture(f);
		return addFixture(density, friction, restitution, sensor, shape, disposeShape);
	}
	
	protected void setFixture(InteractionState state) {
		// TODO Clean
		Vector2 v[] = new Vector2[boundingBoxes[state.getAnimationIndex()].getVertexCount()];
		for (int i = 0; i < v.length; i++) {
			v[i] = new Vector2();
			boundingBoxes[state.getAnimationIndex()].getVertex(i, v[i]);
		}
		((PolygonShape) primaryFixture.getShape()).set(v);
	}
	
	protected void resetToPrimaryFixture(BodyType bodyType, float linearDamping, float density, float friction, float restitution, boolean sensor, Shape shape,
			boolean disposeShape) {
		body.setType(bodyType);
		body.setLinearDamping(linearDamping);
		primaryFixture = setFixture(density, friction, restitution, sensor, shape, disposeShape);
	}

	protected void initBody(BodyDef.BodyType type, float density, float friction, float restitution, boolean sensor,
			Shape shape, boolean disposeShape) {

		body.setFixedRotation(true);
		setFixture(density, friction, restitution, sensor, shape, disposeShape);
	}
	
	protected void setGravityScale(float scale) {
		body.setGravityScale(scale);
	}
	
	protected void rayCast(RayCastCallback callback, Vector2 point1, Vector2 point2) {
		body.getWorld().rayCast(callback, point1, point2);
	}
	
	protected void addSensor(Sensor sensor) {
		if (sensors.contains(sensor)) return;

		sensors.add(sensor);
		sensor.setGameObject(this);

		addFixture(sensor.getFixtureDef()).setUserData(sensor);
	}

	protected void removeSensor(Sensor sensor) {
		if (sensor.getGameObject() == this) sensor.setGameObject(null);
		if (sensors.remove(sensor)) /* TODO Remove Sensor Fixture -> dispose?*/;
	}

	protected void setBoundingBox(int index, PolygonShape shape) {
		boundingBoxes[index] = shape;
	}
	
	protected PolygonShape getBoundingBox(int index) {
		return boundingBoxes[index];
	}
	
	protected boolean addToGameWorld(GameObject gameObject) {
		return gameWorld.addGameObject(gameObject);
	}

	@Override
	public float getX() {
		return GameProperties.meterToPixel(body.getPosition().x);
	}

	@Override
	public float getY() {
		return GameProperties.meterToPixel(body.getPosition().y);
	}

	public int getGameObjectType() {
		return gameObjectType;
	}

	public void setGameObjectType(int gameObjectType) {
		this.gameObjectType = gameObjectType;
	}

	@Override
	public GameWorld getGameWorld() {
		return gameWorld;
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

		for (Shape b : boundingBoxes)
			if (b != null) b.dispose();
		boundingBoxes = null;	
	}
	
	@Override
	public void run() {

	}
	
	@Override
	public boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		return false;
	}
	
}
