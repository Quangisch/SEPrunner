package core.ingame;

import com.badlogic.gdx.InputProcessor;

import core.ingame.InputHandler.Click;
import core.ingame.KeyMap.ActionKey;

public interface IPlayerInput extends InputProcessor {

	void addActionKey(ActionKey action, int... keys);

	boolean isKeyDown(ActionKey action);

	boolean isButtonDown(ActionKey action);

	boolean keyUp(ActionKey action);

	Click popClick();
	
	Click getClick();

}
