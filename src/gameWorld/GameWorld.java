package gameWorld;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.interaction.GameObject;
import gameObject.interaction.enemy.Alarm;
import gameObject.interaction.enemy.Enemy;
import gameObject.interaction.player.Player;
import gameObject.statics.Hideout;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import misc.Debug;
import misc.StringFunctions;
import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.FilePath;
import core.GameProperties;
import core.GameProperties.GameScreen;
import core.GameProperties.GameState;
import core.exception.LevelNotFoundException;
import core.ingame.Camera;
import core.ingame.IDrawable;
import core.ingame.input.IInputHandler;

public class GameWorld implements IDrawable, Runnable, Disposable {

	private IInputHandler iHandler;
	private Box2DDebugRenderer debugRender;
	private Matrix4 debugMatrix;

	private MapTexture[] mapTextures;
	private Background[] backgrounds;
	private World world;
	private Camera camera;
	private List<GameObject> objects;
	private Player player;
	private RayHandler rayHandler;
	private BodyObject goal;
	private float mapWidth, mapHeight;

	private float timeLimit, time = 0;

	public GameWorld(GameScreen level, IInputHandler iHandler, Camera camera) throws LevelNotFoundException {
		this.camera = camera;
		this.iHandler = iHandler;
		
		objects = new ArrayList<GameObject>();
		debugRender = new Box2DDebugRenderer();

		switch (level) {
		case LEVEL1:
			loadMap(FilePath.level0);
			break;
		
		case LEVEL2:
			loadMap(FilePath.level1);
			break;
		
		case LEVEL3:
			loadMap(FilePath.level2);
			break;
			
		case MENU_BACKGROUND:
			loadMap(FilePath.menuBackground);
			break;
		default:
			throw new LevelNotFoundException();
		}
		
		Alarm.iniInstance(rayHandler, camera);
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
		if(!GameProperties.isCurrentGameState(GameState.NORMAL))
			return;
		time += deltaTime;
		if (timeLimit > 0 && time >= timeLimit) 
			GameProperties.setGameOver("Time Out");
	}

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {

		calcTime(deltaTime);

		debugMatrix = new Matrix4(camera.combined);
		debugMatrix.scale(GameProperties.PIXELPROMETER, GameProperties.PIXELPROMETER, 0);

		if(!GameProperties.lowQuality) {
			for(Background b : backgrounds) {
				b.move(camera.position.x, camera.position.y);
				b.sprite.draw(batch);
			}
			
			for (MapTexture mT : mapTextures)
				if (mT.texture != null)
					batch.draw(mT.texture, mT.x, mT.y);
		}
			
		for (GameObject o : objects)
			o.getAnimationObject().draw(batch, deltaTime);

		batch.end();

		if (debugRender != null && (GameProperties.lowQuality || Debug.isMode(Debug.Mode.BOXRENDERER) || Debug.isMode(Debug.Mode.CAMERA)))
			debugRender.render(world, debugMatrix);

		if (!Debug.isMode(Debug.Mode.LIGHTS_OFF) && Gdx.graphics.isGL20Available()) {
			rayHandler.setCombinedMatrix(camera.combined);
			rayHandler.updateAndRender();
		}

		batch.begin();

	}

	private void loadMap(String json) throws NullPointerException {
		JsonValue root;
		try {
			root = new JsonReader().parse(new FileReader(json));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Gdx.app.exit();
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
			mapTextures[part] = new MapTexture(position.getFloat(0), position.getFloat(1), mT.getString("texture"));
			mapWidth = Math.max(mapTextures[part].x+mapTextures[part].width, mapWidth);
			mapHeight = Math.max(mapTextures[part].y+mapTextures[part].height, mapHeight);
			part++;
		}
		Debug.println("MapSize@"+mapWidth+"x"+mapHeight);
		
		// LIGHTS
		if(Gdx.gl20 != null){
			rayHandler = new RayHandler(world);
			rayHandler.setCulling(false);
			rayHandler.setBlur(true);
			rayHandler.setShadows(true);
			rayHandler.setAmbientLight(1, 1, 1, 0.05f);

			JsonValue jLights = root.get("light");
			if (jLights != null) {
				for (JsonValue jLight : jLights) {

					loadSingleLight(jLight);
				}
			}				
		}
		
		
//		BACKGROUND
		backgrounds = new Background[root.get("background").size];
		part = 0;
		for(JsonValue b : root.get("background")) {
			backgrounds[part] = new Background(new MapTexture(0, 0, b.getString("texture",
					"res/map/background/fog1.png")), b.getFloat("scrollFactor", 0));
			if(b.hasChild("light"))
				for(JsonValue jLight : b.get("light"))
					backgrounds[part].addLight(loadSingleLight(jLight));
			part++;
		}



		// GROUND
		JsonValue jGrounds = root.get("ground");
		BodyObject ground = new BodyObject(world, new Vector2(0, 0));

		for (JsonValue JGround : jGrounds) {
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

		ground.setBodyObjectType(BodyObjectType.Ground);

		if (root.hasChild("objects")) //
			for (JsonValue o : root.get("objects"))
				loadMapObject(o);

		world.setContactFilter(new CollisionHandler.MoverContactFilter());
		world.setContactListener(new CollisionHandler());
		world.setAutoClearForces(true);
		
		// GOAL
		Vector2[] gVecs = new Vector2[root.get("goal").size / 2];
		for (int i = 0; i < gVecs.length; i++)
			gVecs[i] = GameProperties.pixelToMeter(new Vector2(root.get("goal").getFloat(i * 2), //
					mapTextures[0].texture.getHeight() - root.get("goal").getFloat(i * 2 + 1)));
		
		PolygonShape g = new PolygonShape();
		g.setAsBox(gVecs[1].x, gVecs[1].y, new Vector2(0,0), 0);

		goal = new BodyObject(world, gVecs[0]);
		goal.addFixture(0, 0, 0, true, g, true);
		goal.setBodyObjectType(BodyObjectType.Goal);
		
		iHandler.popClick();
	}

	private Light loadSingleLight(JsonValue jLight) {
		int rays = jLight.getInt("rays");
		JsonValue c = jLight.get("color");
		Color color = new Color(c.getFloat(0), c.getFloat(1), c.getFloat(2), c.getFloat(3));

		Light light = null;
		switch (StringFunctions.getMostEqualIndexIgnoreCase(jLight.getString("type"), new String[] { "coneLight",
				"directionalLight", "pointLight" })) {

		case 0:
			light = new ConeLight(rayHandler, rays, color, jLight.getFloat("distance"), jLight.getFloat("x"),
					mapTextures[0].texture.getHeight() - jLight.getFloat("y"), jLight.getFloat("directionDegree"),
					jLight.getFloat("coneDegree"));
			break;
		case 1:
			light = new DirectionalLight(rayHandler, rays, color, jLight.getFloat("directionDegree"));
			break;
		case 2:
			light = new PointLight(rayHandler, rays, color, jLight.getFloat("distance"), jLight.getFloat("x"),
					mapTextures[0].texture.getHeight() - jLight.getFloat("y"));
			break;
		}
		if(light != null)
			light.setXray(true);
		return light;
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
			if (root.hasChild("AI")) ((Enemy) obj).setNewAI(root.get("AI"), root.get("forceMultiplier"));
			break;
		case 2:
			obj = new Hideout(this, pos);
			break;
		case -1:
		default:
			return;
		}

		obj.init(root.get("resources"));
		obj.getAnimationObject().setScale(root.getFloat("scale", 1f));
		obj.getAnimationObject().setFlip(root.getBoolean("flip", false));

		objects.add(obj);
	}

	public void step(float timeStep, int velocityIterations, int positionIterations) {
		world.step(GameProperties.fixedWorldStep ? 1f/60 : timeStep, velocityIterations, positionIterations);
	}

	public boolean addGameObject(GameObject object) {
		return objects.add(object);
	}

	public boolean removeGameObject(GameObject object) {
		object.dispose();
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

	public RayHandler getRayHandler() {
		return rayHandler;
	}
	
	@Override
	public void dispose() {
		if(Gdx.graphics.isGL20Available())
			rayHandler.dispose();
		debugRender.dispose();
		for (MapTexture t : mapTextures)
			t.texture.dispose();
		world.dispose();
		for (GameObject g : objects)
			g.disposeAll();
		for(Background b : backgrounds)
			b.dispose();
	}
	
	public void moveMapTextures(int index, float dx, float dy) {
		if(index < mapTextures.length && index >= 0) {
			mapTextures[index].x += dx;
			mapTextures[index].y += dy;
		}
	}
	
	public void moveMapTextures(float dx, float dy) {
		for(int i = 0; i < mapTextures.length; i++)
			moveMapTextures(i, dx, dy);
	}
	
	public float getMapTextureX(int index) {
		return mapTextures[index].x;
	}

	public BodyObject getGoal() {
		return goal;
	}
	
	public float getTime() {
		return time;
	}
	
	public float getTimeLimit() {
		return timeLimit;
	}
	
	public float getWidth() {
		return mapWidth;
	}
	
	public float getHeight() {
		return mapHeight;
	}
	
	private class MapTexture {

		private float x, y, width, height;
		private final Texture texture;

		private MapTexture(float x, float y, String texturePath) {
			this.x = x;
			this.y = y;
			this.texture = new Texture(texturePath);
			width = texture.getWidth();
			height = texture.getHeight();
		}
	}
	
	private class Background implements Disposable {
		private Sprite sprite;
		private List<Light> lights;
		private float scrollFactor;
		
		private Background(MapTexture mapTexture, float scrollFactor) {
			sprite = new Sprite(mapTexture.texture);
			this.scrollFactor = scrollFactor;
			sprite.scale(mapWidth/sprite.getWidth()*scrollFactor);
		}
		
		private void addLight(Light light) {
			if(lights == null)
				lights = new LinkedList<Light>();
			lights.add(light);
			light.setPosition(light.getPosition().x, mapHeight - light.getPosition().y);
		}
		
		private void move(float camX, float camY) {
			float newX = camX * scrollFactor;
			float newY = camY * scrollFactor;

			float dx = newX - sprite.getX();
			float dy = newY - sprite.getY();
			sprite.translate(dx, dy);
			if(lights == null)
				return;
			for(Light l : lights) {
				l.setPosition(l.getX()+dx, l.getY()+dy);
				
			}
		}
		
		public void dispose() {
			sprite.getTexture().dispose();
		}
	}

}
