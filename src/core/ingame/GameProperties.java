package core.ingame;

import menu.MenuMain;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
		Debug.println(width + "x" + height, Mode.CONSOLE);
		
		if(menu && !ingame)
			((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
		else if(!menu && ingame)
			((Game) Gdx.app.getApplicationListener()).setScreen(new GameRender(1));
		
		if(!menu && !ingame)
			Gdx.app.exit();
	}

}
