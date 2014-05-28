package gameObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
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

public class GameObject implements DrawableStatic, Collisionable {

	private String name;

	// BODY
	protected Body body;
	private float density;
	private float friction;
	private float restitution;
	private boolean sensor;

	private List<Shape> sensorShapes;

	private boolean flip = false;
	private boolean visible = true;
	private volatile boolean grounded = true;
	
	protected String[] states;
	protected Animation[] animations;
	protected PolygonShape[] boundingBoxes;

	protected int defaultState;
	protected int currentState;

	public GameObject(World world, Vector2 position) {
		sensorShapes = new LinkedList<Shape>();

		// init bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		body = world.createBody(bodyDef);
	}

	public void init(String name) {
		init(name, "res/sprites/" + name + ".json");
		// init(name, "res/sprites/" + name + ".json");
	}

	private void init(String name, String jsonPath) {
		JsonReader reader = new JsonReader();
		JsonValue root;
		try {
			root = reader.parse(new FileReader(jsonPath)).get(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		this.name = name;

		// ARRAY INIT
		states = new String[root.get("animationen").size];
		animations = new Animation[states.length];
		boundingBoxes = new PolygonShape[states.length];

		// STATES
		for (int i = 0; i < states.length; i++)
			states[i] = root.get("animationen").getString(i);

		for (int j = 0; j < states.length; j++) {
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
				textureRegions[i++] = new TextureRegion(
						new Texture(root.get("texture").asString()), frame.getInt(0),
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

		setFixture(density, friction, restitution, sensor, boundingBoxes[this.currentState], false);
		for (Shape s : sensorShapes)
			addFixture(0, 0, 0, true, s, false);
	}

	public int getCurrentState() {
		return currentState;
	}

	private float stateTime = 0;

	@Override
	public void draw(SpriteBatch batch) {
		if (!visible) 
			return;

		stateTime += Gdx.graphics.getDeltaTime();


		TextureRegion frame = new TextureRegion(animations[currentState].getKeyFrame(stateTime,
				true));
		frame.flip(flip, false);
		
		batch.draw(frame, getX(), getY());
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
	public void addFixture(float density, float friction, float restitution, boolean sensor,
			Shape shape, boolean disposeShape) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.isSensor = sensor;
		fixtureDef.shape = shape;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		body.createFixture(fixtureDef);
		if (disposeShape) shape.dispose();
	}

@Override
	public void setFixture(float density, float friction, float restitution, boolean sensor,
			Shape shape, boolean disposeShape) {
		for (Fixture f : body.getFixtureList())
			body.destroyFixture(f);
		addFixture(density, friction, restitution, sensor, shape, disposeShape);
	}

	public void initBody(BodyDef.BodyType type, float density, float friction, float restitution,
			boolean sensor, Shape shape, boolean disposeShape) {

		setFixture(density, friction, restitution, sensor, shape, disposeShape);
	}

	public void addSensorShape(Shape shape) {
		sensorShapes.add(shape);

		setCurrentState(currentState, true);
	}

	public void setObjectData(GameObjectData data) {
		body.setUserData(data);
	}

	public GameObjectData getObjectData() {
		return (GameObjectData) body.getUserData();
	}

	@Override
	public void applyForce(Vector2 force, boolean wake) {
		body.applyForceToCenter(force, wake);
	}

	public Body getBody() {
		return body;
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
	public void toogleVisible() {
		visible = !visible;
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

	@Override
	public void setGameObjectData(GameObjectData gameObjectData) {
		body.setUserData(gameObjectData);
		System.out.println(getGameObjectData().toString());
	}
	
	@Override
	public void setGameObjectData(int type, int subType) {
		setGameObjectData(new GameObjectData(type, subType, this));
	}
	
	@Override
	public GameObjectData getGameObjectData() {
		return (GameObjectData) body.getUserData();
	}
}
