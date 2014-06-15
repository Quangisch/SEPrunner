package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;


public class Highscore {
	
	private static Highscore highscore;
	private Map<Level, List<Score>> highscoreList;
	
	private Highscore() {
		highscoreList = new HashMap<Level, List<Score>>();
	}
	
	public boolean loadHighscoreList() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.highscore));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(root == null)
			return false;
		
		for(int i = 0; i < root.size; i++) {
			final Level level = new Level(i+1, root.get(i).getString("name"));
			if(!highscoreList.containsKey(level))
				highscoreList.put(level, new LinkedList<Score>());
			for(JsonValue s : root.get(i).get("highscore")) {
				Score score = new Score(level, s.getString("name"), s.getFloat("time"));
				highscoreList.get(level).add(score);
			}
		}
		
		return true;
	}
	
//	TODO
	public void saveHighscoreList() {
		sortAll();
//		JsonValue root = null;
//		try {
//			root = new JsonReader().parse(new FileReader(FilePath.highscore));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}

	}
	
	private void sortAll() {
		for(Level level : highscoreList.keySet())
			sortLevel(level.ID);
	}
	
	private void sortLevel(int level) {
		Collections.sort(highscoreList.get(level));
	}
	
	public List<Score> getHighscoreList(int levelID) {
		for(Level l : highscoreList.keySet())
			if(l.ID == levelID)
				return highscoreList.get(l);
		return null;
	}
	
	public static Highscore getInstance() {
		if(highscore == null)
			highscore = new Highscore();
		return highscore;
	}
	
	public class Score implements Comparable <Score> {
		final public String PLAYER_NAME;
		final public float TIME;
		final public String TIME_STRING;
		final public Level LEVEL;
		
		public Score(Level level, String name, float time) {
			this.PLAYER_NAME = name;
			this.TIME = time;
			this.LEVEL = level;
			
			TIME_STRING = getTimeAsString(time);
			
		}
		
//		TODO dirty
		public String getTimeAsString(float time) {
			String min = String.valueOf((int) time / 60);
			min = min.length() == 1 ? "0"+min : min.length() == 0 ? "00" : min.length() > 2 ? "99" : min;
			
			String sec = String.valueOf((int) time % 60);
			sec = sec.length() == 1 ? "0"+sec : sec.length() == 0 ? "00" : sec.length() > 2 ? "99" : sec;
			
			DecimalFormat df = new DecimalFormat(".00");
			String milli = df.format(time);
			milli = milli.substring(milli.length() - 2);
			
			return min+":"+sec+":"+milli;
		}

		@Override
		public int compareTo(Score other) {
			return (int)(TIME - other.TIME);
		}
	}
	
	public class Level {
		final public int ID;
		final public String NAME;
		
		private Level(int id, String name) {
			this.ID = id;
			this.NAME = name;
		}
		
		public boolean equals(Object object) {
			if(object == null || !(object instanceof Level))
				return false;
			return ((Level) object).ID == this.ID;
		}
	}

}
