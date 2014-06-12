package core.ingame;

import gameWorld.GameWorld;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import core.ingame.input.InputHandler;

public class GameRender implements Screen {

	private SpriteBatch batch;
	private FPSLogger log;
	private List<GeometricObject> geometrics = new ArrayList<GeometricObject>();

	private GameWorld gameWorld;
	private InputHandler iHandler;
	private Camera camera;
//	private HUD hud;

	public GameRender(int level) {
		loadResources(); //TODO
		camera = new Camera();
		iHandler = new InputHandler(camera);
		gameWorld = new GameWorld(level, iHandler, camera);
//		hud = new HUD(gameWorld);

		// TODO TMP for debugging
		Debug.init(iHandler, camera);
		GeometricObject.setRender(this);
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		log = new FPSLogger();
		Gdx.input.setInputProcessor(iHandler);
	}

	@Override
	public void dispose() {
		batch.dispose();
		ResourceManager.getInstance().dispose();

		for (Disposable d : geometrics)
			d.dispose();

	}

	public boolean addGeometricObject(GeometricObject geo) {
		return geometrics.add(geo);
	}

	public boolean removeGeometricObject(GeometricObject geo) {
		return geometrics.remove(geo);
	}

	@Override
	public void render(float delta) {

		switch (GameProperties.getGameState()) {
		case INGAME:
			gameWorld.run();
			break;
		case INGAME_LOSE:
			System.out.println("GAME OVER");
			break;
		case INGAME_PAUSE:
			System.out.println("PAUSE");
			delta = 0;
			break;
		case INGAME_WIN:
			System.out.println("WIN");
			break;
		default:
			break;

		}

		if (Debug.isMode(Debug.Mode.CONSOLE)) log.log();

		Gdx.gl.glClearColor(255, 255, 255, 1);//(0,0,0,1)
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		gameWorld.draw(batch, delta); //map

//		hud.draw(batch, delta); //userInterface

		try {
			for (GeometricObject g : geometrics)
				g.draw(batch);
		} catch (ConcurrentModificationException e) {
			//			if(GameProperties.debugMode)
			//				e.printStackTrace();
		}

		batch.end();

		gameWorld.step(Gdx.graphics.getDeltaTime(), 6, 4);

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	private void loadResources() {
		//TODO
		//		manager.load(fileName, type);
	}

}
