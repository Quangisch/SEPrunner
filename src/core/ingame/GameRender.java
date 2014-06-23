package core.ingame;

import gameWorld.GameWorld;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import misc.Debug;
import misc.GeometricObject;
import misc.ShaderBatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;

import core.GameProperties;
import core.ResourceManager;
import core.exception.LevelNotFoundException;
import core.ingame.input.InputHandler;

public class GameRender implements Screen {

	private ShaderBatch batch;
	private FPSLogger log;
	private List<GeometricObject> geometrics = new ArrayList<GeometricObject>();

	private GameWorld gameWorld;
	private InputHandler iHandler;
	private Camera camera;
	private HUD hud;

	public GameRender(int level) throws LevelNotFoundException {
		camera = new Camera();
		iHandler = new InputHandler(camera);
		gameWorld = new GameWorld(level, iHandler, camera);
		hud = new HUD(gameWorld);

		// TODO TMP for debugging
		Debug.init(iHandler, camera);
		GeometricObject.setRender(this);
	}

	@Override
	public void show() {
		batch = new ShaderBatch(100);
		log = new FPSLogger();
		Gdx.input.setInputProcessor(iHandler);
	}

	@Override
	public void dispose() {
		batch.dispose();
		ResourceManager.getInstance().dispose();
		gameWorld.dispose();
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

		gameWorld.run();
		if (Debug.isMode(Debug.Mode.CONSOLE)) log.log();

		Gdx.gl.glClearColor(0, 0, 0, 1);//(0,0,0,1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.brightness = GameProperties.brightness;
		batch.contrast = GameProperties.contrast;
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		gameWorld.draw(batch, delta); //map

		hud.draw(batch, delta); //userInterface

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
}
