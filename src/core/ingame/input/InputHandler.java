package core.ingame.input;

import java.util.Set;
import java.util.TreeSet;

import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

import core.GameProperties;
import core.GameProperties.GameState;
import core.ingame.Camera;
import core.ingame.input.KeyMap.ActionKey;

public class InputHandler implements IInputHandler, InputProcessor {

	private Set<Integer> pressedKeys = new TreeSet<Integer>();
	private Click click;
	private KeyMap keyMap;
	private Camera camera;

	public InputHandler(Camera camera) {
		initKeyMap();
		this.camera = camera; //TODO TMP: only to debug/visualize points
	}

	private void initKeyMap() {
		keyMap = new KeyMap();
		keyMap.init();
	}

	@Override
	public void addActionKey(ActionKey action, int... keys) {
		keyMap.add(action, keys);
	}

	public boolean isKeyDown(int key) {
		return pressedKeys.contains(key);
	}

	@Override
	public boolean isKeyDown(ActionKey action) {
		if (keyMap != null) for (int k : keyMap.get(action))
			if (isKeyDown(k)) return true;
		return false;
	}

	public boolean isKeyDown(int[] keys) {
		for (int k : keys)
			if (isKeyDown(k)) return true;
		return false;
	}

	@Override
	public boolean isButtonDown(ActionKey action) {
		if (click != null) for (int a : keyMap.get(action))
			if (click.button == a) return true;
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		pressedKeys.add(keycode);

		switch (keycode) {
		// toogle Fullscreen
		case Keys.ESCAPE:
			GameProperties.toogleFullScreen();
			break;

		// toogle pause
		case Keys.P:
			GameProperties.toogleIngamePause();
			break;

		// back to menu
		case Keys.BACKSPACE:
			GameProperties.setGameState(GameState.MENU);
			break;

		// toogle debug
		case Keys.TAB:
			Debug.toogleOnOff();
			break;
		}

		Debug.setMode(keycode);

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return pressedKeys.remove(keycode);
	}

	@Override
	public boolean keyUp(ActionKey action) {
		for (int k : keyMap.get(action))
			keyUp(k);
		return true;
	}

	@Override
	public void keyDown(ActionKey action) {
		for (int k : keyMap.get(action))
			keyDown(k);
	}

	@Override
	public Click getClick() {
		return click;
	}

	//	TODO elsewhere
	@Override
	public Click popClick() {
		Click c = click;
		click = null;
		return c;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		click = new Click(screenX, screenY, pointer, button);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
//		boolean inScreen = !(screenX < 0 || screenY < 0 || screenX > Gdx.graphics.getWidth() || screenY > Gdx.graphics.getHeight());
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public class Click {

		public final int screenX, screenY, pointer, button;
		private GeometricObject geo;

		private Click(int screenX, int screenY, int pointer, int button) {
			this.screenX = screenX;
			this.screenY = screenY;
			this.pointer = pointer;
			this.button = button;

			Vector3 vec = new Vector3(screenX, screenY, 0);
			camera.unproject(vec);

			switch (Debug.getMode()) {
			case CONSOLE:
				System.out.println("clickLocal@" + screenX + "x" + screenY);
				System.out.println("clickReal @" + vec.x + "x" + vec.y);
				break;
			case GEOMETRIC:
				float radius = 10;
				geo = new GeometricObject(new Circle(vec.x - radius, vec.y - radius, radius), Color.CYAN);
				break;
			default:
				break;
			}
		}

		public void draw(SpriteBatch batch) {
			if (geo != null) geo.draw(batch);
		}

		public Click cpy() {
			return new Click(screenX, screenY, pointer, button);
		}

		public String toString() {
			return "Click@" + screenX + "x" + screenY + ", pointer:" + pointer + ", button:" + button;
		}
	}

}
