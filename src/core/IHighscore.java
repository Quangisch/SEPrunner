package core;

import java.util.List;
import java.util.Map;

import core.Highscore.Score;

public interface IHighscore {

	Map<Integer, List<Score>> loadLocalHighscores(String filePath);
	void saveHighscoreList();
	List<Score> getHighscoreList(int levelIndex);
	int getPosition(Score score);
	void addHighscore(Score score);
	void deleteAll();
	
}
