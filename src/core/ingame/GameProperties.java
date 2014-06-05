package core.ingame;

import menu.MenuMain;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class GameProperties {

	public static int width = 1280;	//640
	public static int height = 720; //360

	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	public static float gamma = 1.0f;
	public static float brightness = 1.0f;
	
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
	
//	GAMESTATES
	
	public enum GameState {
		MENU(true),
		INGAME(false),
		INGAME_WIN(false), 
		INGAME_LOSE(false), 
		INGAME_PAUSE(false);
		
		private final boolean IN_MENU;
		GameState(boolean menu) {
			IN_MENU = menu;
		}
		
		private boolean isIngame() 	{ return !IN_MENU;	}
		private boolean isMenu() 	{ return IN_MENU;	}
	}
	
	public static void setGameState(GameState state) {
		setGameState(state, 1);
	}
	
	public static void setGameState(GameState state, int level) {

		final GameState prevState = gameState;
		gameState = state;
		
		if(Gdx.graphics == null 
				|| (isInMenu() && prevState.isMenu()) 
				|| (isInGame() && prevState.isIngame())) {
			return;
		}

		width = state.isMenu() ? 1280 : 640;
		height = state.isMenu() ? 720 : 360; 
		
		Gdx.graphics.setDisplayMode(width, height, Gdx.graphics.isFullscreen());
		Debug.println(width + "x" + height, Mode.CONSOLE);
		
		if(isInMenu())	((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		else			((Game) Gdx.app.getApplicationListener()).setScreen(new GameRender(level));
			
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
	
	public static boolean isInMenu() { return gameState.equals(GameState.MENU);	}
	public static boolean isInGame() { return !isInMenu();						}

	public static GameState getGameState() {
		return gameState;
	}

}
