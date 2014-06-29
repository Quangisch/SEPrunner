package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import misc.StringFunctions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;


public class Highscore {
	
	private static Highscore highscore;
	private Map<Integer, List<Score>> highscoreMap;
	
	private Highscore() {
		highscoreMap = new HashMap<Integer, List<Score>>();
		loadHighscoreList();
	}
	
	private boolean loadHighscoreList() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.highscore));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(root == null)
			return false;
		
		for(int levelIndex = 0; levelIndex < GameProperties.IMPLEMENTED_LEVEL.length; levelIndex++) {
			highscoreMap.put(levelIndex, new LinkedList<Score>());
			if(root.get(levelIndex) != null)
				for(JsonValue s : root.get(levelIndex)) {
					Score score = new Score(levelIndex, s.getString("name"), s.getFloat("time"));
					highscoreMap.get(levelIndex).add(score);
				}
		}
		
		return true;
	}
	
	public static void saveHighscoreList() {
		getInstance().sortAll();
		
		StringBuilder sBuilder = new StringBuilder("[");
		for(int i = 0; i < getInstance().highscoreMap.size(); i++) {
			List<Score> scoreList = getInstance().highscoreMap.get(i);
			sBuilder.append("[");
			for(int s = 0; s < scoreList.size(); s++) {
				sBuilder.append(String.format("{\"name\":\"%s\",\"time\":%s},",scoreList.get(s).PLAYER_NAME, Float.toString(scoreList.get(s).TIME)));
			}
			sBuilder.deleteCharAt(sBuilder.length()-1);
			sBuilder.append("],");
		}
					
		sBuilder.deleteCharAt(sBuilder.length()-1);
		sBuilder.append("]");
		FileHandle file = Gdx.files.local(FilePath.highscore);
		file.writeString(sBuilder.toString(), false);

	}
	
	private void sortAll() {
		for(int levelIndex : highscoreMap.keySet())
			sortLevel(levelIndex);
	}
	
	private void sortLevel(int level) {
		Collections.sort(highscoreMap.get(level));
	}
	
	public static List<Score> getHighscoreList(int levelIndex) {
		for(Integer index : getInstance().highscoreMap.keySet())
			if(index == levelIndex)
				return getInstance().highscoreMap.get(index);
		return null;
	}
	
	public static List<Score> getHighscoreList(Score score, int listSize) {
		List<Score> list = new LinkedList<Score>();
		for(int i = 0; i < listSize; i++)
			list.add(getHighscoreList(score.LEVEL_INDEX).get(i));
		
		if(getPosition(score) > listSize)
			list.remove(listSize - 1);
		
		return list;
			
	}
	
	public static int getPosition(Score score) {
		addHighscore(score);
		int i = 0;
		for(Score s : getHighscoreList(score.LEVEL_INDEX)) {
			if(s.equals(score))
				break;
			i++;
		}
		return i+1;
	}
	
	public static boolean addHighscore(Score score) {
		if(!getInstance().highscoreMap.containsKey(score.LEVEL_INDEX)) {
			System.err.println(Highscore.class.toString()+" @addHighscore(...) : Illegal LevelIndex");
			return false;
		}
		
		boolean add = false;
		if(!getInstance().highscoreMap.get(score.LEVEL_INDEX).contains(score))
			add = getInstance().highscoreMap.get(score.LEVEL_INDEX).add(score);
		
		getInstance().sortLevel(score.LEVEL_INDEX);
		return add;
	}
	
	public static void deleteAll() {
		FileHandle file = Gdx.files.local(FilePath.highscore);
		file.writeString("[]", false);
	}
	
	public static List<List<Score>> getHighscoreLists() {
		List<List<Score>> highscoreLists = new LinkedList<List<Score>>();
		for(int i = 0; i < 3; i++)
			highscoreLists.add(getHighscoreList(i));
		return highscoreLists;
	}
	
	public String toString() {
		StringBuilder score = new StringBuilder();
		for(int i = 0; i < highscoreMap.size(); i++){
			score.append("Level "+(i+1)+" "+GameProperties.IMPLEMENTED_LEVEL[i]+"\n");
			List<Score> scoreList = highscoreMap.get(i);
			for(int s = 0; s < scoreList.size(); s++) {
				score.append(String.format("\t%d.\t%s\t%s", s+1,scoreList.get(s).PLAYER_NAME, scoreList.get(s).TIME_STRING)+"\n");
			}
		}
		return score.toString();
	}
	
	public static Highscore getInstance() {
		if(highscore == null)
			highscore = new Highscore();
		return highscore;
	}
	
	public static class Score implements Comparable <Score> {
		final public String PLAYER_NAME;
		final public float TIME;
		final public String TIME_STRING;
		final public int LEVEL_INDEX;
		
		public Score(int levelIndex, String name, float time) {
			this.PLAYER_NAME = name;
			this.TIME = time;
			this.LEVEL_INDEX = levelIndex;
			
			TIME_STRING = StringFunctions.getTimeAsString(time);
		}

		@Override
		public int compareTo(Score other) {
			return (int)(TIME - other.TIME);
		}
		
		@Override
		public boolean equals(Object object) {
			if(object == null || !(object instanceof Score))
				return false;
			Score s = (Score) object;
			return s.PLAYER_NAME.compareTo(PLAYER_NAME) == 0 && s.TIME == TIME && s.LEVEL_INDEX == LEVEL_INDEX;
		}
	}
	
}
