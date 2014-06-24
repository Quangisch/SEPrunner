package gameObject.interaction.enemy;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import core.GameProperties;
import core.ingame.Camera;
import core.ingame.IDrawable;

public class Alarm implements IDrawable {
	
	private static Alarm alarm;
	
	public static Alarm getInstance() {
		if(alarm == null)
			System.err.println("Alarm not initialized");
		return alarm;
	}
	
	public static Alarm iniInstance(RayHandler rayHandler, Camera camera) {
		return alarm = new Alarm(rayHandler, camera);
	}
	
	private Camera camera;
	private PointLight[] alarmLights;
	private boolean active = false;
	
	private final float W, H, DISTANCE_MAX;
	private float x, y, distance;
	
	private Alarm(RayHandler rayHandler, Camera camera) {
		this.camera = camera;
		
		alarmLights = new PointLight[4];
		for(int i = 0; i < alarmLights.length; i++)
			alarmLights[i] = new PointLight(rayHandler, 32, Color.RED, 100, 0, 0);

		W = GameProperties.SCALE_WIDTH*0.7f;
		H = GameProperties.SCALE_HEIGHT*0.7f;
		DISTANCE_MAX = 400;
	}
	
	public void draw(SpriteBatch batch, float deltaTime) {
		if(active) {
			x = camera.position.x;
			y = camera.position.y;
			
			alarmLights[0].setPosition(x-W, y);
			alarmLights[1].setPosition(x+W, y);
			alarmLights[2].setPosition(x, y-H);
			alarmLights[3].setPosition(x, y+H);
			
			distance = (distance+10)%DISTANCE_MAX;
			
			for(PointLight l : alarmLights)
				l.setDistance(distance);
			System.out.println("alarm");
		}	
	}
	
	public void setActive(boolean active) {
		this.active = active;
		for(PointLight l : alarmLights)
			l.setActive(active);
	}
	
	public boolean isActive() {
		return active;
	}

}
