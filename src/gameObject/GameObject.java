package gameObject;

import java.util.HashMap;
import java.util.Map;

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
//
//

public class GameObject implements DrawableStatic, Moveable {

	protected Body body;
	protected TextureRegion textRG;

	private boolean flip;
	private boolean visible;

	protected String[] stati;
	protected Animation[] animations;
	protected PolygonShape[] boundingBoxes;

	protected int currentStatus;// animation die bei draw ausgef�hrt wird

	public GameObject(World world, Vector2 position) {
		// init bodyDef
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		body = world.createBody(bodyDef);
	}

	// neu
	public void init(String name) {
		init(name, "res/sprites/" + name + ".json");
		// init(name, "res/sprites/" + name + ".json");
	}

	// aktuellen status speichern
	public void init(String name, String jsonPath) {
		// an neues json anpassen
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(jsonPath).get(name);

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
				vertices[i++] = v.asFloat();
			boundingBox.set(vertices);
			boundingBoxes[i] = boundingBox;

			// TEXTURE FRAMES
			i = 0;
			TextureRegion[] textureRegions = new TextureRegion[animationFrames
					.get("textureMap").size];
			for (JsonValue frame : animationFrames.get("textureMap"))
				textureRegions[i++] = new TextureRegion(new Texture(root.get(
						"texture").asString()), frame.getInt(0),
						frame.getInt(1), frame.getInt(2), frame.getInt(3));

			animations[i] = new Animation(
					animationFrames.getFloat("frameDuration"), textureRegions);

			// STATUS
			currentStatus = root.getInt("defaultState");
		}

		// BODYDEF
		setFixture(root.get("bodyDef").getFloat("density"), root.get("bodyDef")
				.getFloat("friction"),
				root.get("bodyDef").getFloat("restitution"), root
						.get("bodyDef").getBoolean("sensor"),
				boundingBoxes[currentStatus]);

		switch (root.get("bodyDef").get("bodyType").asInt()) {
		case 0:
			body.setType(BodyType.StaticBody);
		case 1:
			body.setType(BodyType.KinematicBody);
		case 2:
			body.setType(BodyType.DynamicBody);
		}
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	//

	@Override
	public void draw(SpriteBatch batch) {
		if (visible)
			batch.draw(textRG,
					GameProperties.meterToPixel(body.getPosition().x),
					GameProperties.meterToPixel(body.getPosition().y));
		// aktuellen status des gameobjekts abfragen und ausf�hren
	}

	@Override
	public void setFlip(boolean flip) {
		if (this.flip == flip)
			return;
		flip();
	}

	@Override
	public void flip() {
		flip = !flip;
		textRG.flip(true, false);
	}

	@Override
	public boolean isFlipped() {
		return flip;
	}

	// @Override
	public void addFixture(float density, float friction, float restitution,
			boolean sensor, Shape shape) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.isSensor = sensor;
		fixtureDef.shape = shape;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		body.createFixture(fixtureDef);
		shape.dispose();
	}

	// @Override
	public void setFixture(float density, float friction, float restitution,
			boolean sensor, Shape shape) {
		for (Fixture f : body.getFixtureList())
			body.destroyFixture(f);
		addFixture(density, friction, restitution, sensor, shape);
	}

	/**
	 * 
	 * @param world
	 *            Kollisionsebene
	 * @param type
	 *            Beweglichkeit
	 * @param position
	 *            Positionsvektor
	 * @param density
	 *            Dichte
	 * @param friction
	 *            Reibungskoeffizient
	 * @param restitution
	 *            Elastizitätskoeffizient
	 * @param sensor
	 *            Durchlässigkeit
	 * @param shape
	 *            geometrische Form
	 */
	// @Override
	// �ndern: -world -position -shape
	public void initBody(BodyDef.BodyType type, float density, float friction,
			float restitution, boolean sensor, Shape shape) {

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
}
