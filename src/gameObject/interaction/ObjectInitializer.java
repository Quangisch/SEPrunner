package gameObject.interaction;

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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.GameProperties;

abstract class ObjectInitializer extends InteractionObject {

	private static Map<String, Texture> loadingTextures = new HashMap<String, Texture>();
	
	protected ObjectInitializer(GameWorld gameWorld) {
		super(gameWorld);
	}
	
	
	protected void init(String name, String jsonPath) {
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
		
		applyInteraction(getDefaultInteractionState());
	}

	private void initStates(String texturePath, JsonValue stateFrames, int defaultStateIndex) {
		
//		TODO necessary?
		for (JsonValue js : stateFrames)
			js.name = js.name.toUpperCase();

		for (InteractionState iS : InteractionState.values()) {
		
			JsonValue animationFrames = stateFrames.get(iS.getAnimationAsString().toUpperCase());
			if (animationFrames == null) {
				Debug.println(iS.getAnimationAsString() + " not found", Mode.CONSOLE);
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
			getBodyObject().addBoundingBox(iS, boundingBox);
			

			// TEXTURE FRAMES
			i = 0;
			TextureRegion[] textureRegions = new TextureRegion[animationFrames.get("textureMap").size];
			for (JsonValue frame : animationFrames.get("textureMap"))
				textureRegions[i++] = new TextureRegion(getTexture(texturePath), frame.getInt(0), frame.getInt(1),
						frame.getInt(2), frame.getInt(3));

			getAnimationObject().addAnimation(iS, new Animation(animationFrames.getFloat("frameDuration"), textureRegions));
		}
		
		String defaultStateName = stateFrames.get(defaultStateIndex).name();
		InteractionState defaultState = null;
		for(InteractionState iS : InteractionState.values()) {
			if(iS.toString().compareToIgnoreCase(defaultStateName) == 0) {
				defaultState = iS;
				break;
			}
		}
		
		setDefaultInteractionState(defaultState);
		
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

		getBodyObject().resetToPrimaryFixture(bType, bodyDef.getFloat("linearDamping"), 
				bodyDef.getFloat("density"), bodyDef.getFloat("friction"), 
				bodyDef.getFloat("restitution"), bodyDef.getBoolean("sensor"),
				getBodyObject().getBoundingBox(getDefaultInteractionState()), false);
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

			getBodyObject().addSensor(sType, sensorVertices, s.getInt("type"), s.getInt("priority"));
		}
	}
	
	private Texture getTexture(String path) {
		if (!loadingTextures.containsKey(path)) loadingTextures.put(path, new Texture(path));
		return loadingTextures.get(path);
	}
	
	protected void disposeTextures() {
		for(String s : loadingTextures.keySet())
			loadingTextures.get(s).dispose();
		loadingTextures.clear();
	}
	
}
