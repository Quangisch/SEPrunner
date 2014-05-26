package gameWorld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;

public class Map implements DrawableMap {

	protected Texture mapTexture;
	protected World world;

	private Box2DDebugRenderer debugRender = new Box2DDebugRenderer();

	// SINGLETON
	private static Map instance;

	public static Map getInstance() {
		if (instance == null) instance = new Map();
		return instance;
	}

	// END SINGLETON

	private Map() {}

	public void draw(SpriteBatch batch) {
		batch.disableBlending();
		//		for (Background b : backgrounds)
		//			batch.draw(b.texture, b.scrollFactorX * Camera.getInstance().position.x,
		//					b.scrollFactorY * Camera.getInstance().position.y);
		batch.enableBlending();

		if (mapTexture != null) batch.draw(mapTexture, 0, 0);

		if (debugRender != null) debugRender.render(world, Camera.getInstance().combined);
	}

	public void initMap(String json) {
		if (world == null) world = new World(new Vector2(/**/), false);

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
		world.dispose();
		world = null;
	}

	public void step(float timeStep, int velocityIterations, int positionIterations) {
		world.step(timeStep, velocityIterations, positionIterations);
	}
}
