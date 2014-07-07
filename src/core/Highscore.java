package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import misc.StringFunctions;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;


public class Highscore {
	
	private static Highscore highscore;
	private Map<Integer, List<Score>> highscoreMap;
	
	private Highscore() {
		highscoreMap = new HashMap<Integer, List<Score>>();
		loadLocalHighscores(FilePath.highscore);
	}
	
	public Map<Integer, List<Score>> loadLocalHighscores(String filePath) {
		JsonValue root = null;
		
		try {
			root = new JsonReader().parse(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			deleteAll();
			try {
				root = new JsonReader().parse(new FileReader(filePath));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		for(int levelIndex = 0; levelIndex < GameProperties.IMPLEMENTED_LEVEL.length; levelIndex++) {
			highscoreMap.put(levelIndex, new LinkedList<Score>());
			if(root.get(levelIndex) != null)
				for(JsonValue s : root.get(levelIndex)) {
					Score score = new Score(levelIndex, s.getString("name"), s.getFloat("time"));
					highscoreMap.get(levelIndex).add(score);
				}
		}
		
		return highscoreMap;
	}
	
	public void saveHighscoreList() {
		
		sortAll();
		JsonValue root = new JsonValue(ValueType.array);
		root.child = new JsonValue(ValueType.array);
		
		JsonValue array = root.child;
		for(int i = 1; i < highscoreMap.size(); i++) {
			array.next = new JsonValue(ValueType.array);
			array = array.next; 
		}

		for(int i = 0; i < highscoreMap.size(); i++) {
			
			List<Score> scoreList = highscoreMap.get(i);
			
			if(scoreList.size() <= 0)
				continue;
			
			JsonValue jScore = new JsonValue(ValueType.object);
			root.get(i).child = jScore;
			
			for(int s = 0; s < scoreList.size(); s++) {
				
				jScore.child = new JsonValue(ValueType.stringValue);
				jScore.child.name = "name";
				jScore.child.set(scoreList.get(s).PLAYER_NAME);
				
				jScore.child.next = new JsonValue(ValueType.doubleValue);
				jScore.child.next.name = "time";
				jScore.child.next.set(scoreList.get(s).TIME);

				if(s+1 < scoreList.size()) {
					jScore.next = new JsonValue(ValueType.object);
					jScore = jScore.next;
				}
			}
		}
	
		try {
			FileWriter f = new FileWriter(FilePath.highscore);
			f.write(root.toString());
			f.close();
		} catch (IOException e1) {
			return;
		}
	}
	
	private void sortAll() {
		for(int levelIndex : highscoreMap.keySet())
			sortLevel(levelIndex);
	}
	
	private void sortLevel(int level) {
		Collections.sort(highscoreMap.get(level));
	}
	
	public List<Score> getHighscoreList(int levelIndex) {
		for(Integer index : highscoreMap.keySet())
			if(index == levelIndex)
				return highscoreMap.get(index);
		return null;
	}
	
	public int getPosition(Score score) {
		addHighscore(score);
		int i = 0;
		for(Score s : getHighscoreList(score.LEVEL_INDEX)) {
			if(s.equals(score))
				break;
			i++;
		}
		return i+1;
	}
	
	public boolean addHighscore(Score score) {
		if(!highscoreMap.containsKey(score.LEVEL_INDEX)) {
			System.err.println(Highscore.class.toString()+" @addHighscore(...) : Illegal LevelIndex");
			return false;
		}
		
		boolean add = false;
		if(!highscoreMap.get(score.LEVEL_INDEX).contains(score))
			add = highscoreMap.get(score.LEVEL_INDEX).add(score);
		
		sortLevel(score.LEVEL_INDEX);
		saveHighscoreList();
		return add;
	}
	
	public static void deleteAll() {
		FileWriter f;
		try {
			f = new FileWriter(FilePath.highscore);
			f.write("[]");
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
