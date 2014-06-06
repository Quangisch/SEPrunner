package menu;

import com.badlogic.gdx.Game;

import core.ingame.GameProperties;
import core.ingame.GameRender;

public class Project extends Game{

	public static final String TITLE = "SEPrunner", VERSION = "1.0.0";
	
	
	
	@Override
	public void create() {
		if(GameProperties.isInMenuState())
			setScreen(new Splash()); //set first img, next Z56 in splash
		else 
			setScreen(new GameRender(1));
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
		super.dispose();
	}

}
