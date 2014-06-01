package gameObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.GameProperties;

public class GameObject implements Drawable, Collisionable, IGameObjectTypes, ISensorTypes {

	private String name;
	private int gameObjectType = GameObjectTypes.UNSPECIFIED;

	// BODY
	protected Body body;
	private float density;
	private float friction;
	private float restitution;
	private boolean sensor;

	private List<Sensor> sensors;

	protected boolean flip = false;
	protected boolean visible = true;
	private volatile boolean grounded = true;
	protected int layer = 0;
	protected float alpha = 1;

	private String[] states;
	protected Animation[] animations;
	protected PolygonShape[] boundingBoxes;

	protected int defaultState;
	protected int currentState;

	protected enum gameObjectStates {
		// DEFAULT
	}

	public GameObject(World world, Vector2 position) {
		sensors = new LinkedList<Sensor>();

		// init bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		body = world.createBody(bodyDef);
		body.setUserData(this);
	}

	public void init(String name) {
		init(name, gameObjectStates.class);
	}

	@SuppressWarnings("rawtypes")
	public void init(String name, Class<? extends Enum> stateEnum) {
		init(name, "res/sprites/" + name + ".json", stateEnum);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void init(String name, String jsonPath, Class<? extends Enum> stateEnum) {
		JsonReader reader = new JsonReader();
		JsonValue root;
		try {
			root = reader.parse(new FileReader(jsonPath)).get(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		this.name = name;

		// GAMEOBJECT STATE ENUM and STATES
		try {
			EnumSet<?> enumSet = EnumSet.allOf(stateEnum);
			states = new String[enumSet.size()];
			for (Enum<?> i : enumSet)
				states[i.ordinal()] = i.toString().toUpperCase();

			if (root.hasChild("animationen")) {
				List<String> addAni = new LinkedList<String>();
				json: for (JsonValue ani : root.get("animationen")) {
					for (int i = 0; i < states.length; i++)
						if (states[i].equalsIgnoreCase(ani.asString())) break json;
					addAni.add(ani.asString());
				}
				String[] tmp = new String[states.length + addAni.size()];
				System.arraycopy(states, 0, tmp, 0, states.length);
				for (int i = 0; i < addAni.size(); i++)
					tmp[states.length + i] = addAni.get(i);
				states = tmp;
			}
		} catch (Exception e) {
			states = null;
			System.err.println(name + ": State initialisation error!");
		}

		// ARRAY INIT
		if (states == null) states = new String[root.get("animationen").size];
		animations = new Animation[states.length];
		boundingBoxes = new PolygonShape[states.length];

		// CORRECT CASE
		for (JsonValue s : root.get("stateframes"))
			s.name = s.name.toUpperCase();

		for (int j = 0; j < states.length; j++) {
			if (!root.get("stateframes").hasChild(states[j])) continue;

			JsonValue animationFrames = root.get("stateframes").get(states[j]);

			// BOUNDING BOX
			PolygonShape boundingBox = new PolygonShape();
			JsonValue bBox = animationFrames.get("boundingBox");
			int i = 0;
			float[] vertices = new float[bBox.size];
			for (JsonValue v : bBox)
				vertices[i++] = GameProperties.pixelToMeter(v.asFloat());
			boundingBox.set(vertices);
			boundingBoxes[j] = boundingBox;

			// TEXTURE FRAMES
			i = 0;
			TextureRegion[] textureRegions = new TextureRegion[animationFrames.get("textureMap").size];
			for (JsonValue frame : animationFrames.get("textureMap"))
				textureRegions[i++] = new TextureRegion(new Texture(root.get("texture").asString()), frame.getInt(0),
						frame.getInt(1), frame.getInt(2), frame.getInt(3));

			animations[j] = new Animation(animationFrames.getFloat("frameDuration"), textureRegions);
		}

		// STATUS
		defaultState = root.getInt("defaultState");

		// BODYDEF
		density = root.get("bodyDef").getFloat("density");
		friction = root.get("bodyDef").getFloat("friction");
		restitution = root.get("bodyDef").getFloat("restitution");
		sensor = root.get("bodyDef").getBoolean("sensor");

		switch (root.get("bodyDef").get("bodyType").asInt()) {
		case 0:
			body.setType(BodyType.StaticBody);
			break;
		case 1:
			body.setType(BodyType.KinematicBody);
			break;
		case 2:
			body.setType(BodyType.DynamicBody);
			break;
		}

		// SENSORS
		// TODO Add Sensors

		setCurrentState(defaultState, true);
	}

	public void setCurrentState(String state) {
		for (int i = 0; i < states.length; i++)
			if (states[i].equalsIgnoreCase(state)) {
				setCurrentState(i);
				return;
			}

		if (String.valueOf(Integer.parseInt(state)).equals(state)) {
			setCurrentState(Integer.parseInt(state));
			return;
		}

		System.err.println(name + ": Unknown state '" + state + "'");
		setCurrentState(defaultState);
	}

	public <T extends Enum<T>> void setCurrentState(T state) {
		setCurrentState(state.ordinal());
	}

	public void setCurrentState(int state) {
		setCurrentState(state, false);
	}

	public void setCurrentState(int state, boolean force) {
		if (this.currentState == state && !force) return;

		if (state % states.length != state) {
			System.err.println(name + ": Unknown state '" + state + "'");
			setCurrentState(defaultState);
			return;
		}

		this.currentState = state;

		// TODO Solve addFixture slowdown
		if (!force) return;

		setFixture(density, friction, restitution, sensor, boundingBoxes[this.currentState], false);
		for (Sensor s : sensors)
			addFiture(s.getFixtureDef()).setUserData(s);
	}

	public int getCurrentState() {
		return currentState;
	}

	private float stateTime = 0;

	@Override
	public void draw(SpriteBatch batch) {
		if (!visible) return;

		stateTime += Gdx.graphics.getDeltaTime();

		TextureRegion frame = new TextureRegion(animations[currentState].getKeyFrame(stateTime, true));
		frame.flip(flip, false);

		batch.setColor(1, 1, 1, getAlpha());
		// batch.setBlendFunction(GL11.GL_SRC0_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
		batch.draw(frame, getX(), getY());
		batch.setColor(Color.WHITE);
	}

	@Override
	public void setFlip(boolean flip) {
		if (this.flip == flip) return;
		flip();
	}

	@Override
	public void flip() {
		flip = !flip;
	}

	@Override
	public boolean isFlipped() {
		return flip;
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
		Fixture fix = addFiture(fixtureDef);
		if (disposeShape) shape.dispose();
		return fix;
	}

	public Fixture addFiture(FixtureDef fixtureDef) {
		return body.createFixture(fixtureDef);
	}

	@Override
	public Fixture setFixture(float density, float friction, float restitution, boolean sensor, Shape shape,
			boolean disposeShape) {
		for (Fixture f : body.getFixtureList())
			body.destroyFixture(f);
		return addFixture(density, friction, restitution, sensor, shape, disposeShape);
	}

	public void initBody(BodyDef.BodyType type, float density, float friction, float restitution, boolean sensor,
			Shape shape, boolean disposeShape) {

		body.setFixedRotation(true);
		setFixture(density, friction, restitution, sensor, shape, disposeShape);
	}

	public void addSensor(Sensor sensor) {
		if (sensors.contains(sensor)) return;

		sensors.add(sensor);
		sensor.setGameObject(this);

		setCurrentState(currentState, true);
	}

	public void removeSensor(Sensor sensor) {
		if (sensor.getGameObject() == this) sensor.setGameObject(null);
		if (sensors.remove(sensor)) setCurrentState(currentState, true);
	}

	@Override
	public void applyForce(Vector2 force, boolean wake) {
		body.applyForceToCenter(force, wake);
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
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
	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	@Override
	public boolean isGrounded() {
		return grounded;
	}

	public int getGameObjectType() {
		return gameObjectType;
	}

	public void setGameObjectType(int gameObjectType) {
		this.gameObjectType = gameObjectType;
	}

	/**
	 * @return the drawing layer
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * @param layer set the drawing layer
	 */
	protected void setLayer(int layer) {
		this.layer = layer;
	}

	/**
	 * @return the alpha
	 */
	protected float getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	protected void setAlpha(float alpha) {
		this.alpha = Math.abs(alpha) % 1;
		// this.alpha = (100 + alpha) % 1;
	}

	@Override
	public boolean handleCollision(Sensor sender, GameObject other, Sensor otherSensor) {
		return false;
	}
}
