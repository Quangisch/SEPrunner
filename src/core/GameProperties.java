package core;

import gameObject.interaction.enemy.Alarm;

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
	private static Screen gdxScreen;

	public static DisplayMode prefDisplayMode;
	public static float brightness = 0.0f;
	public static float contrast = 1.0f;
	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	
	public static String loseMessage = "";
	
	public static final String[] IMPLEMENTED_LEVEL = {"Slums", "City", "Undergrounds"};
	
	private static final String INITIAL_NAME = "NewPlayer";
	public static final int INITIAL_SHURIKENS = 10;
	public static final int INITIAL_HOOK_RADIUS = 300;
	public static final int INITIAL_STYLEPOINTS = 20;


	public static final int MAX_NAME_LENGTH = 12;
	public static final int MAX_PROFILE_COUNT = 5;
	public static final int MAX_HOOK_RADIUS = 500;
	public static final int MAX_SCOREPOSITION_TO_SERVER = 50;
	
	public static boolean uploadScore = true;
	
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
			saveToFile();
			return;
		}
	}

	public static void saveToFile() {
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


	public static final int P_WITHOUT_SHURIKENS = 100;
	public static final int P_DISPOSED_MUL = 5;
	public static final int P_HIDDEN_MUL = 5;
	public static final int P_LEVEL_COMPLETE = 50;
	public static final int P_ALARM_MUL = -5;
	public static final int P_UNSEEN = 50;

//	TODO
	public static int calcStylePoints(int shurikenThrown, int disposedBodies, int hiddenFrom) {
//		System.out.println(String.format(
//				  "Disposed Enemies : %d * %d = %d\n"
//				+ "Hidden from Enemy: %d * %d = %d\n"
//				+ (Alarm.getTotalAlarmTime() > 0 
//				? String.format("Total Alarm time : %f * %d = %d\n", Alarm.getTotalAlarmTime(), P_ALARM_MUL, (int)(Alarm.getTotalAlarmTime()*P_ALARM_MUL))
//				: String.format("Unseen Bonus     : %d\n",  P_UNSEEN))
//				+ (shurikenThrown == 0
//				? "Unharmed Enemies : %d\n" : "\n")
//				+ "Level Completed  : %d\n"
//				+ "=================\n"
//				+ "TOTAL: %d\n"
//				+ "=================\n",
//				disposedBodies, P_DISPOSED_MUL, disposedBodies*P_DISPOSED_MUL,
//				hiddenFrom, P_HIDDEN_MUL, hiddenFrom * P_HIDDEN_MUL,
//				P_WITHOUT_SHURIKENS, P_LEVEL_COMPLETE));
//				(int)(disposedBodies*P_DISPOSED_MUL + hiddenFrom*P_HIDDEN_MUL + P_LEVEL_COMPLETE +
//				shurikenThrown == 0 ? P_WITHOUT_SHURIKENS : 0 + Alarm.getTotalAlarmTime() == 0 ? P_UNSEEN : 0)));
		
		return Math.max(P_LEVEL_COMPLETE, shurikenThrown == 0 ? P_WITHOUT_SHURIKENS : 0 //
				+ disposedBodies * P_DISPOSED_MUL //
				+ hiddenFrom * P_HIDDEN_MUL //
				+ P_LEVEL_COMPLETE //
				+ Alarm.getTotalAlarmTime() == 0 ? P_UNSEEN : (int) (Alarm.getTotalAlarmTime() * P_ALARM_MUL)
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
		AudioManager.getInstance().startMusic(state);
	}
	
	public static boolean switchGameScreen(GameScreen screen) throws LevelNotFoundException {
		if(Gdx.graphics == null)
			return false;
		
		Screen nextScreen;
		gameState = GameState.NORMAL;
		
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

		refreshDisplayMode();

		if(PlayerProfile.getProfileCount() == 0 && !nextScreen.equals(GameScreen.MENU_SPLASH))
			nextScreen = new EnterNameScreen(nextScreen);
		if(GameProperties.gameScreen != null && GameProperties.gameScreen.INDEX >= 0)
			gdxScreen.dispose();
		gdxScreen = nextScreen;
		((Game) Gdx.app.getApplicationListener()).setScreen(nextScreen);
		AudioManager.getInstance().startMusic(screen);

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
		AudioManager.getInstance().startMusic(gameState);
	}
	
	public static void setGameOver(String message) {
		if(gameState.equals(GameState.LOSE))
			return;
		loseMessage = message;
		gameState = GameState.LOSE;
		AudioManager.getInstance().startMusic(GameState.LOSE);
	}

	public static void setWin() {
		gameState = GameState.WIN;
		AudioManager.getInstance().startMusic(GameState.WIN);
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
		saveToFile();
	}
	

	public static class GameScreenSwitcher implements Runnable {
		private GameScreen screen;
		private Screen gdxScreen;
		private boolean disposeNewScreen = true;
		public GameScreenSwitcher(GameScreen screen) {
			this.screen = screen;
		}

		public GameScreenSwitcher(Screen gdxScreen) {
			this.gdxScreen = gdxScreen;
		}
		
		public GameScreenSwitcher(Screen gdxScreen, boolean disposeNewScreen) {
			this.gdxScreen = gdxScreen;
			this.disposeNewScreen = disposeNewScreen;
		}
		
		public void run() {
			if(screen != null)
				GameProperties.switchGameScreen(screen);
			else if(gdxScreen != null) {
				if(GameProperties.gameScreen != null && GameProperties.gameScreen.INDEX >= 0 && disposeNewScreen)
					gdxScreen.dispose();
				((Game) Gdx.app.getApplicationListener()).setScreen(gdxScreen);
			}
		}
	}

}
