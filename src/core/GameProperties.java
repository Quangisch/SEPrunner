package core;

import gameObject.interaction.enemy.Alarm;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.exception.LevelNotFoundException;
import core.ingame.GameRender;
import core.menu.MenuHighscore;
import core.menu.MenuLevelSelect;
import core.menu.MenuMain;
import core.menu.MenuOption;
import core.menu.MenuProfile;
import core.menu.Splash;

public class GameProperties {

	public static final int SCALE_WIDTH = 640;
	public static final int SCALE_HEIGHT = 360;

	public static GameState gameState = GameState.NORMAL;
	public static GameScreen gameScreen;


	public static DisplayMode prefDisplayMode;
	public static float brightness = 0.0f;
	public static float contrast = 1.0f;
	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	
	public static String loseMessage = "";
	
	public static final String[] IMPLEMENTED_LEVEL = {"Slums", "City", "Undergrounds"};
	public static final int HOOK_RADIUS_MIN = 300;
	public static final int HOOK_RADIUS_MAX = 500;
	public static final int MAX_PROFILE_COUNT = 5;
	public static final int INITIAL_STYLEPOINTS = 20;
	
	
	
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
		Noob(0), Rookie(30), Expert(100);

		final private int EXPERIENCE;

		private Rank(int points) {
			this.EXPERIENCE = points;
		}

		public static Rank getRank(int expPoints) {
			Rank rank = Noob;
			for (Rank r : Rank.values())
				if (expPoints > r.EXPERIENCE && r.EXPERIENCE > rank.EXPERIENCE) rank = r;
			return rank;
		}
	}


	public static final int WITHOUT_SHURIKENS = 100;
	public static final int DISPOSED_MUL = 5;
	public static final int HIDDEN_MUL = 5;
	public static final int LEVEL_COMPLETE = 50;
	public static final int ALARM_MUL = -5;
	public static final int UNSEEN = 50;

	public static int calcStylePoints(int shurikenThrown, int disposedBodies, int hiddenFrom) {
		System.out.println(String.format(
				  "Disposed Enemies : %d * %d = %d\n"
				+ "Hidden from Enemy: %d * %d = %d\n"
				+ (Alarm.getTotalAlarmTime() <= 0 
				? "Total Alarm time : %.2f * %d = %d\n"
				: "Unseen Bonus     : %d\n")
				+ (shurikenThrown == 0
				? "Unharmed Enemies : %d" : ""
				+ "Level Completed  : %d\n"
				+ "=================\n"
				+ "TOTAL: %d"
				+ "=================\n"),
				disposedBodies, DISPOSED_MUL, disposedBodies*DISPOSED_MUL,
				hiddenFrom, HIDDEN_MUL, hiddenFrom * HIDDEN_MUL,
				Alarm.getTotalAlarmTime(), ALARM_MUL, (int)(Alarm.getTotalAlarmTime()*ALARM_MUL), UNSEEN, 
				WITHOUT_SHURIKENS, LEVEL_COMPLETE, 
				disposedBodies*DISPOSED_MUL+hiddenFrom * HIDDEN_MUL+Alarm.getTotalAlarmTime()*ALARM_MUL+LEVEL_COMPLETE+
				shurikenThrown == 0 ? WITHOUT_SHURIKENS : 0 + Alarm.getTotalAlarmTime() == 0 ? UNSEEN : 0));
		return Math.max(LEVEL_COMPLETE, shurikenThrown == 0 ? WITHOUT_SHURIKENS : 0 //
				+ disposedBodies * DISPOSED_MUL //
				+ hiddenFrom * HIDDEN_MUL //
				+ LEVEL_COMPLETE //
				+ Alarm.getTotalAlarmTime() == 0 ? UNSEEN : (int) (Alarm.getTotalAlarmTime() * ALARM_MUL)
		);
	}

	
//	GAMESTATES
	public static enum GameState {
		NORMAL,
		WIN,
		LOSE,
		PAUSE
	}
	
	public static enum GameScreen {
		MENU_MAIN(-1),
		MENU_LEVELSELECT(-2),
		MENU_PROFILE(-3),
		MENU_OPTION(-4),
		MENU_HIGHSCORE(-5),
		MENU_SPLASH(-10),
		
		LEVEL1(0),
		LEVEL2(1),
		LEVEL3(2),
		
		MENU_BACKGROUND(-11) //for animatedMenuBackground
		;
		
		public final int INDEX;
		GameScreen(int index) {
			this.INDEX = index;
		}
		
		public static GameScreen getScreen(int index) {
			GameScreen screen = MENU_MAIN;
			for(GameScreen s : GameScreen.values())
				if(s.INDEX == index)
					screen = s;
			return screen;
		}
	}
	
	public static GameState getGameState() {
		return gameState;
	}
	
	public static void switchGameState(GameState state) {
		gameState = state;
		ResourceManager.getInstance().startMusic(state);
	}
	
	public static boolean switchGameScreen(GameScreen screen) throws LevelNotFoundException {
		if(Gdx.graphics == null)
			return false;
		
		gameScreen = screen;
		Screen nextScreen;
		gameState = GameState.NORMAL;
		
		switch(gameScreen) {
		case LEVEL1:
		case LEVEL2:
		case LEVEL3:
			nextScreen = new GameRender(screen);
			break;
			
		case MENU_HIGHSCORE:
			nextScreen = new MenuHighscore();
			break;
		case MENU_LEVELSELECT:
			nextScreen = new MenuLevelSelect();
			break;
		case MENU_OPTION:
			nextScreen = new MenuOption();
			break;
		case MENU_PROFILE:
			nextScreen = new MenuProfile();
			break;
		case MENU_SPLASH:
			nextScreen = new Splash();
			break;

		case MENU_BACKGROUND:
		case MENU_MAIN:
		default:
			nextScreen = new MenuMain();
//			throw new LevelNotFoundException();
			break;
		
		}

		refreshDisplayMode();
		((Game) Gdx.app.getApplicationListener()).setScreen(nextScreen);
		ResourceManager.getInstance().startMusic(screen);
		
		return true;

	}

	public static void toogleFullScreen() {
		if (Gdx.graphics.isFullscreen())
			Gdx.graphics.setDisplayMode(GameProperties.SCALE_WIDTH, GameProperties.SCALE_HEIGHT, false);
		else
			Gdx.graphics.setDisplayMode(prefDisplayMode.width,
					prefDisplayMode.height, true);
	}
	
	public static DisplayMode initPrefDisplayMode() {
		if(prefDisplayMode == null) {
			for(DisplayMode m : LwjglApplicationConfiguration.getDisplayModes()) {
				if(prefDisplayMode == null || (m.width < prefDisplayMode.width))
					prefDisplayMode = m;
			}
		}
		return prefDisplayMode;
	}
	
	public static void refreshDisplayMode() {
		int width = Gdx.graphics.isFullscreen() ? prefDisplayMode.width : GameProperties.SCALE_WIDTH;
		int height = Gdx.graphics.isFullscreen() ? prefDisplayMode.height : GameProperties.SCALE_HEIGHT;
		Gdx.graphics.setDisplayMode(width, height, Gdx.graphics.isFullscreen());
	}

	public static void toogleIngamePause() {

		if(gameState.equals(GameState.NORMAL))
			gameState = GameState.PAUSE;
		else if(gameState.equals(GameState.PAUSE))
			gameState = GameState.NORMAL;
		ResourceManager.getInstance().startMusic(gameState);
	}
	
	public static void setGameOver(String message) {
		if(gameState.equals(GameState.LOSE))
			return;
		loseMessage = message;
		gameState = GameState.LOSE;
		ResourceManager.getInstance().startMusic(GameState.LOSE);
	}

	public static void setWin() {
		gameState = GameState.WIN;
		ResourceManager.getInstance().startMusic(GameState.WIN);
	}
	
	public static boolean isCurrentGameState(GameState state) {
		return gameState.equals(state);
	}
	
	public static boolean isCurrentGameScreen(GameScreen screen) {
		return gameScreen.equals(screen);
	}
	
	public static boolean isInMenu() {
		return gameScreen.INDEX < 0;
	}
	
	public static boolean isIngame() {
		return !isInMenu();
	}
	
	
	public static void resetUserData() {
		TMP tmp = new TMP();
		new LwjglApplication(tmp);
		Highscore.deleteAll();
		PlayerProfile.deletePlayerProfiles();
		
		brightness = 0.0f;
		contrast = musicVolume = soundVolume = 1.0f;
		saveToFile();
		
		System.exit(0);
		
	}
	

	public static class GameScreenSwitcher implements Runnable {
		private GameScreen screen;
		public GameScreenSwitcher(GameScreen screen) {
			this.screen = screen;

		}

		public void run() {
			GameProperties.switchGameScreen(screen);
		}
	}
	
//	HACK
	private static class TMP extends Game {
		@Override
		public void create() {
			
		}
		
	}

}
