package gameObject.interaction.enemy;

import misc.Debug;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import core.GameProperties;
import core.ingame.Camera;
import core.ingame.IDrawable;

public class Alarm implements IDrawable {

	private static float totalAlarmTime = 0;

	private static Alarm alarm;

	public static Alarm getInstance() {
		if (alarm == null) System.err.println("Alarm not initialized");
		return alarm;
	}

	public static Alarm iniInstance(RayHandler rayHandler, Camera camera) {
		return alarm = new Alarm(rayHandler, camera);
	}

	private Camera camera;
	private PointLight[] alarmLights;
	private int timer;

	private final float W, H, DISTANCE_MAX;
	private float x, y, distance;

	private Alarm(RayHandler rayHandler, Camera camera) {
		this.camera = camera;

		alarmLights = new PointLight[4];
		for (int i = 0; i < alarmLights.length; i++)
			alarmLights[i] = new PointLight(rayHandler, 32, Color.RED, 100, 0, 0);

		W = GameProperties.SCALE_WIDTH * 0.7f;
		H = GameProperties.SCALE_HEIGHT * 0.7f;
		DISTANCE_MAX = 400;
	}

	public void draw(SpriteBatch batch, float deltaTime) {
		if (timer > 0) {
			x = camera.position.x;
			y = camera.position.y;

			alarmLights[0].setPosition(x - W, y);
			alarmLights[1].setPosition(x + W, y);
			alarmLights[2].setPosition(x, y - H);
			alarmLights[3].setPosition(x, y + H);

			distance = (distance + 10) % DISTANCE_MAX;

			for (PointLight l : alarmLights)
				l.setDistance(distance);
			timer--;
			totalAlarmTime += deltaTime;
		} else {
			if (alarmLights[0].isActive()) for (PointLight l : alarmLights)
				l.setActive(false);
		}

		Debug.println("AlarmTimer@" + timer);
	}

	public static void trigger() {
		trigger(80);
	}

	public static void trigger(int time) {
		if (getInstance() == null) return;
		getInstance().timer += time;
		for (PointLight l : getInstance().alarmLights)
			l.setActive(true);
	}

	public static boolean isActive() {
		return getInstance() != null && getInstance().timer > 0;
	}

	public static float getTotalAlarmTime() {
		return totalAlarmTime;
	}
}
