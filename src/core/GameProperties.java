package core;

import gameObject.interaction.enemy.Alarm;
import gameWorld.GameWorld;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.exception.LevelNotFoundException;
import core.ingame.GameRender;
import core.menu.EnterNameScreen;
import core.menu.MenuHighscore;
import core.menu.MenuLevelSelect;
import core.menu.MenuMain;
import core.menu.MenuOption;
import core.menu.MenuProfile;
import core.menu.Splash;

public class GameProperties {

	public static final int SCALE_WIDTH = 640;
	public static final int SCALE_HEIGHT = 400;

	public static GameState gameState = GameState.NORMAL;
	public static GameScreen gameScreen;

	public static DisplayMode prefDisplayMode;
	public static float brightness = 0.0f;
	public static float contrast = 1.0f;
	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	
	public static String loseMessage = "";
	
	public static final String[] IMPLEMENTED_LEVEL = {"Slums", "City", "Undergrounds"};
	private static final String INITIAL_NAME = "NewPlayer";
	public static final int 
		INITIAL_SHURIKENS = 10,
		INITIAL_HOOK_RADIUS = 300,
		INITIAL_STYLEPOINTS = 125,

		MAX_NAME_LENGTH = 12,
		MAX_PROFILE_COUNT = 5,
		MAX_HOOK_RADIUS = 500,
		MAX_SCOREPOSITION_TO_SERVER = 50,
		
		POINTS_DISPOSED_MUL = 30,
		POINTS_HIDDEN_MUL = 10,
		POINTS_ALARM_MUL = -5,
		POINTS_UNSEEN = 300,
		POINTS_WITHOUT_SHURIKENS = 300,
		
		PRICE_SHURIKEN = 50,
		PRICE_HOOK = 25;
	
	public static boolean debug = false,
			offline = false,
			deleteUserFiles = false,
			fixedWorldStep = false,
			lowQuality = false;
	
	public static String getInitialName() {
		return INITIAL_NAME + (new Random().nextInt(999-100) + 100);
	}
	
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

		} catch (Exception e) {
			saveSettings();
			return;
		}
	}
	
	public static void saveSettings() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.settings));

			root.get("brightness").set(brightness);
			root.get("contrast").set(contrast);
			root.get("musicVolume").set(musicVolume);
			root.get("soundVolume").set(soundVolume);

			FileHandle file = Gdx.files.local(FilePath.settings);
			file.writeString(root.toString(), false);
		} catch (Exception e) {
			resetSettings();
			return;
		}
	}

	public static void resetSettings() {
		FileWriter f;
		try {
			f = new FileWriter(FilePath.settings);
			f.write(new Json().prettyPrint(String.format("{keymap: { left: %d, right: %d, run: %d, jump: %d, crouch: %d, action: %d},"
					+ "brightness: %s,contrast: %s,musicVolume: %s,soundVolume: %s}",
					Keys.A, Keys.D, Keys.F, Keys.SPACE, Keys.S, Keys.E,
					Float.toString(brightness), Float.toString(contrast), Float.toString(musicVolume), Float.toString(soundVolume))));
			f.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	//	RANK, EXPERIENCE
	public enum Rank {
		//Noob(0), Rookie(1000), Tryhard(10000), Expert(100000);
		Novice(0), Scout(4000), Spy(8000), Saboteur(12000), Assassin(16000);

		final private int EXPERIENCE;

		private Rank(int points) {
			this.EXPERIENCE = points;
		}

		public static Rank getRank(int expPoints) {
			Rank rank = Novice;
			for (Rank r : Rank.values())
				if (expPoints > r.EXPERIENCE && r.EXPERIENCE > rank.EXPERIENCE) rank = r;
			return rank;
		}
	}

	public static int calcStylePoints(GameWorld world) {
		int earnedPoints = world.getPlayer().getEnemiesHidden()*GameProperties.POINTS_DISPOSED_MUL 
				+ world.getPlayer().getUnseenFrom() * GameProperties.POINTS_HIDDEN_MUL 
				+ (int)(world.getTimeLimit()-world.getTime())
				+ (int)Alarm.getTotalAlarmTime()*GameProperties.POINTS_ALARM_MUL;
		
		if(Alarm.getTotalAlarmTime() <= 0)
			earnedPoints += GameProperties.POINTS_UNSEEN;
		if(world.getPlayer().getShurikenThrown() == 0)
			earnedPoints += GameProperties.POINTS_WITHOUT_SHURIKENS;
		return Math.max(earnedPoints, 0);
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
		
		public GameScreen getNext() {
			switch(this) {
			case LEVEL1:
				return GameScreen.LEVEL2;
			case LEVEL2:
				return GameScreen.LEVEL3;
			case LEVEL3:
				return GameScreen.MENU_LEVELSELECT;
			default:
				return GameScreen.MENU_MAIN;
			}
		}
	}
	
	public static GameState getGameState() {
		return gameState;
	}
	
	public static void switchGameState(GameState state) {
		gameState = state;
		Gdx.app.postRunnable(new AudioManager.AudioStarter(state));
	}
	
	public static boolean switchGameScreen(GameScreen screen) throws LevelNotFoundException {
		if(Gdx.graphics == null)
			return false;
		
		Screen nextScreen;
		gameState = GameState.NORMAL;
	
		if(PlayerProfile.getProfileCount() == 0 && !screen.equals(GameScreen.MENU_SPLASH))
			nextScreen = new EnterNameScreen(screen);
		else {
			switch(screen) {
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
		}

		refreshDisplayMode();
		((Game) Gdx.app.getApplicationListener()).setScreen(nextScreen);
		Gdx.app.postRunnable(new AudioManager.AudioStarter(screen));

		gameScreen = screen;
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
		Gdx.app.postRunnable(new AudioManager.AudioStarter(gameState));
	}
	
	public static void setGameOver(String message) {
		if(gameState.equals(GameState.LOSE))
			return;
		loseMessage = message;
		gameState = GameState.LOSE;
		Gdx.app.postRunnable(new AudioManager.AudioStarter(GameState.LOSE));
	}

	public static void setWin() {
		gameState = GameState.WIN;
		Gdx.app.postRunnable(new AudioManager.AudioStarter(GameState.WIN));
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
		Highscore.deleteAll();
		PlayerProfile.deletePlayerProfiles();
		brightness = 0.0f;
		contrast = musicVolume = soundVolume = 1.0f;
		resetSettings();
	}

	public static class GameScreenSwitcher implements Runnable {
		private GameScreen screen;
		private Screen gdxScreen;
		public GameScreenSwitcher(GameScreen screen) {
			this.screen = screen;
		}

		public GameScreenSwitcher(Screen gdxScreen) {
			this.gdxScreen = gdxScreen;
		}
	
		public void run() {
			if(screen != null)
				GameProperties.switchGameScreen(screen);
			else if(gdxScreen != null) {
				((Game) Gdx.app.getApplicationListener()).setScreen(gdxScreen);
			}
		}
	}

}
