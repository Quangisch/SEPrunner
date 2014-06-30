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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;

import core.AudioManager;
import core.GameProperties;
import core.GameProperties.GameScreen;
import core.exception.LevelNotFoundException;
import core.ingame.input.InputHandler;

public class GameRender implements Screen {

	private ShaderBatch batch;
	private List<GeometricObject> geometrics = new ArrayList<GeometricObject>();

	private GameWorld gameWorld;
	private InputHandler iHandler;
	private Camera camera;
	private HUD hud;
	private PauseMenu pauseMenu;
	private WinMenu2 winMenu;
	private LoseMenu loseMenu;

	public GameRender(GameScreen level) throws LevelNotFoundException {
		camera = new Camera();
		iHandler = new InputHandler(camera);
		gameWorld = new GameWorld(level, iHandler, camera);
		hud = new HUD(gameWorld);
		pauseMenu = new PauseMenu();
		loseMenu = new LoseMenu();
		
		// TODO TMP for debugging
		Debug.init(iHandler, camera);
		GeometricObject.setRender(this);
	}

	@Override
	public void show() {
		batch = new ShaderBatch(100);
		Gdx.input.setInputProcessor(iHandler);
	}

	@Override
	public void dispose() {
		batch.dispose();
		AudioManager.getInstance().dispose();
		gameWorld.dispose();
		for (Disposable d : geometrics)
			d.dispose();
		if(winMenu != null)
			winMenu.dispose();
	}

	public boolean addGeometricObject(GeometricObject geo) {
		return geometrics.add(geo);
	}

	public boolean removeGeometricObject(GeometricObject geo) {
		return geometrics.remove(geo);
	}

	@Override
	public void render(float delta) {
		float realdelta = delta;

		switch (GameProperties.getGameState()) {
		case WIN:
			if(winMenu == null)
				winMenu = new WinMenu2(gameWorld, batch);
		case LOSE:
			break;
		case PAUSE:
			delta = 0;
			break;
		case NORMAL:
		default:
			break;
		}


		gameWorld.run();

		Gdx.gl.glClearColor(0, 0, 0, 1); //(0,0,0,1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.brightness = GameProperties.brightness;
		batch.contrast = GameProperties.contrast;
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		gameWorld.draw(batch, delta); //map

		switch (GameProperties.getGameState()) {
		case NORMAL:
			hud.draw(batch, delta); //userInterface
			break;
		case PAUSE:
			pauseMenu.draw(batch, realdelta);
			break;
		case WIN:
//			Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(new WinMenu2(gameWorld)));
//			winMenu.render(batch, realdelta);
			break;
		case LOSE:
			loseMenu.draw(batch, realdelta);
			break;
		default:
			break;
		}

		try {
			for (GeometricObject g : geometrics)
				g.draw(batch);
		} catch (ConcurrentModificationException e) {
			// if(GameProperties.debugMode)
			// 	e.printStackTrace();
		}


		batch.end();
		
		if(winMenu != null)
			winMenu.render(realdelta);

		gameWorld.step(delta, 6, 4);
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {
		dispose();
	}
}
