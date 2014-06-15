package core;

import java.io.FileNotFoundException;
import java.io.FileReader;

import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.GameRender;
import core.menu.MenuMain;

public class GameProperties {
	
	public static final int SCALE_WIDTH = 640;
	public static final int SCALE_HEIGHT = 480;
	
	public static DisplayMode displayMode;

	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	public static float contrast = 1.0f;
	public static float brightness = 0.7f;
	
	private static GameState gameState = GameState.MENU;
	
	
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
			e.printStackTrace();
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
			e.printStackTrace();
			return;
		}

		root.get("brightness").set(brightness);
		root.get("contrast").set(contrast);
		root.get("soundVolume").set(soundVolume);
		root.get("musicVolume").set(musicVolume);
		
		FileHandle file = Gdx.files.local(FilePath.settings);
		file.writeString(root.toString(), false);
		
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
		setGameState(state, 1);
	}
	
	public static void setGameState(GameState state, int level) {

		final GameState prevState = gameState;
		gameState = state;
		if(!gameState.equals(prevState))
			ResourceManager.getInstance().startMusic();
		
		if(Gdx.graphics == null && (isInMenuState() && prevState.isMenu()) 
				|| (isInGameState() && prevState.isInGame()))
			return;
		
		if(displayMode == null)
			displayMode = Gdx.graphics.getDesktopDisplayMode();
		
		Gdx.graphics.setDisplayMode(SCALE_WIDTH, SCALE_HEIGHT, Gdx.graphics.isFullscreen());
		Debug.println(displayMode.width + "x" + displayMode.height, Mode.CONSOLE);

		
		if(isInMenuState())	((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		else			((Game) Gdx.app.getApplicationListener()).setScreen(new GameRender(level));
			
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
