package core.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public abstract class GameProperties {

	public static boolean menu = false;
	public static boolean ingame = false;
	
	public static int width = 1280;	//640
	public static int height = 720; //360

	public static float musicVolume = 1.0f;
	public static float soundVolume = 1.0f;
	public static float gamma = 1.0f;
	public static float brightness = 1.0f;

	public static boolean debugMode = false;
	
	public static int[] keyLeft = {Keys.A, Keys.LEFT}, 
			keyRight = {Keys.D, Keys.RIGHT},
			keyRun = {Keys.F, Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT},
			keyJump = {Keys.SPACE, Keys.UP}, 
			keyCower = {Keys.C, Keys.DOWN}, 
			keyAction = {Keys.E, Keys.ENTER};

	final public static float PIXELPROMETER = 100;

	public static float meterToPixel(float meter) {
		return meter * PIXELPROMETER;
	}

	public static float pixelToMeter(float pixel) {
		return pixel / PIXELPROMETER;
	}
	
	public static void switchMode(boolean menu, boolean ingame) {
		GameProperties.menu = menu;
		GameProperties.ingame = ingame;
		
		width = menu ? 1280 : 640;	//640
		height = menu ? 720 : 360; 	//360
		
		if(Gdx.graphics == null)
			return;
		
		Gdx.graphics.setDisplayMode(width, height, Gdx.graphics.isFullscreen());
		
		if(debugMode)
			System.out.println(width+"x"+height);
		
		if(!menu && !ingame)
			Gdx.app.exit();
	}

}
