package gameWorld;

import gameObject.GameObject;
import gameObject.GameObjectData;
import gameObject.player.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class Map implements DrawableMap {

	protected Texture mapTexture;
	protected World world;

	protected List<GameObject> objects;
	protected Player player;

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
		
//		debugRender = new Box2DDebugRenderer();
		debugMatrix = new Matrix4(Camera.getInstance().combined);
	    debugMatrix.scale(GameProperties.PIXELPROMETER, GameProperties.PIXELPROMETER, 0);
	}

	public void draw(SpriteBatch batch) {
		
		player.run();
		
		//		for (Background b : backgrounds)
		batch.disableBlending();
		//			batch.draw(b.texture, b.scrollFactorX * Camera.getInstance().position.x,
		//					b.scrollFactorY * Camera.getInstance().position.y);
		batch.enableBlending();

		if (debugRender != null) debugRender.render(world, debugMatrix);

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
		int j = 0;
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
				vertices[i] = GameProperties.pixelToMeter(JGround.getFloat(i));
				vertices[i+1] = GameProperties.pixelToMeter(mapTexture.getHeight() - JGround.getFloat(i+1));
			}
			p.createChain(vertices);

			ground.addFixture(0, 0.4f, 0, false, p);
		}
		
		ground.setObjectData(GameObjectData.GROUND, j++);
		System.out.println(ground.getObjectData().toString());
		//		objects.add(ground);

		
		
		player = Player.initInstance(world, new Vector2(GameProperties.pixelToMeter(200), GameProperties.pixelToMeter(150)));
		player.init("ninja");
		PolygonShape p = new PolygonShape();
		float[] vertices = {0.5f,0.3f,0.8f,0.3f,
							0.8f,0.4f,0.5f,0.4f};
		p.set(vertices);
		player.addFixture(0, 0, 0, true, p);
		player.setVisible(true);
		player.getBody().setLinearDamping(2.5f);
		player.getBody().setFixedRotation(true);
		player.setObjectData(GameObjectData.PLAYER, 0);
		
		
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

	public void step(float timeStep, int velocityIterations, int positionIterations) {
		world.step(timeStep, velocityIterations, positionIterations);
	}
}
