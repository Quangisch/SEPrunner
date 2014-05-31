package gameObject.player;

import java.util.Set;
import java.util.TreeSet;

import menu.MenuMain;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import core.ingame.GameProperties;

public class InputHandler implements InputProcessor {

	private static InputHandler handler;

	private Set<Integer> pressedKeys = new TreeSet<Integer>();

	private InputHandler() {

	}

	public boolean isKeyDown(int[] key) {
		for(int k : key)
			if(isKeyDown(k))
				return true;
		return false;
	}
	
	public boolean isKeyDown(int key) {
		return pressedKeys.contains(key);
	}

	public static InputHandler getInstance() {
		if (handler == null)
			handler = new InputHandler();
		return handler;
	}

	@Override
	public boolean keyDown(int keycode) {
		pressedKeys.add(keycode);
		
//		toogle Fullscreen
		if(keycode == Keys.ESCAPE) {
			if(Gdx.graphics.isFullscreen())
				Gdx.graphics.setDisplayMode(GameProperties.width, GameProperties.height, false);
			else
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
		}
		
//		back to menu
		if(keycode == Keys.BACKSPACE) {
			GameProperties.switchMode(true, false);
			((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		}
		
//		toogle debug
		if(keycode == Keys.TAB)
			GameProperties.debugMode = !GameProperties.debugMode;
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		pressedKeys.remove(new Integer(keycode));
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
