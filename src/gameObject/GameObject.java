package gameObject;

import java.io.FileNotFoundException;
import java.io.FileReader;

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

	protected Body body;

	private boolean flip = false;
	private boolean visible = true;
	private volatile boolean grounded = true;
	
	protected String[] stati;
	protected Animation[] animations;
	protected PolygonShape[] boundingBoxes;

	protected int currentStatus;

	public GameObject(World world, Vector2 position) {
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

		// ARRAY INIT
		stati = new String[root.get("animationen").size];
		animations = new Animation[stati.length];
		boundingBoxes = new PolygonShape[stati.length];

		// STATES
		for (int i = 0; i < stati.length; i++)
			stati[i] = root.get("animationen").getString(i);

		for (int j = 0; j < stati.length; j++) {
			JsonValue animationFrames = root.get("stateframes").get(stati[j]);

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
		currentStatus = root.getInt("defaultState");

		// BODYDEF
		setFixture(root.get("bodyDef").getFloat("density"), root.get("bodyDef")
				.getFloat("friction"), root.get("bodyDef").getFloat("restitution"),
				root.get("bodyDef").getBoolean("sensor"), boundingBoxes[currentStatus]);

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
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public int getCurrentStatus() {
		return currentStatus;
	}

	private float stateTime = 0;

	@Override
	public void draw(SpriteBatch batch) {
		if (!visible) 
			return;

		stateTime += Gdx.graphics.getDeltaTime();

		TextureRegion frame = new TextureRegion(animations[currentStatus].getKeyFrame(stateTime, true));
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
			Shape shape) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.isSensor = sensor;
		fixtureDef.shape = shape;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		body.createFixture(fixtureDef);
		shape.dispose();
	}

	@Override
	public void setFixture(float density, float friction, float restitution, boolean sensor,
			Shape shape) {
		for (Fixture f : body.getFixtureList())
			body.destroyFixture(f);
		addFixture(density, friction, restitution, sensor, shape);
	}

	@Override
	public void initBody(BodyDef.BodyType type, float density, float friction, float restitution,
			boolean sensor, Shape shape) {

		setFixture(density, friction, restitution, sensor, shape); // Problem:shape
	}

	// setter der alte werte beh�lt und nur shape �ndert

	
	
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
