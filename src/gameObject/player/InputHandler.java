package gameObject.player;

import gameObject.IInteractionStates.InteractionState;
import gameWorld.Map;

import java.util.Set;
import java.util.TreeSet;

import menu.MenuMain;
import misc.GeometricObject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class InputHandler implements InputProcessor {

	private static InputHandler handler;

	private Set<Integer> pressedKeys = new TreeSet<Integer>();
	private Click click;

	private InputHandler() {

	}

	public boolean isKeyDown(int[] key) {
		for (int k : key)
			if (isKeyDown(k)) return true;
		return false;
	}

	public boolean isKeyDown(int key) {
		return pressedKeys.contains(key);
	}

	public static InputHandler getInstance() {
		if (handler == null) handler = new InputHandler();
		return handler;
	}

	@Override
	public boolean keyDown(int keycode) {
		pressedKeys.add(keycode);

		//		toogle Fullscreen
		if (keycode == Keys.ESCAPE) {
			if (Gdx.graphics.isFullscreen())
				Gdx.graphics.setDisplayMode(GameProperties.width, GameProperties.height, false);
			else
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
						Gdx.graphics.getDesktopDisplayMode().height, true);
		}

		//		back to menu
		if (keycode == Keys.BACKSPACE) {
			GameProperties.switchMode(true, false);
			((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		}

		//		toogle debug
		if (keycode == Keys.TAB) {
			GameProperties.Debug.toNext();
			System.out.println("DebugMode " + GameProperties.debugMode.toString());
		}

		//		tmp switchAnimationStates
		if (keycode == Keys.N) {
			Player p = Map.getInstance().getPlayer();
			p.setInteractionState(InteractionState.values()[(p.getInteractionState().ordinal() + 1)
					% InteractionState.values().length]);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		pressedKeys.remove(new Integer(keycode));
		return false;
	}

	public boolean keyUp(int[] keycodes) {
		for (int k : keycodes)
			if (keyUp(k)) return true;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		click = new Click(screenX, screenY, pointer, button);

		return false;
	}

	public boolean buttonDown(int[] button) {
		if (click == null) return false;
		for (int b : button)
			if (click.button == b) return true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		resetClick();
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

	public Click getClick() {
		return click;
	}

	public void resetClick() {
		click = null;
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
			Camera.getInstance().unproject(vec);

			switch (GameProperties.debugMode) {
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
