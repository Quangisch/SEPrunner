package gameObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import core.ingame.GameProperties.Debug;

public class GameObject implements Drawable, Collisionable, IGameObjectTypes, ISensorTypes, IInteractionStates {

	protected String name;
	private int gameObjectType = GameObjectTypes.UNSPECIFIED;
	protected float rotation = 0;

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

	protected Animation[] animations;
	protected PolygonShape[] boundingBoxes;
	protected int aniDraw;

	private InteractionState defaultState;
	private InteractionState currentState;

	public GameObject(World world, Vector2 position) {
		sensors = new LinkedList<Sensor>();
		aniDraw = 0;

		// init bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		body = world.createBody(bodyDef);
		body.setUserData(this);
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
		int aniPointer = 0;
		animations = new Animation[InteractionState.values().length];
		boundingBoxes = new PolygonShape[animations.length];

		for (JsonValue js : root.get("stateframes"))
			js.name = js.name.toUpperCase();

		Map<String, Integer> found = new HashMap<String, Integer>();

		for (InteractionState iS : InteractionState.values()) {
			if (found.containsKey(iS.getAnimation())) {
				iS.setAnimationIndex(found.get(iS.getAnimation()));
				continue;
			}

			JsonValue animationFrames = root.get("stateframes").get(iS.getAnimation().toUpperCase());
			if (animationFrames == null) {
				System.err.println(iS.getAnimation() + " not found");
				continue;
			}

			// BOUNDING BOX
			PolygonShape boundingBox = new PolygonShape();
			JsonValue bBox = animationFrames.get("boundingBox");
			int i = 0;
			float[] vertices = new float[bBox.size];
			for (JsonValue v : bBox)
				vertices[i++] = GameProperties.pixelToMeter(v.asFloat());
			boundingBox.set(vertices);
			boundingBoxes[aniPointer] = boundingBox;

			// TEXTURE FRAMES
			i = 0;
			TextureRegion[] textureRegions = new TextureRegion[animationFrames.get("textureMap").size];
			for (JsonValue frame : animationFrames.get("textureMap"))
				textureRegions[i++] = new TextureRegion(new Texture(root.get("texture").asString()),
						frame.getInt(0), frame.getInt(1), frame.getInt(2), frame.getInt(3));

			animations[aniPointer] = new Animation(animationFrames.getFloat("frameDuration"), textureRegions);

			found.put(iS.getAnimation(), aniPointer);
			iS.setAnimationIndex(aniPointer++);
		}

		// STATUS
		defaultState = InteractionState.values()[root.getInt("defaultState")];

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

		setInteractionState(defaultState, true);
	}

	public void setInteractionState(InteractionState state) {
		setInteractionState(state, false);
	}

	public boolean setInteractionState(InteractionState state, boolean force) {
		if ((this.currentState == state) && !force) return true;

		if(currentState != null)
			System.out.println("try to set "+state.toString()+" @current "+currentState.toString());
		
		if(force || isAnimationFinished())
			this.currentState = state;

		// TODO Solve addFixture slowdown
		if (force) {
			setFixture(density, friction, restitution, sensor, boundingBoxes[aniDraw], false);
			for (Sensor s : sensors)
				addFiture(s.getFixtureDef()).setUserData(s);
		}
		
		return true;
	}

	public boolean applyAnimation() {
		aniDraw = getInteractionState().getAnimationIndex();
		stateTime = 0;
		
		if(GameProperties.debugMode.equals(Debug.CONSOLE))
			System.out.println(">>apply " + currentState.toString());
		return true;
	}

	public boolean isAnimationFinished() {
		if(currentState != null || currentState.isInterruptable())
			return true;
		
		return animations[aniDraw].isAnimationFinished(stateTime);
	}
	
	private float stateTime = 0;

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		if (!visible) return;

		stateTime += deltaTime;

		TextureRegion frame = new TextureRegion(animations[aniDraw].getKeyFrame(stateTime, true));

		batch.setColor(1, 1, 1, getAlpha());
		batch.draw(frame.getTexture(), getX(), getY(), frame.getRegionWidth() / 2, frame.getRegionHeight() / 2, /* origin */
				frame.getRegionWidth(), frame.getRegionHeight(), 1, 1, /* scale */
				rotation, frame.getRegionX(), frame.getRegionY(), frame.getRegionWidth(), frame.getRegionHeight(),
				flip, false);
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

		setInteractionState(currentState, true);
	}

	public void removeSensor(Sensor sensor) {
		if (sensor.getGameObject() == this) sensor.setGameObject(null);
		if (sensors.remove(sensor)) setInteractionState(currentState, true);
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
	public boolean handleCollision(boolean start, Sensor sender, GameObject other, Sensor otherSensor) {
		return false;
	}

	@Override
	public World getWorld() {
		return body.getWorld();
	}

	@Override
	public Vector2 getWorldPosition() {
		return body.getPosition();
	}

	@Override
	public Vector2 getPosition() {
		return new Vector2(getX(), getY());
	}

	public Body getBody() {
		return body;
	}

	@Override
	public Vector2 getLocalCenterInWorld() {
		return body.getWorldPoint(body.getLocalCenter());
	}

	public InteractionState getInteractionState() {
		return currentState;
	}
}
