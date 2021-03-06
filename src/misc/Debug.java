package misc;

import com.badlogic.gdx.Input.Keys;

import core.GameProperties;
import core.ingame.Camera;
import core.ingame.input.player.InputHandler;

public abstract class Debug {

	private static boolean on = false;
	private static InputHandler iHandler;
	private static Camera camera;
	
	public static void init(InputHandler iH, Camera cam) {
		iHandler = iH;
		camera = cam;
	}

	public static boolean isOn() {
		return on && GameProperties.debug;
	}

	public static void toogleOnOff() {
		on = !on;

		System.out.println("Debug " + (on ? "on" : "off"));

		if (on)
			printCurrentMode();
		else
			endMode(mode);
	}

	private static Mode mode = Mode.BOXRENDERER;

	public enum Mode {
		BOXRENDERER(Keys.NUM_1), 
		GEOMETRIC(Keys.NUM_2), 
		CONSOLE(Keys.NUM_3), 
		CAMERA(Keys.NUM_4),
		LIGHTS_OFF(Keys.NUM_5);

		final private int key;

		Mode(int key) {
			this.key = key;
		}
	}

	public static boolean isMode(Mode mod) {
		if (!on) return false;
		return mode.equals(mod);
	}

	public static Mode getMode() {
		return mode;
	}

	public static boolean setMode(int key) {
		for (Mode m : Mode.values())
			if (m.key == key) {
				endMode(mode);
				startMode(m);

				mode = m;
				printCurrentMode();

				return true;
			}
		return false;
	}

	private static void startMode(Mode mode) {
		switch (mode) {

		case BOXRENDERER:
			break;
		case CAMERA:
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
		switch (mode) {

		case BOXRENDERER:
			break;
		case CAMERA:
			if(camera != null)
				camera.zoom = 1;
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
		System.out.println("Mode " + mode.toString());
	}

	public static void println(String log, Mode mode) {
		print(log + '\n', mode);
	}

	public static void println(String log) {
		print(log + '\n');
	}

	public static void print(String log, Mode mode) {
		if (isMode(mode)) print(log);
	}

	public static void print(String log) {
		if (on) System.out.print(log);
	}
	
	// CAMERA
	private static final int[] CAM_UP = { Keys.W, Keys.UP }, CAM_RIGHT = { Keys.D, Keys.RIGHT }, CAM_DOWN = { Keys.S,
			Keys.DOWN }, CAM_LEFT = { Keys.A, Keys.LEFT }, CAM_FAST = { Keys.F, Keys.SPACE }, CAM_ZOOM_IN = { Keys.E,
			Keys.I }, CAM_ZOOM_OUT = { Keys.Q, Keys.O };

	private static final float CAMSPEED_INITIAL = 5, CAMSPEED_UP = 2, CAMSPEED_ZOOM = 0.1f;

	private static float camSpeed = CAMSPEED_INITIAL;
	private static float zoom;
	private static float moveX, moveY;

	public static void processCamera(Camera cam) {
		processCameraControll();

		float dx = moveX * camSpeed * cam.zoom;
		float dy = moveY * camSpeed * cam.zoom;
		
		cam.translate(dx, dy);
		cam.zoom += zoom * CAMSPEED_ZOOM;
	}

	private static void processCameraControll() {
		if (!isMode(Mode.CAMERA) || iHandler == null) return;

		if (iHandler.isKeyDown(CAM_UP))
			moveY = 1;
		else if (iHandler.isKeyDown(CAM_DOWN))
			moveY = -1;
		else
			moveY = 0;

		if (iHandler.isKeyDown(CAM_RIGHT))
			moveX = 1;
		else if (iHandler.isKeyDown(CAM_LEFT))
			moveX = -1;
		else
			moveX = 0;

		if (iHandler.isKeyDown(CAM_FAST))
			camSpeed = CAMSPEED_INITIAL * CAMSPEED_UP;
		else
			camSpeed = CAMSPEED_INITIAL;

		if (iHandler.isKeyDown(CAM_ZOOM_IN))
			zoom = -1;
		else if (iHandler.isKeyDown(CAM_ZOOM_OUT))
			zoom = 1;
		else
			zoom = 0;
	}
}
