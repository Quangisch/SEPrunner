package core;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.exception.LevelNotFoundException;
import core.ingame.GameRender;
import core.menu.MenuMain;

public class GameProperties {
	
	public static final int SCALE_WIDTH = 640;
	public static final int SCALE_HEIGHT = 360;
	
	public static final int SIZE_WIDTH = 1280, SIZE_HEIGHT = 800;
	private static GameState gameState = null;

	public static float brightness = 0.0f;
	public static float contrast = 1.0f;	
	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	
	
	public static final int IMPLEMENTED_LEVEL = 3;
	public static final int HOOK_RADIUS_MAX = 500;
	public static final int MAX_PROFILE_COUNT = 5;
	
//	CONVERSION
	final public static float PIXELPROMETER = 100;

	public static float meterToPixel(float meter) {
		return meter * PIXELPROMETER;
	}

	public static float pixelToMeter(float pixel) {
		return pixel / PIXELPROMETER;
	}
	
	public static Vector2 meterToPixel(Vector2 meter) {
		return new Vector2(meterToPixel(meter.x), meterToPixel(meter.y));
	}
	
	public static Vector2 pixelToMeter(Vector2 pixel) {
		return new Vector2(pixelToMeter(pixel.x), pixelToMeter(pixel.y));
	}
	
	public static void initFromFile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.settings));
			
			brightness = root.getFloat("brightness");
			contrast = root.getFloat("contrast");
			musicVolume = root.getFloat("musicVolume");
			soundVolume = root.getFloat("soundVolume");
			
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			System.err.println("settings.json not found");
			return;
		} catch (NullPointerException e) {
			e.printStackTrace();
			brightness = contrast = musicVolume = soundVolume = 1;
			return;
		}
	}
	
	public static void saveToFile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.settings));
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			System.out.println("settings.json not found");
			return;
		}

		root.get("brightness").set(brightness);
		root.get("contrast").set(contrast);
		root.get("soundVolume").set(soundVolume);
		root.get("musicVolume").set(musicVolume);
		
		FileHandle file = Gdx.files.local(FilePath.settings);
		file.writeString(root.toString(), false);
		
	}
	
//	RANK, EXPERIENCE
	public enum Rank {
		Noob(0),
		Rookie(30),
		Expert(100);
		
		final private int EXPERIENCE;
		private Rank(int points) {
			this.EXPERIENCE = points;
		}
		
		public static Rank getRank(int expPoints) {
			Rank rank = Noob;
			for(Rank r : Rank.values())
				if(expPoints > r.EXPERIENCE && r.EXPERIENCE > rank.EXPERIENCE)
					rank = r;
			return rank;
		}
	}
	
	public static final int SHU_MUL = -1;
	public static final int DISPOSED_MUL = 10;
	public static final int HIDDEN_MUL = 20;
	
	public static int calcStylePoints(int shurikenThrown, int disposedBodies, int hiddenFrom) {
		return Math.max(0, shurikenThrown * SHU_MUL + disposedBodies * DISPOSED_MUL + hiddenFrom * HIDDEN_MUL);
	}
	
	
//	GAMESTATES
	public enum GameState {
		MENU(true),
		INGAME(false),
		INGAME_WIN(false), 
		INGAME_LOSE(false), 
		INGAME_PAUSE(false);
		
		private final boolean inMenu;
		GameState(boolean menu) {
			inMenu = menu;
		}
		
		private boolean isInGame() 	{ return !inMenu;	}
		private boolean isMenu() 	{ return inMenu;	}
	}
	
	public static void setGameState(GameState state) {
		setGameState(state, -1);
	}
	
	public static void setGameState(GameState state, int level) throws LevelNotFoundException {
		final GameState prevState = gameState;
		gameState = state;
	
		if(prevState == null || (Gdx.graphics == null && (isInMenuState() && prevState.isMenu()) 
				|| (isInGameState() && prevState.isInGame())))
			return;
		Gdx.graphics.setDisplayMode(SCALE_WIDTH, SCALE_HEIGHT, Gdx.graphics.isFullscreen());
		if(isInMenuState())	((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		else				((Game) Gdx.app.getApplicationListener()).setScreen(new GameRender(level));
		
		if(!gameState.equals(prevState))
			ResourceManager.getInstance().startMusic();
			
	}
	
	public static void toogleFullScreen() {
		if (Gdx.graphics.isFullscreen())
			Gdx.graphics.setDisplayMode(GameProperties.SCALE_WIDTH, 
					GameProperties.SCALE_HEIGHT, false);
		else
			Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
					Gdx.graphics.getDesktopDisplayMode().height, true);
	}
	
	public static void toogleIngamePause() {
		if(gameState.equals(GameState.INGAME))
			gameState = GameState.INGAME_PAUSE;
		else if(gameState.equals(GameState.INGAME_PAUSE))
			gameState = GameState.INGAME;
	}
	
	public static void setGameOver() {
		gameState = GameState.INGAME_LOSE;
	}
	
	public static void setWin() {
		gameState = GameState.INGAME_WIN;
	}
	
	public static boolean isGameState(GameState state) {
		return gameState.equals(state);
	}
	
	public static boolean isInMenuState() { return gameState.equals(GameState.MENU);	}
	public static boolean isInGameState() { return !isInMenuState();						}

	public static GameState getGameState() {
		return gameState;
	}

}
