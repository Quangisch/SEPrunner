package gameWorld;

import gameObject.GameObject;
import gameObject.player.InputHandler;
import gameObject.player.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.Camera;

public class Map implements DrawableMap {

	protected Texture mapTexture;
	protected World world;

	protected List<GameObject> objects;
	protected Player player;

	private Box2DDebugRenderer debugRender;

	// SINGLETON
	private static Map instance;

	public static Map getInstance() {
		if (instance == null) instance = new Map();
		return instance;
	}

	// END SINGLETON

	private Map() {
		objects = new ArrayList<GameObject>();
//				debugRender = new Box2DDebugRenderer();
	}

	public void draw(SpriteBatch batch) {
		//		for (Background b : backgrounds)
		batch.disableBlending();
		//			batch.draw(b.texture, b.scrollFactorX * Camera.getInstance().position.x,
		//					b.scrollFactorY * Camera.getInstance().position.y);
		batch.enableBlending();

		if (debugRender != null) debugRender.render(world, Camera.getInstance().combined);

		if (mapTexture != null) batch.draw(mapTexture, 0, 0);

		for (GameObject o : objects)
			o.draw(batch);

		if (player != null) player.draw(batch);

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
			world = new World(new Vector2(root.get("gravity").getFloat(0), root.get("gravity")
					.getFloat(1)), false);

		mapTexture = new Texture(root.getString("mapTexture"));

		// GROUND
		JsonValue JGrounds = root.get("ground");
		GameObject ground = new GameObject(world, new Vector2());
		for (JsonValue JGround : JGrounds) {
			
			/*
			PolygonShape p = new PolygonShape();
			//			float[] vertices = new float[8];
			//			vertices[0] = JGround.getFloat(0);
			//			vertices[1] = JGround.getFloat(1);
			//			vertices[2] = JGround.getFloat(0);
			//			vertices[3] = JGround.getFloat(1) + JGround.getFloat(3);
			//			vertices[4] = JGround.getFloat(0) + JGround.getFloat(2);
			//			vertices[5] = JGround.getFloat(1) + JGround.getFloat(3);
			//			vertices[6] = JGround.getFloat(0) + JGround.getFloat(2);
			//			vertices[7] = JGround.getFloat(1);

			float[] vertices = new float[JGround.size];
			for (int i = 0; i < vertices.length; i++)
				vertices[i] = JGround.getFloat(i);
			*/
			
			ChainShape p = new ChainShape();
			float[] vertices = new float[JGround.size];
			for (int i = 0; i < vertices.length; i += 2) {
				vertices[i] = JGround.getFloat(i);
				vertices[i+1] = mapTexture.getHeight() - JGround.getFloat(i+1);
			}
			p.createChain(vertices);

			ground.addFixture(0, 0, 0, false, p);
		}
		//		objects.add(ground);

		player = new Player(world, new Vector2(200, 150));
		player.init("ninja");
		player.setVisible(true);
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

	public void step(float timeStep, int velocityIterations, int positionIterations) {
		world.step(timeStep, velocityIterations, positionIterations);
	}
}
