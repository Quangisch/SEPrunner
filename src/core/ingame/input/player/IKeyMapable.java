package core.ingame.input.player;

import core.ingame.input.player.KeyMap.ActionKey;

public interface IKeyMapable {

	void init();
	void add(ActionKey action, int...keyCode);
	boolean remove(int keyCode);
	void saveToFile();
	int getSingle(ActionKey action);
	boolean hasUnmappedActions();
	
}
