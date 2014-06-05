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

public class GameRender implements Screen {

	private SpriteBatch batch;
	private FPSLogger log;
	private List<GeometricObject> geometrics = new ArrayList<GeometricObject>();
	
	private GameWorld gameWorld;
	private IPlayerInput iHandler;
	
	public GameRender(int level) {
		loadResources();	//TODO
		iHandler = new InputHandler();
		gameWorld = new GameWorld(level, iHandler);
		
//		TMP for DebugMode.Geometric
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
		
		for(Disposable d : geometrics)
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
		gameWorld.run();
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		if(Debug.isMode(Debug.Mode.CONSOLE))	
			log.log();
		
		Gdx.gl.glClearColor(255, 255, 255, 1);//(0,0,0,1)
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		Camera.getInstance().update();
		batch.setProjectionMatrix(Camera.getInstance().combined);
		batch.begin();
		
		gameWorld.draw(batch, deltaTime); //map

		HUD.getInstance().draw(batch); //userInterface
		
		try {
			for(GeometricObject g : geometrics)
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
