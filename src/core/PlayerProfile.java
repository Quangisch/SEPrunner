package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;

import core.exception.ProfileNotFoundException;

public class PlayerProfile {


	private JsonValue root;
	private int index;
	public String name;
	public int shuriken, hookRadius, stylePoints, experience;
	
	private static FileHandle file;
	
	public PlayerProfile(int index) throws ProfileNotFoundException {
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(file == null)
			file = Gdx.files.local(FilePath.profile);
		
		if(root == null)
			root = new JsonValue(ValueType.array);
		
		if(root.size == 0)
			root.child = initNewProfile();
		else if(root.size <= index) {
			this.index = root.size;
			root.get(this.index-1).child = initNewProfile();
		} else if(index < 0) {
			this.index = 0;
			JsonValue newProfile = initNewProfile();
			newProfile.next = root.get(0);
			root.child = newProfile;
		}
		
		this.index = index;
		loadProfile();
	}
	
	public PlayerProfile() {
		this(0);
	}
	
	private static JsonValue initNewProfile() {
		JsonValue singleProfile = new JsonValue(ValueType.object);
		singleProfile.child = new JsonValue(ValueType.object);
		singleProfile.child.name = "name";
		singleProfile.child.set(GameProperties.getInitialName());
		
		singleProfile.child.next = new JsonValue(ValueType.object);
		singleProfile.child.next.name = "shuriken";
		singleProfile.child.next.set(GameProperties.INITIAL_SHURIKENS);
		
		singleProfile.child.next.next = new JsonValue(ValueType.object);
		singleProfile.child.next.next.name = "hookRadius";
		singleProfile.child.next.next.set(GameProperties.INITIAL_HOOK_RADIUS);
		
		singleProfile.child.next.next.next = new JsonValue(ValueType.object);
		singleProfile.child.next.next.next.name = "stylepoints";
		singleProfile.child.next.next.next.set(GameProperties.INITIAL_STYLEPOINTS);
		
		singleProfile.child.next.next.next.next = new JsonValue(ValueType.object);
		singleProfile.child.next.next.next.next.name = "experience";
		singleProfile.child.next.next.next.next.set(0);
		
		return singleProfile;
	}
	
	private void loadProfile() {
		if(root == null || root.size == 0)
			return;
		name = root.get(index).getString("name");
		shuriken = root.get(index).getInt("shuriken");
		hookRadius = root.get(index).getInt("hookRadius");
		stylePoints = root.get(index).getInt("stylePoints");
		experience = root.get(index).getInt("experience");
		
		applyNameCheat(this);
	}
	
	public void updateAndSaveProfile() {
		root.get(index).get("name").set(name);
		root.get(index).get("experience").set(experience);
		root.get(index).get("shuriken").set(shuriken);
		root.get(index).get("hookRadius").set(hookRadius);
		root.get(index).get("stylePoints").set(stylePoints);
		
		applyNameCheat(this);
		
		if(Gdx.files != null) {
			file.writeString(root.toString(), false);
		} else {
			System.err.println(this.getClass()+": No Gdx Instance found\n");
			System.out.println(root.toString());
		}
		
	}
	
	public void reset() {
		name = "NewPlayer";
		shuriken = GameProperties.INITIAL_SHURIKENS;
		hookRadius = GameProperties.INITIAL_HOOK_RADIUS;
		stylePoints = GameProperties.INITIAL_STYLEPOINTS;
		experience = 0;
	}
	
	public void deleteProfile() {
		if(root.size == 1)	root = new JsonValue(ValueType.array);
		else				root.child = root.child.next;
		
		file.writeString(root.toString(), false);
	}
	
	public void reorderToIndex(int newIndex) {
		if(newIndex == index || newIndex < 0 || newIndex > root.size) {
			if(newIndex != index)
				System.err.println(this.getClass()+": Illegal reorderIndex");
			return;
		}
		
		JsonValue profile = root.get(index);
		
		if(newIndex > 0) {
			JsonValue next = root.get(newIndex+1);
			root.remove(index);
			root.get(newIndex-1).setNext(profile);
			profile.setNext(next);
		} else if (newIndex == 0) {
			root.remove(index);
			profile.setNext(root.get(0));
			root.child = profile;
			
		}
		
		this.index = newIndex;
		updateAndSaveProfile();
	}

	public String toString() {
		return String.format("Name: %s [shuriken@%d, hookRadius@%d, stylePoints@%d, experience@%d] @index %d", 
				name, shuriken, hookRadius, stylePoints, experience, index);
	}
	
//	STATIC METHODS
	public static PlayerProfile createNewProfile(String name) {
		PlayerProfile newProfile = new PlayerProfile(-1);
		newProfile.reset();
		newProfile.name = name;
		newProfile.updateAndSaveProfile();
		return newProfile;
	}
	
	private static boolean applyNameCheat(PlayerProfile profile) {
		if(profile.name.compareTo("Cheater") == 0) {
			profile.hookRadius = GameProperties.MAX_HOOK_RADIUS;
			profile.shuriken = profile.stylePoints = profile.experience = 999;
			return true;
		}
		return false;
	}
	
	public static boolean deletePlayerProfile(int index) {
		(new PlayerProfile(index)).deleteProfile();
		return false;
	}
	
	public static void deletePlayerProfiles() {
		if(file == null)
			file = Gdx.files.local(FilePath.profile);
		file.writeString("[]", false);
	}
	
	public static int getProfileCount() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return root == null ? 0 : root.size;
	}
	
	public static List<String> getNameList() {
		List<String> list = new LinkedList<String>();
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for(JsonValue v : root)
			list.add(v.getString("name"));
		return list;
	}
	
}
