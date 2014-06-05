package core.ingame;

import gameObject.IInteractionStates.InteractionState;
import gameObject.player.Player;
import gameWorld.Map;

import java.util.Set;
import java.util.TreeSet;

import menu.MenuMain;
import misc.Debug;
import misc.GeometricObject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

import core.ingame.KeyMap.ActionKey;

public class InputHandler implements InputProcessor, IInputHandler {

	private Set<Integer> pressedKeys = new TreeSet<Integer>();
	private Click click;

	protected KeyMap keyMap;

	private static InputHandler instance;

	public static InputHandler getInstance() {
		if (instance == null)
			instance = new InputHandler();
		return instance;
	}

	private InputHandler() {
		keyMap = new KeyMap();
	}

	public void addActionKey(ActionKey action, int... keys) {
		keyMap.add(action, keys);
	}

	public boolean isKeyDown(int key) {
		return pressedKeys.contains(key);
	}

	@Override
	public boolean isKeyDown(ActionKey action) {
		if (keyMap != null)
			for (int k : keyMap.get(action))
				if (isKeyDown(k))
					return true;
		return false;
	}

	public boolean isKeyDown(int[] keys) {
		for (int k : keys)
			if (isKeyDown(k))
				return true;
		return false;
	}

	@Override
	public boolean isButtonDown(ActionKey action) {
		if (click != null)
			for (int a : keyMap.get(action))
				if (click.button == a)
					return true;
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		pressedKeys.add(keycode);

		// toogle Fullscreen
		if (keycode == Keys.ESCAPE) {
			if (Gdx.graphics.isFullscreen())
				Gdx.graphics.setDisplayMode(GameProperties.width,
						GameProperties.height, false);
			else
				Gdx.graphics.setDisplayMode(
						Gdx.graphics.getDesktopDisplayMode().width,
						Gdx.graphics.getDesktopDisplayMode().height, true);
		}

		// back to menu
		if (keycode == Keys.BACKSPACE) {
			GameProperties.switchMode(true, false);
			((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		}

		// toogle debug
		if (keycode == Keys.TAB)
			Debug.toogleOnOff();

		if (Debug.isOn())
			Debug.setMode(keycode);

		// tmp switchAnimationStates
		if (keycode == Keys.N) {
			Player p = Map.getInstance().getPlayer();
			p.setInteractionState(InteractionState.values()[(p
					.getInteractionState().ordinal() + 1)
					% InteractionState.values().length]);
		}
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
		// TODO Auto-generated method stub
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
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public static class Click {

		public final int screenX, screenY, pointer, button;
		private GeometricObject geo;

		private Click(int screenX, int screenY, int pointer, int button) {
			this.screenX = screenX;
			this.screenY = screenY;
			this.pointer = pointer;
			this.button = button;

			Vector3 vec = new Vector3(screenX, screenY, 0);
			Camera.getInstance().unproject(vec);

			switch (Debug.getMode()) {
			case CONSOLE:
				System.out.println("clickLocal@" + screenX + "x" + screenY);
				System.out.println("clickReal @" + vec.x + "x" + vec.y);
				break;
			case GEOMETRIC:
				float radius = 10;
				geo = new GeometricObject(new Circle(vec.x - radius, vec.y
						- radius, radius), Color.CYAN);
				break;
			default:
				break;
			}
		}

		public void draw(SpriteBatch batch) {
			if (geo != null)
				geo.draw(batch);
		}
		
		public Click cpy() {
			return new Click(screenX, screenY, pointer, button);
		}

		public String toString() {
			return "Click@" + screenX + "x" + screenY + ", pointer:" + pointer
					+ ", button:" + button;
		}
	}

}
