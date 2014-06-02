package gameWorld;

import gameObject.GameObject;
import gameObject.IGameObjectTypes;
import gameObject.player.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

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

	protected Texture mapTexture;
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
			for(Runnable r : runnables)
				r.run();
		} catch (ConcurrentModificationException e) {
//			e.printStackTrace();
		}
	}
	

	public void draw(SpriteBatch batch) {
		
		debugMatrix = new Matrix4(Camera.getInstance().combined);
		debugMatrix.scale(GameProperties.PIXELPROMETER, GameProperties.PIXELPROMETER, 0);

		//		batch.disableBlending();
		//		for (Background b : backgrounds) {
		//			batch.draw(b.texture, b.scrollFactorX * Camera.getInstance().position.x,
		//			b.scrollFactorY * Camera.getInstance().position.y);
		//		}
		// 		batch.enableBlending();

		if (mapTexture != null) batch.draw(mapTexture, 0, 0);

		for (GameObject o : objects)
			o.draw(batch);

		if (player != null) player.draw(batch);

		if (debugRender != null && GameProperties.debugMode.equals(GameProperties.Debug.BOXRENDERER)) 
			debugRender.render(world, debugMatrix);

		world.clearForces();
	}

	public void initMap(String json) {
		JsonValue root;
		try {
			root = new JsonReader().parse(new FileReader(json));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		if (world == null)
			world = new World(new Vector2(root.get("gravity").getFloat(0), root.get("gravity").getFloat(1)), false);

		mapTexture = new Texture(root.getString("mapTexture"));

		// GROUND
		JsonValue JGrounds = root.get("ground");
		GameObject ground = new GameObject(world, new Vector2(0, 0));

		for (JsonValue JGround : JGrounds) {
			ChainShape p = new ChainShape();
			float[] vertices = new float[JGround.size];
			for (int i = 0; i < vertices.length; i += 2) {
				vertices[i] = GameProperties.pixelToMeter(JGround.getFloat(i));
				vertices[i + 1] = GameProperties.pixelToMeter(mapTexture.getHeight() - JGround.getFloat(i + 1));
			}
			p.createChain(vertices);

			ground.addFixture(0, 0.4f, 0, false, p, true);
		}

		ground.setGameObjectType(IGameObjectTypes.GameObjectTypes.GROUND);

		// TODO cleanup
		// init player
		player = new Player(world, new Vector2(GameProperties.pixelToMeter(200), GameProperties.pixelToMeter(150)));
		player.init("ninja_full");

		// init gameObjects
		world.setContactListener(new CollisionHandler());
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
}
