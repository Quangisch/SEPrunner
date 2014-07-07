package core;

import java.util.List;

public interface IPlayerProfile {

	void updateAndSaveProfile();
	void reset();
	void deleteProfile();
	void reorderToIndex(int newIndex);

	PlayerProfile createNewProfile(String name);
	boolean deletePlayerProfile(int profileIndex);
	void deletePlayerProfiles();
	int getProfileCount();
	List<String> getNameList();
	
}
