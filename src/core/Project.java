package core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import core.GameProperties.GameScreen;

public class Project extends Game{

	public static final String TITLE = "SEPrunner", VERSION = "1.0.0";
	private GameScreen initialScreen;
	
	public Project(GameScreen initialScreen) {
		this.initialScreen = initialScreen;
	}
	
	@Override
	public void create() {
		GameProperties.SIZE_WIDTH = Gdx.graphics.getDesktopDisplayMode().width;
		GameProperties.SIZE_HEIGHT = Gdx.graphics.getDesktopDisplayMode().height;
		GameProperties.switchGameScreen(initialScreen);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		ResourceManager.resetInstance();
		super.dispose();
	}

}
