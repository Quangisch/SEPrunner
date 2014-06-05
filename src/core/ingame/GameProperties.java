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
		MENU,
		INGAME,
		INGAME_WIN, INGAME_LOSE, INGAME_PAUSE;
	}
	
	public static void setGameState(GameState state) {
		setGameState(state, 1);
	}
	
	public static void setGameState(GameState state, int level) {

		gameState = state;
//		TODO
		width = isInMenu() ? 1280 : 640;
		height = isInMenu() ? 720 : 360; 
		
		if(Gdx.graphics == null)
			return;
		
		Gdx.graphics.setDisplayMode(width, height, Gdx.graphics.isFullscreen());
		Debug.println(width + "x" + height, Mode.CONSOLE);
		
		if(isInMenu())	((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		else			((Game) Gdx.app.getApplicationListener()).setScreen(new GameRender(level));
			
	}
	
	public static boolean isGameState(GameState state) {
		return gameState.equals(state);
	}
	
	public static boolean isInMenu() { return gameState.equals(GameState.MENU);	}
	public static boolean isInGame() { return !isInMenu();						}

}
