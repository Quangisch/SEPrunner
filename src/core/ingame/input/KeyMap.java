package core.ingame.input;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.FilePath;

public class KeyMap {

	public static enum ActionKey {
		LEFT, RIGHT, RUN,
		JUMP, CROUCH,
		ACTION, THROW, HOOK;
	}
	
	private Map<ActionKey, Set<Integer>> kMap; 
	public KeyMap() {
		kMap = new HashMap<ActionKey, Set<Integer>>();
		add(ActionKey.THROW, Input.Buttons.LEFT);
		add(ActionKey.HOOK, Input.Buttons.RIGHT);
	}
	
	public void add(ActionKey action, int...keys) {
		if(!kMap.containsKey(action))
			kMap.put(action, new HashSet<Integer>());
		for(int k : keys) {
			for(Map.Entry<ActionKey, Set<Integer>> m : kMap.entrySet())
				m.getValue().remove(k);
			kMap.get(action).add(k);
		}
	}
	
	public boolean remove(int key) {
		for(Map.Entry<ActionKey, Set<Integer>> e : kMap.entrySet()) {
			boolean rm = e.getValue().remove(key);
			if(rm)
				return true;
		}
		return false;
	}
	
	public void init() {
		initFromFile();
	}
	
	public void initDefault() {
		add(ActionKey.LEFT, Keys.A, Keys.LEFT);
		add(ActionKey.RIGHT, Keys.D, Keys.RIGHT);
		add(ActionKey.RUN, Keys.F, Keys.SHIFT_LEFT);

		add(ActionKey.JUMP, Keys.SPACE, Keys.UP);
		add(ActionKey.CROUCH, Keys.S, Keys.DOWN);

		add(ActionKey.ACTION, Keys.E, Keys.ENTER);
		System.out.println("Init Default KeyMap");
	}
	
	public boolean initFromFile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.settings));
		} catch (FileNotFoundException e) {
			System.err.println("settings.json not found");
			initDefault();
			return false;
		}
		
		if(root.hasChild("keyMap")) {
			JsonValue map = root.get("keymap");
			for (ActionKey action : ActionKey.values()) {
				if(map.get(action.toString()) != null) {
					int key = map.getInt(action.toString());
					remove(key);
					add(action, key);
				}
			}
		}
		return true;
	}
	
	public void saveToFile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.settings));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		JsonValue k = root.get("keymap");
		Integer[] a = new Integer[kMap.get(ActionKey.LEFT).size()];
		kMap.get(ActionKey.LEFT).toArray(a);
		k.get("left").set(a.length <= 0 ? -1 : a[0]);
		
		a = new Integer[kMap.get(ActionKey.RIGHT).size()];
		kMap.get(ActionKey.RIGHT).toArray(a);
		k.get("right").set(a.length <= 0 ? -1 : a[0]);
		
		a = new Integer[kMap.get(ActionKey.RUN).size()];
		kMap.get(ActionKey.RUN).toArray(a);
		k.get("run").set(a.length <= 0 ? -1 : a[0]);
		
		a = new Integer[kMap.get(ActionKey.JUMP).size()];
		kMap.get(ActionKey.JUMP).toArray(a);
		k.get("jump").set(a.length <= 0 ? -1 : a[0]);
		
		a = new Integer[kMap.get(ActionKey.CROUCH).size()];
		kMap.get(ActionKey.CROUCH).toArray(a);
		k.get("crouch").set(a.length <= 0 ? -1 : a[0]);
		
		a = new Integer[kMap.get(ActionKey.ACTION).size()];
		kMap.get(ActionKey.ACTION).toArray(a);
		k.get("action").set(a.length <= 0 ? -1 : a[0]);
		
		FileHandle file = Gdx.files.local(FilePath.settings);
		file.writeString(root.toString(), false);
	}

	
	public Set<Integer> get(ActionKey action) {
		return kMap.get(action);
	}
	
	public int getSingle(ActionKey action) {
		if(action == null || kMap.get(action) == null)
			return -1;
		Integer[] i = new Integer[kMap.get(action).size()];
		kMap.get(action).toArray(i);
		return i.length > 0 ? i[0] : -1;
	}
	
	public boolean hasUnmappedActions() {
		for(ActionKey a: ActionKey.values())
			if(getSingle(a) == -1)
				return true;
		return false;
	}

}
