package core.ingame;

import gameObject.player.InputHandler;
import gameWorld.Map;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class GameRender implements Screen, ApplicationListener {

	private static GameRender render;
	private int level = 1;
	private SpriteBatch batch;
	private FPSLogger log;
	private List<GeometricObject> geometrics = new ArrayList<GeometricObject>();
	
	private GameRender(int level) {
		this.level = level;
	}

	public void create() {
		batch = new SpriteBatch();
		
		log = new FPSLogger();
		Gdx.input.setInputProcessor(InputHandler.getInstance());
		
//		TODO
		loadResources();
		Map.getInstance().initMap(level);
		
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

	
	public void render() {
		
		Map.getInstance().run();
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		if(Debug.isMode(Debug.Mode.CONSOLE))	
			log.log();
		
		Gdx.gl.glClearColor(255, 255, 255, 1);//(0,0,0,1)
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		Camera.getInstance().update();
		batch.setProjectionMatrix(Camera.getInstance().combined);
		batch.begin();
		
		//Background -> f�hrt aber noch zu Lags
//		Texture backTXT = new Texture(Gdx.files.internal("res/map/Rio.png"));
//		batch.draw(backTXT, 150, 150, 1500, 309);
		
		Map.getInstance().draw(batch, deltaTime); //map

		HUD.getInstance().draw(batch); //userInterface
		
		try {
			for(GeometricObject g : geometrics)
				g.draw(batch);
		} catch (ConcurrentModificationException e) {
//			if(GameProperties.debugMode)
//				e.printStackTrace();
		}

		batch.end();

		Map.getInstance().step(Gdx.graphics.getDeltaTime(), 6, 4);
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

	public static GameRender setInstance(int level) {
		return render = new GameRender(level);
	}
	
	public static GameRender getInstance() {
		if (render == null) 
			render = new GameRender(1);
		return render;
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		render();
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		create();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
	private void loadResources() {
		//TODO
//		manager.load(fileName, type);
	}
	
}
