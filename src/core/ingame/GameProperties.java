package core.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public abstract class GameProperties {

	public static boolean menu = false;
	public static boolean ingame = false;
	
	public static int width = 1280;	//640
	public static int height = 720; //360

	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	public static float gamma = 1.0f;
	public static float brightness = 1.0f;

//	0: off, 1: boxrenderer, 2: geometricObject, 3: console
	public static Debug debugMode = Debug.OFF;
	
	public enum Debug {
		OFF,
		BOXRENDERER,
		GEOMETRIC,
		CONSOLE;
		
		public static Debug toNext() {
			switch(debugMode) {
			case BOXRENDERER: 
				return debugMode = Debug.GEOMETRIC;
			case CONSOLE: 	
				return debugMode = Debug.OFF;
			case GEOMETRIC: 	
				return debugMode = Debug.CONSOLE;
			case OFF: 			
				return debugMode = Debug.BOXRENDERER;
			default:			
				return debugMode = Debug.OFF;	
			}
		}
	}
	
	public static int[] keyLeft = {Keys.A, Keys.LEFT}, 
			keyRight = {Keys.D, Keys.RIGHT},
			keyRun = {Keys.F, Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT},
			
			keyJump = {Keys.SPACE, Keys.UP}, 
			keyCrouch = {Keys.S, Keys.DOWN},
			
			keyAction = {Keys.E, Keys.ENTER}, //hide, dispose, grab - abhängig von den Umgebung
			keyThrow = {Input.Buttons.LEFT},
			keyHook = {Input.Buttons.RIGHT};
		
	

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
	
	public static void switchMode(boolean menu, boolean ingame) {
		GameProperties.menu = menu;
		GameProperties.ingame = ingame;
		
		width = menu ? 1280 : 640;	//640
		height = menu ? 720 : 360; 	//360
		
		if(Gdx.graphics == null)
			return;
		
		Gdx.graphics.setDisplayMode(width, height, Gdx.graphics.isFullscreen());
		
		if(debugMode.equals(Debug.CONSOLE))
			System.out.println(width+"x"+height);
		
		if(!menu && !ingame)
			Gdx.app.exit();
	}

}
