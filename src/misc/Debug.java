package misc;

import gameObject.player.InputHandler;

import com.badlogic.gdx.Input.Keys;

import core.ingame.Camera;


public abstract class Debug {

	private static boolean on;
	public static boolean isOn() 		{ return on;	}
	public static void toogleOnOff() 	{ 
		on = !on;
		
		System.out.println("Debug "+ (on ? "on" : "off"));
		
		if(on)
			printCurrentMode();
	}
	
	private static Mode mode = Mode.BOXRENDERER;
	
	public enum Mode {
		BOXRENDERER(Keys.NUM_1),
		GEOMETRIC(Keys.NUM_2),
		CONSOLE(Keys.NUM_3),
		CAMERA(Keys.NUM_4);
		
		final private int key;
		
		Mode(int key) {
			this.key = key;
		}
	}
	
	public static boolean isMode(Mode mod) 	{
		if(!on)
			return false;
		return mode.equals(mod);	
	}
	public static Mode getMode() 				{ return mode;				}
	
	public static boolean setMode(int key) {
		for(Mode m : Mode.values())
			if(m.key == key) {
				endMode(mode);
				startMode(m);
				
				mode = m;
				printCurrentMode();
				
				return true;
			}
		return false;
	}
	
	private static void startMode(Mode mode) {
		switch(mode) {
		
		case BOXRENDERER:
			break;
		case CAMERA:
			initialZoom = Camera.getInstance().zoom;
			break;
		case CONSOLE:
			break;
		case GEOMETRIC:
			break;
		default:
			break;
		
		}
	}
	
	private static void endMode(Mode mode) {
		switch(mode) {
		
		case BOXRENDERER:
			break;
		case CAMERA:
			Camera.getInstance().zoom = initialZoom;
			break;
		case CONSOLE:
			break;
		case GEOMETRIC:
			break;
		default:
			break;
		
		}
	}
	
	private static void printCurrentMode() {
		System.out.println("Mode "+mode.toString());
	}
	
	private static final int CAM_UP = Keys.W,
			CAM_RIGHT = Keys.D,
			CAM_DOWN = Keys.S,
			CAM_LEFT = Keys.A,
			CAM_FAST = Keys.F,
			CAM_ZOOM_IN = Keys.E,
			CAM_ZOOM_OUT = Keys.Q;
	
	private static final float CAMSPEED_INITIAL = 5,
			CAMSPEED_UP = 2,
			CAMSPEED_ZOOM = 0.1f;
	private static float camSpeed = CAMSPEED_INITIAL;
	private static float zoom, initialZoom;
	private static float moveX, moveY;
	
	public static void processCamera(Camera cam) {
		
		System.out.println("controlCam");
		processCameraControll();
		
		float dx = moveX * camSpeed;
		float dy = moveY * camSpeed;
		
		cam.translate(dx, dy);
		cam.zoom += zoom * CAMSPEED_ZOOM;
	}
	
	private static void processCameraControll() {
		if(!isMode(Mode.CAMERA))
			return;
		
		if(InputHandler.getInstance().isKeyDown(CAM_UP))		moveY = 1;
		else if(InputHandler.getInstance().isKeyDown(CAM_DOWN))	moveY = -1;
		else													moveY = 0;
		
		if(InputHandler.getInstance().isKeyDown(CAM_RIGHT))		moveX = 1;
		else if(InputHandler.getInstance().isKeyDown(CAM_LEFT))	moveX = -1;
		else													moveX = 0;
		
		if(InputHandler.getInstance().isKeyDown(CAM_FAST))		camSpeed = CAMSPEED_INITIAL * CAMSPEED_UP;
		else													camSpeed = CAMSPEED_INITIAL;	
		
		if(InputHandler.getInstance().isKeyDown(CAM_ZOOM_IN))			zoom = 1;
		else if (InputHandler.getInstance().isKeyDown(CAM_ZOOM_OUT))	zoom = -1;
		else															zoom = 0;
	}
}
