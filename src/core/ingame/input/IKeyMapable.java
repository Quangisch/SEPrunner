package core.ingame.input;

import core.ingame.input.KeyMap.ActionKey;

public interface IKeyMapable {

	void init();
	void add(ActionKey action, int...keyCode);
	boolean remove(int keyCode);
	void saveToFile();
	int getSingle(ActionKey action);
	boolean hasUnmappedActions();
	
}
