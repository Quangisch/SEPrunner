package core;

import com.badlogic.gdx.Game;

import core.GameProperties.GameScreen;

public class Project extends Game{

	public static final String TITLE = "SEPrunner", VERSION = "1.0.0";
	private GameScreen initialScreen;
	
	public Project(GameScreen initialScreen) {
		this.initialScreen = initialScreen;
	}
	
	public Project() {
		this(GameScreen.MENU_SPLASH);
	}
	
	@Override
	public void create() {
		GameProperties.initFromFile();
		GameProperties.initPrefDisplayMode();
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
