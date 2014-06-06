package core.ingame.input;

import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public interface IInputHandler {

	void addActionKey(ActionKey action, int... keys);

	boolean isKeyDown(ActionKey action);

	boolean isButtonDown(ActionKey action);

	boolean keyUp(ActionKey action);

	Click popClick();
	
	Click getClick();

}
