package core;

import com.badlogic.gdx.Game;

import core.GameProperties.GameState;
import core.ingame.GameRender;
import core.menu.MenuProfile;

public class Project extends Game{

	public static final String TITLE = "SEPrunner", VERSION = "1.0.0";

	public Project(GameState state) {
		GameProperties.setGameState(state, 0);
	}
	
	@Override
	public void create() {
//		GameProperties.initFromFile();
		ResourceManager.getInstance().startMusic();
		if(GameProperties.isInMenuState())
			setScreen(new MenuProfile()); //set first img, next Z56 in splash
		else 
			setScreen(new GameRender(0));
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
