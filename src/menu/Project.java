package menu;

import com.badlogic.gdx.Game;

public class Project extends Game{

	public static final String TITLE = "SEPrunner", VERSION = "1.0.0";
	
	@Override
	public void create() {
		setScreen(new Splash());
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
