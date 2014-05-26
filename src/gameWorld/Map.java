package gameWorld;

import gameObject.GameObject;
import gameObject.player.Player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

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
		debugRender = new Box2DDebugRenderer();
	}

	public void draw(SpriteBatch batch) {
		batch.disableBlending();
		//		for (Background b : backgrounds)
		//			batch.draw(b.texture, b.scrollFactorX * Camera.getInstance().position.x,
		//					b.scrollFactorY * Camera.getInstance().position.y);
		batch.enableBlending();

		if (mapTexture != null) batch.draw(mapTexture, 0, 0);

		for (GameObject o : objects) {
			o.draw(batch);
		}

		if (player != null) player.draw(batch);

		if (debugRender != null) debugRender.render(world, Camera.getInstance().combined);
	}

	public void initMap(String json) {
		if (world == null) world = new World(new Vector2(0, -10), false);

		player = new Player(world, new Vector2(50, 180));
		player.init("goku");
		player.setVisible(true);

		GameObject goku2 = new GameObject(world, new Vector2(50, 50));
		goku2.init("goku2");
		goku2.setVisible(true);

		objects.add(goku2);
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
