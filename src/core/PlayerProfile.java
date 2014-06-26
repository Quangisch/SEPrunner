package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.exception.ProfileNotFoundException;

public class PlayerProfile {


	private JsonValue root;
	private int index;
	public String name;
	public int shuriken, hookRadius, stylePoints, experience;
	
	public PlayerProfile(int index) throws ProfileNotFoundException {
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(index > root.size || index < 0)
			throw new ProfileNotFoundException();
		
		this.index = index;
		
		if(root.size == 0)
			initNewFile();
		
		loadProfile();
	}
	
	public PlayerProfile() {
		this(0);
	}
	
	private void initNewFile() {
		if(root.size == 0) {
			FileHandle file = Gdx.files.local(FilePath.profile);
			file.writeString("[{name: NewPlayer,shuriken: 10,hookRadius: 100,stylePoints: 0,experience: 0}]", false);
			
			try {
				root = new JsonReader().parse(new FileReader(FilePath.profile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadProfile() {
		
		name = root.get(index).getString("name");
		experience = root.get(index).getInt("experience");
		shuriken = root.get(index).getInt("shuriken");
		hookRadius = root.get(index).getInt("hookRadius");
		stylePoints = root.get(index).getInt("stylePoints");
		
		applyNameCheat(this);
	}
	
	public void saveProfile() {
		root.get(index).get("name").set(name);
		root.get(index).get("experience").set(experience);
		root.get(index).get("shuriken").set(shuriken);
		root.get(index).get("hookRadius").set(hookRadius);
		root.get(index).get("stylePoints").set(stylePoints);
		
		applyNameCheat(this);
		
		if(Gdx.files != null) {
			FileHandle file = Gdx.files.local(FilePath.profile);
			file.writeString(root.toString(), false);
		} else {
			System.err.println(this.getClass()+": No Gdx Instance found\n");
			System.out.println(root.toString());
		}
		
	}
	
	public void reset() {
		name = "NewPlayer";
		shuriken = 10;
		hookRadius = 100;
		experience = stylePoints = 0;
	}
	
	public void deleteProfile() {
		root.remove(index);
		FileHandle file = Gdx.files.local(FilePath.profile);
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
		saveProfile();
	}

	public String toString() {
		return String.format("Name: %s [shuriken@%d, hookRadius@%d, stylePoints@%d, experience@%d] @index %d", 
				name, shuriken, hookRadius, stylePoints, experience, index);
	}
	
//	STATIC METHODS
	public static PlayerProfile createNewProfile(String name) {
		PlayerProfile newProfile = new PlayerProfile();
		
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		newProfile.name = name;
		newProfile.experience = newProfile.stylePoints = 0;
		newProfile.hookRadius = 100;
		newProfile.shuriken = 10;
		
		applyNameCheat(newProfile);
		
		newProfile.root.get(newProfile.index).setNext(root.get(0));
		root.child = newProfile.root.get(newProfile.index);
		newProfile.saveProfile();
		
		return newProfile;
	}
	
	private static boolean applyNameCheat(PlayerProfile profile) {
		if(profile.name.compareTo("Cheater") == 0) {
			profile.hookRadius = GameProperties.HOOK_RADIUS_MAX;
			profile.shuriken = profile.stylePoints = profile.experience = 999;
			return true;
		}
		return false;
	}
	
	public static boolean deletePlayerProfile(int index) {
		(new PlayerProfile(index)).deleteProfile();
		return false;
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
