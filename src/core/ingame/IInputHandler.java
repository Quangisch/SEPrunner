package core.ingame;

import core.ingame.InputHandler.Click;
import core.ingame.KeyMap.ActionKey;

public interface IInputHandler {

	void addActionKey(ActionKey action, int... keys);

	boolean isKeyDown(ActionKey action);

	boolean isButtonDown(ActionKey action);

	boolean keyUp(ActionKey action);

	Click popClick();
	
	Click getClick();

}
