package gameWorld;

import gameObject.GameObject;
import gameObject.IGameObjectTypes;
import gameObject.enemy.Enemy;
import gameObject.player.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

import misc.StringFunctions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class Map implements DrawableMap, Runnable {

	protected MapTexture[] mapTextures;
	protected World world;

	protected List<GameObject> objects;
	protected Player player;

	private List<Runnable> runnables;

	private Box2DDebugRenderer debugRender;
	private Matrix4 debugMatrix;

	// SINGLETON
	private static Map instance;

	public static Map getInstance() {
		if (instance == null) instance = new Map();
		return instance;
	}

	// END SINGLETON

	private Map() {
		objects = new ArrayList<GameObject>();
		debugRender = new Box2DDebugRenderer();
		runnables = new ArrayList<Runnable>();
	}

	public void run() {
		try {
			for (Runnable r : runnables)
				r.run();
		} catch (ConcurrentModificationException e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {

		debugMatrix = new Matrix4(Camera.getInstance().combined);
		debugMatrix.scale(GameProperties.PIXELPROMETER, GameProperties.PIXELPROMETER, 0);

		// batch.disableBlending();
		// for (Background b : backgrounds) {
		//		batch.draw(b.texture, b.scrollFactorX * Camera.getInstance().position.x,
		//		b.scrollFactorY * Camera.getInstance().position.y);
		// }
		// batch.enableBlending();

		for(MapTexture mT : mapTextures)
			if(mT.texture != null)
				batch.draw(mT.texture, mT.x, mT.y);

		Collections.sort(objects, new Comparator<GameObject>() {

			@Override
			public int compare(GameObject a, GameObject b) {
				return a.getLayer() - b.getLayer();
			}
		});

		for (GameObject o : objects)
			o.draw(batch, deltaTime);

		// if (player != null) player.draw(batch);

		if (debugRender != null && GameProperties.debugMode.equals(GameProperties.Debug.BOXRENDERER))
			debugRender.render(world, debugMatrix);

		world.clearForces();
	}

	public void initMap(String json) {
		JsonValue root;
		try {
			root = new JsonReader().parse(new FileReader(json));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		if (world == null)
			world = new World(new Vector2(root.get("gravity").getFloat(0), root.get("gravity").getFloat(1)), false);

		// TEXTURE
		JsonValue mapTextureData = root.get("mapTexture");
		mapTextures = new MapTexture[mapTextureData.size];
		int part = 0;
		for(JsonValue mT : mapTextureData) {
			JsonValue position = mT.get("position");
			mapTextures[part++] = new MapTexture(position.getFloat(0), position.getFloat(1), mT.getString("texture"));
		}

		// GROUND
		JsonValue JGrounds = root.get("ground");
		GameObject ground = new GameObject(world, new Vector2(0, 0));

		for (JsonValue JGround : JGrounds) {
			ChainShape p = new ChainShape();
			float[] vertices = new float[JGround.size];
			for (int i = 0; i < vertices.length; i += 2) {
				vertices[i] = GameProperties.pixelToMeter(JGround.getFloat(i));
//				TODO height
				vertices[i + 1] = GameProperties.pixelToMeter(mapTextures[0].texture.getHeight() - JGround.getFloat(i + 1));
			}
			p.createChain(vertices);

			ground.addFixture(0, 0.4f, 0, false, p, true);
		}

		ground.setGameObjectType(IGameObjectTypes.GameObjectTypes.GROUND);

		if (root.hasChild("objects")) //
			for (JsonValue o : root.get("objects"))
				loadMapObject(o);

		// TODO cleanup
		// init player
		//		player = new Player(world, new Vector2(GameProperties.pixelToMeter(200), GameProperties.pixelToMeter(150)));
		//		player.init("ninja_full");
		//		objects.add(player);

		world.setContactListener(new CollisionHandler());
	}

	private void loadMapObject(JsonValue root) {
		Vector2 pos = GameProperties.pixelToMeter(new Vector2(root.get("position").getFloat(0), root.get("position")
				.getFloat(1)));

		GameObject obj = null;
		switch (StringFunctions.getMostEqualIndexIgnoreCase(root.getString("ID"), new String[] //
				{ "Player", "Enemy" })) {
		case 0:
			obj = new Player(world, pos);
			if (root.getBoolean("isPlayer", false)) player = (Player) obj;
			break;
		case 1:
			obj = new Enemy(world, pos);
			break;
		case -1:
		default:
			return;
		}

		obj.init(root.getString("json"));
		obj.setScale(root.getFloat("scale", 1f));
		obj.setFlip(root.getBoolean("flip", false));

		/*
		AI ai = null;
		switch (StringFunctions.getMostEqualIndexIgnoreCase(root.get("AI").getString("ID"), new String[] //
				{ "SimplePatrolAI" })) {
		case 0:
			ai = new SimplePatrolAI();
			break;
		case 1:
			break;
		case -1:
		default:
			return;
		}
		ai.init(root.get("AI").get("Params"));
		obj.setAI(ai);
		*/

		objects.add(obj);
	}

	public void initMap(int level) {
		resetMap();

		switch (level) {
		case 1:
			initMap("res/map/level1.json");
			break;

		default:
			break;
		}
	}

	public void resetMap() {
		if (world != null) world.dispose();
		world = null;
	}

	public Player getPlayer() {
		return player;
	}

	public void step(float timeStep, int velocityIterations, int positionIterations) {
		world.step(timeStep, velocityIterations, positionIterations);
	}

	public boolean addRunnable(Runnable r) {
		return runnables.add(r);
	}

	public boolean removeRunnable(Runnable r) {
		return runnables.remove(r);
	}

	public boolean addGameObject(GameObject object) {
		return objects.add(object);
	}

	public boolean removeGameObject(GameObject object) {
		return objects.remove(object);
	}
	
	private class MapTexture {
		private final float x, y;
		private final Texture texture;
		
		private MapTexture(float x, float y, String texturePath) {
			this.x = x;
			this.y = y;
			this.texture = new Texture(texturePath);
		}
	}
}
