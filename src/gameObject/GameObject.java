package gameObject;

import gameObject.body.Sensor;
import gameObject.interaction.InteractionObject;
import gameWorld.GameWorld;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.GameProperties;

public class GameObject extends InteractionObject implements Comparable<GameObject> {

	private Map<String, Texture> loadingTextures = new HashMap<String, Texture>();

	public GameObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
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

		initStates(root.getString("texture"), root.get("stateframes"), root.getInt("defaultState"));
		initBody(root.get("bodyDef"));
		initSensors(root.get("sensor"));
	}

	private void initStates(String texturePath, JsonValue stateFrames, int defaultStateIndex) {
		int aniPointer = 0;

		for (JsonValue js : stateFrames)
			js.name = js.name.toUpperCase();

		Map<String, Integer> found = new HashMap<String, Integer>();

		for (InteractionState iS : InteractionState.values()) {
			if (found.containsKey(iS.getAnimation())) {
				iS.setAnimationIndex(found.get(iS.getAnimation()));
				continue;
			}

			JsonValue animationFrames = stateFrames.get(iS.getAnimation().toUpperCase());
			if (animationFrames == null) {
				Debug.println(iS.getAnimation() + " not found", Mode.CONSOLE);
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
			setBoundingBox(aniPointer, boundingBox);

			// TEXTURE FRAMES
			i = 0;
			TextureRegion[] textureRegions = new TextureRegion[animationFrames.get("textureMap").size];
			for (JsonValue frame : animationFrames.get("textureMap"))
				textureRegions[i++] = new TextureRegion(getTexture(texturePath), frame.getInt(0), frame.getInt(1),
						frame.getInt(2), frame.getInt(3));

			setAnimation(aniPointer, new Animation(animationFrames.getFloat("frameDuration"), textureRegions),
					iS.getPlayMode());

			found.put(iS.getAnimation(), aniPointer);
			iS.setAnimationIndex(aniPointer++);
		}

		setDefaultInteractionState(InteractionState.values()[defaultStateIndex]);
	}

	private void initBody(JsonValue bodyDef) {
		BodyType bType;
		switch (bodyDef.get("bodyType").asInt()) {
		case 0:
			bType = BodyType.StaticBody;
			break;
		case 1:
			bType = BodyType.KinematicBody;
			break;
		case 2:
		default:
			bType = BodyType.DynamicBody;
			break;
		}

		resetToPrimaryFixture(bType, bodyDef.getFloat("linearDamping"), bodyDef.getFloat("density"),
				bodyDef.getFloat("friction"), bodyDef.getFloat("restitution"), bodyDef.getBoolean("sensor"),
				getBoundingBox(getDefaultInteractionState().getAnimationIndex()), false);
	}

	private void initSensors(JsonValue sensors) {
		if (sensors == null) return;

		for (JsonValue s : sensors) {
			Shape.Type sType;
			switch (s.getInt("shape")) {
			case 0:
				sType = Shape.Type.Chain;
				break;
			case 1:
				sType = Shape.Type.Circle;
				break;
			case 2:
				sType = Shape.Type.Edge;
				break;
			case 3:
			default:
				sType = Shape.Type.Polygon;
				break;
			}

			float[] sensorVertices = new float[s.get("vertices").size];
			int j = 0;
			for (JsonValue sV : s.get("vertices"))
				sensorVertices[j++] = sV.asFloat();

			addSensor(new Sensor(this, sType, sensorVertices, s.getInt("type"), s.getInt("priority")));
		}
	}

	private Texture getTexture(String path) {
		if (!loadingTextures.containsKey(path)) loadingTextures.put(path, new Texture(path));
		return loadingTextures.get(path);
	}

	@Override
	public int compareTo(GameObject other) {
		return this.getLayer() - other.getLayer();
	}

}
