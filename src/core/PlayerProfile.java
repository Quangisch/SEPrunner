package core;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.exception.ProfileNotFoundException;

public class PlayerProfile {

	/*
	 * name: No1,
		shuriken: 10,
		hookRadius: 100,
		stylePoints: 123,
		experience: 0

	 */
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
		loadProfile();
	}
	
	public PlayerProfile() {
		this(0);
	}
	
	private void loadProfile() {
		
		name = root.get(index).getString("name");
		experience = root.get(index).getInt("experience");
		shuriken = root.get(index).getInt("shuriken");
		hookRadius = root.get(index).getInt("hookRadius");
		stylePoints = root.get(index).getInt("stylePoints");
		System.out.println("loadProfile:"+this.toString());
	}
	
	public void saveProfile() {
		root.get(index).get("name").set(name);
		root.get(index).get("experience").set(experience);
		root.get(index).get("shuriken").set(shuriken);
		root.get(index).get("hookRadius").set(hookRadius);
		root.get(index).get("stylePoints").set(stylePoints);
		
		FileHandle file = Gdx.files.local(FilePath.profile);
		file.writeString(root.toString(), false);
		
		System.out.println(root.toString());
	}
	
	public void deleteProfile() {
		root.remove(index);
		FileHandle file = Gdx.files.local(FilePath.profile);
		file.writeString(root.toString(), false);
	}
	
	public void reorderToIndex(int newIndex) {
		if(newIndex == index || newIndex < 0 || newIndex > root.size) {
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
		
		newProfile.root.get(newProfile.index).setNext(root.get(0));
		root.child = newProfile.root.get(newProfile.index);
		newProfile.saveProfile();
		
		return newProfile;
	}
	
	public static boolean deletePlayerProfile(int index) {
		(new PlayerProfile(index)).deleteProfile();
		return false;
	}
	
}
