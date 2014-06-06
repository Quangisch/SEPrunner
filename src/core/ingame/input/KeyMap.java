package core.ingame.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyMap {

	public static enum ActionKey {
		LEFT, RIGHT, RUN,
		JUMP, CROUCH,
		ACTION, THROW, HOOK;
	}
	
	private Map<ActionKey, Set<Integer>> kMap; 
	public KeyMap() {
		kMap = new HashMap<ActionKey, Set<Integer>>();
	}
	
	public void add(ActionKey action, int...keys) {
		if(!kMap.containsKey(action))
			kMap.put(action, new HashSet<Integer>());
		for(int k : keys)
			kMap.get(action).add(k);
	}
	
	public Set<Integer> get(ActionKey action) {
		return kMap.get(action);
	}

}
