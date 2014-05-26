package gameObject;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
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

	protected Map<String, Animation> anim = new HashMap<String, Animation>();
	protected Animation currentStatus;

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
		//an neues json anpassen
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(jsonPath).get(name);

		// BODYDEF
		setFixture(root.get("bodyDef").getFloat("density"), root.get("bodyDef")
				.getFloat("friction"),
				root.get("bodyDef").getFloat("restitution"), root
						.get("bodyDef").getBoolean("sensor"), shape);

		switch (root.get("bodyDef").get("bodyType").asInt()) {
		case 0:
			body.setType(BodyType.StaticBody);
		case 1:
			body.setType(BodyType.KinematicBody);
		case 2:
			body.setType(BodyType.DynamicBody);
		}

		// was soll mit shape geschehen? -> bounding box?

		// STATES
		for (JsonValue j : root.get("animationen"))
			anim.put(j.asString(), null);

		// ANIMATIONS
		for (Map.Entry<String, Animation> a : anim.entrySet()) {
			JsonValue animations = root.get("stateframes").get(a.getKey());

			Animation a_curr = new Animation();

			// FRAMES
			for (JsonValue frame : animations) {
				JsonValue textureMap = frame.get("textureMap");
				TextureRegion texts = new TextureRegion(new Texture(root.get(
						"texture").asString()), textureMap.getInt(0),
						textureMap.getInt(1), textureMap.getInt(2),
						textureMap.getInt(3));

				PolygonShape boundingBox = new PolygonShape();
				JsonValue bBox = frame.get("boundingBox");
				int i = 0;
				float[] vertices = new float[bBox.size];
				for (JsonValue v : bBox)
					vertices[i++] = v.asFloat();
				boundingBox.set(vertices);

				a_curr.addFrame(boundingBox, texts);
			}
			anim.put(a.getKey(), a_curr);
		}

		//

		// pro Objekt eigenes json anlegen
		// also pro Ordnertyp

	}
	
	public void setCurrentStatus(Animation currentStatus){
		this.currentStatus = currentStatus;
	}

	//

	@Override
	public void draw(SpriteBatch batch) {
		if (visible)
			batch.draw(textRG,
					GameProperties.meterToPixel(body.getPosition().x),
					GameProperties.meterToPixel(body.getPosition().y));
		// aktuellen status des gameobjekts abfragen und ausführen
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
	 *            ElastizitÃ¤tskoeffizient
	 * @param sensor
	 *            DurchlÃ¤ssigkeit
	 * @param shape
	 *            geometrische Form
	 */
	// @Override
	// ändern: -world -position -shape
	public void initBody(BodyDef.BodyType type, float density, float friction,
			float restitution, boolean sensor) {

		setFixture(density, friction, restitution, sensor, shape); // Problem:shape
	}

	// setter der alte werte behält und nur shape ändert

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
