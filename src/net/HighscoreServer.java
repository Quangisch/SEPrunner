package net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.JsonWriter;

import core.FilePath;
import core.GameProperties;
import core.Highscore;
import core.Highscore.Score;

public class HighscoreServer {

	private final static String addr = "seprunner.pascal-reintjens.com";

	public boolean isConnected() {
		try {
			InetAddress.getByName(addr);
			if(GameProperties.offline)
				System.out.println("Offline Mode");
			if(GameProperties.debug)
				System.out.println("Debug Mode - Server Connection Denied");
			return !GameProperties.offline && !GameProperties.debug;//.isReachable(500);
		} catch (UnknownHostException e) {} // catch (IOException e) {
											// }
		return false;
	}

	public void addHighScore(int level, String name, float score) {
		JsonValue root;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.highscoreTemp));
		} catch (FileNotFoundException e) {
			try {
				FileWriter f = new FileWriter(FilePath.highscoreTemp);
				f.write("[]");
				f.close();
				root = new JsonReader().parse(new FileReader(FilePath.highscoreTemp));
			} catch (IOException e1) {
				return;
			}
		}

		if (root == null) root = new JsonValue(false);
		root.setType(ValueType.array);

		JsonValue entry = new JsonValue(false);
		entry.setType(ValueType.array);
		entry.child = new JsonValue(level);
		entry.child.next = new JsonValue(name);
		entry.child.next.next = new JsonValue(score);

		if (root.child == null)
			root.child = entry;
		else {
			JsonValue last = root.child;
			while (last.next() != null)
				last = last.next();
			last.next = entry;
		}

		try {
			FileWriter f = new FileWriter(FilePath.highscoreTemp);
			JsonWriter j = new JsonWriter(f);
			j.array();
			for (JsonValue p = root.child; p != null; p = p.next) {
				j.array();
				j.value(p.getInt(0));
				j.value(p.getString(1));
				j.value(p.getFloat(2));
				j.pop();
			}

			j.close();
			f.close();
		} catch (IOException e) {
			return;
		}
	}

	public boolean uploadScore(Score score) {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL( //
					"http://" + addr + "/Eintragen.php?Index=" + score.LEVEL_INDEX //
							+ "&Name=" + score.PLAYER_NAME //
							+ "&Score=" + score.TIME //
			).openConnection();
			c.setRequestMethod("GET");
			c.setConnectTimeout(1000);
			c.setReadTimeout(5000);
			if (c.getInputStream().read() != '1') 
				return true;
		} catch (IOException e) {
		}
		
		return false;
	}
	
	public void tryUpload() {
		JsonValue root;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.highscoreTemp));
		} catch (FileNotFoundException e) {
			return;
		}

		if (!isConnected()) return;

		for (JsonValue p = root.child; p != null; p = p.next) {
			try {
				HttpURLConnection c = (HttpURLConnection) new URL( //
						"http://" + addr + "/Eintragen.php?Index=" + p.getInt(0) //
								+ "&Name=" + p.getString(1) //
								+ "&Score=" + p.getFloat(2) //
				).openConnection();
				c.setRequestMethod("GET");
				c.setConnectTimeout(1000);
				c.setReadTimeout(5000);
				if (c.getInputStream().read() != '1') return;
			} catch (IOException e) {
				return;
			}
		}

		try {
			FileWriter f = new FileWriter(FilePath.highscoreTemp);
			f.close();
			File fl = new File(FilePath.highscoreTemp);
			fl.delete();
		} catch (IOException e) {
			return;
		}
	}

	public void updateLocalHighscoreFile() {
		if(!isConnected())
			return;
		
		try {
			URL website = new URL("http://"+addr+"/Auslesen.php");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(FilePath.highscoreServer);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();

		} catch (IOException e) {		
			return;
		}

		Map<Integer, List<Score>> scoreMap = Highscore.getInstance().loadLocalHighscores(FilePath.highscoreServer);
		for(Entry<Integer, List<Score>> l : scoreMap.entrySet())
			for(Score s : l.getValue())
				Highscore.getInstance().addHighscore(s);
		
		new File(FilePath.highscoreServer).delete();
		
	}
}
