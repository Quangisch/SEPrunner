package gameWorld;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.interaction.GameObject;
import gameObject.interaction.enemy.Enemy;
import gameObject.interaction.player.Player;
import gameObject.statics.Hideout;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import misc.Debug;
import misc.StringFunctions;

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
import core.ingame.IDrawable;
import core.ingame.input.IInputHandler;

public class GameWorld implements IDrawable, Runnable {

	private IInputHandler iHandler;
	private Box2DDebugRenderer debugRender;
	private Matrix4 debugMatrix;

	private MapTexture[] mapTextures;
	private World world;
	private Camera camera;
	private List<GameObject> objects;
	private Player player;

	private float timeLimit, time = 0;

	public GameWorld(int level, IInputHandler iHandler, Camera camera) {
		this.camera = camera;
		this.iHandler = iHandler;
		objects = new ArrayList<GameObject>();
		debugRender = new Box2DDebugRenderer();

		switch (level) {
		case 1:
			loadMap("res/map/level1.json");
			break;

		default:
			break;
		}
	}

	public void run() {
		for (Iterator<GameObject> i = objects.iterator(); i.hasNext();) {
			GameObject g = (GameObject) i.next();
			if (g.getBodyObject().willDisposed()) {
				g.getBodyObject().disposeUnsafe();
				i.remove();
			}
		}

		try {
			for (Runnable r : objects)
				r.run();
		} catch (ConcurrentModificationException e) {
			// e.printStackTrace();
		}

		Collections.sort(objects);
	}

	private void calcTime(float deltaTime) {
		if (time < timeLimit) {
			time += deltaTime;
			System.out.println("Remaining Time: " + Float.toString(timeLimit - time).substring(0, 4) + " min");
			if (time >= timeLimit) GameProperties.setGameOver();
		}
	}

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {

		//		TODO
		calcTime(deltaTime);

		debugMatrix = new Matrix4(camera.combined);
		debugMatrix.scale(GameProperties.PIXELPROMETER, GameProperties.PIXELPROMETER, 0);

		// batch.disableBlending();
		// for (Background b : backgrounds) {
		//		batch.draw(b.texture, b.scrollFactorX * Camera.getInstance().position.x,
		//		b.scrollFactorY * Camera.getInstance().position.y);
		// }
		// batch.enableBlending();

		for (MapTexture mT : mapTextures)
			if (mT.texture != null) batch.draw(mT.texture, mT.x, mT.y);

		for (GameObject o : objects)
			o.getAnimationObject().draw(batch, deltaTime);

		if (debugRender != null && (Debug.isMode(Debug.Mode.BOXRENDERER) || Debug.isMode(Debug.Mode.CAMERA)))
			debugRender.render(world, debugMatrix);

		world.clearForces();
	}

	private void loadMap(String json) throws NullPointerException {
		JsonValue root;
		try {
			root = new JsonReader().parse(new FileReader(json));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		if (world == null)
			world = new World(new Vector2(root.get("gravity").getFloat(0), root.get("gravity").getFloat(1)), false);

		timeLimit = root.getFloat("timelimit");

		// TEXTURE
		JsonValue mapTextureData = root.get("mapTexture");
		mapTextures = new MapTexture[mapTextureData.size];
		int part = 0;
		for (JsonValue mT : mapTextureData) {
			JsonValue position = mT.get("position");
			mapTextures[part++] = new MapTexture(position.getFloat(0), position.getFloat(1), mT.getString("texture"));
		}

		// GOAL
		BodyObject goal = new BodyObject(this, new Vector2(0, 0));
		PolygonShape g = new PolygonShape();
		Vector2[] gVecs = new Vector2[root.get("goal").size / 2];
		for (int i = 0; i < gVecs.length; i++)
			gVecs[i] = GameProperties.pixelToMeter(new Vector2(root.get("goal").getFloat(i * 2), //
					mapTextures[0].texture.getHeight() - root.get("goal").getFloat(i * 2 + 1)));

		g.set(gVecs);
		goal.addFixture(0, 0, 0, true, g, true);
		goal.setGameObjectType(GameObjectType.Goal);
		
		// GROUND
		JsonValue JGrounds = root.get("ground");
		BodyObject ground = new BodyObject(this, new Vector2(0, 0));

		for (JsonValue JGround : JGrounds) {
			ChainShape p = new ChainShape();
			Vector2[] vertices = new Vector2[JGround.size / 2];
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = GameProperties.pixelToMeter(new Vector2(JGround.getFloat(i * 2), //
						mapTextures[0].texture.getHeight() - JGround.getFloat(i * 2 + 1)));
				//		vertices[i] = GameProperties.pixelToMeter(JGround.getFloat(i));
				////		TODO height
				//		vertices[i + 1] = GameProperties.pixelToMeter(mapTextures[0].texture.getHeight() - JGround.getFloat(i + 1));
			}
			p.createLoop(vertices);

			ground.addFixture(0, 0.4f, 0, false, p, true);
		}

		ground.setGameObjectType(GameObjectType.Ground);

		if (root.hasChild("objects")) //
			for (JsonValue o : root.get("objects"))
				loadMapObject(o);

		world.setContactFilter(new CollisionHandler.MoverContactFilter());
		world.setContactListener(new CollisionHandler());
	}

	private void loadMapObject(JsonValue root) throws NullPointerException {
		Vector2 pos = GameProperties.pixelToMeter(new Vector2(root.get("position").getFloat(0), root.get("position")
				.getFloat(1)));

		GameObject obj = null;
		switch (StringFunctions.getMostEqualIndexIgnoreCase(root.getString("ID"), new String[] //
				{ "player", "enemy", "hidable" })) {
		case 0:
			obj = new Player(iHandler, this, pos);
			if (root.getBoolean("isPlayer", false)) player = (Player) obj;
			break;
		case 1:
			obj = new Enemy(this, pos);
			if (root.hasChild("AI")) ((Enemy) obj).setNewAI(root.get("AI"));
			break;
		case 2:
			obj = new Hideout(this, pos);
			break;
		case -1:
		default:
			return;
		}

		obj.init(root.getString("json"));
		obj.getAnimationObject().setScale(root.getFloat("scale", 1f));
		obj.getAnimationObject().setFlip(root.getBoolean("flip", false));

		objects.add(obj);
	}

	public void step(float timeStep, int velocityIterations, int positionIterations) {
		world.step(timeStep, velocityIterations, positionIterations);
	}

	public boolean addGameObject(GameObject object) {
		return objects.add(object);
	}

	public boolean removeGameObject(BodyObject object) {
		return objects.remove(object);
	}

	public Player getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}

	public Camera getCamera() {
		return camera;
	}

	private static class MapTexture {

		private final float x, y;
		private final Texture texture;

		private MapTexture(float x, float y, String texturePath) {
			this.x = x;
			this.y = y;
			this.texture = new Texture(texturePath);
		}
	}
}
