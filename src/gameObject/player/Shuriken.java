package gameObject.player;

import gameObject.BodyObject;
import gameObject.GameObject;
import gameObject.ICollisionable;
import gameObject.Sensor;
import gameObject.enemy.Enemy;

import com.badlogic.gdx.math.Vector2;

import core.ingame.GameProperties;

public class Shuriken extends GameObject {

	private Vector2 direction;
	private int ttl = 70; // Time To Live

	public Shuriken(ICollisionable thrower, Vector2 clickPoint) {
		super(thrower.getGameWorld(), thrower.getLocalCenterInWorld());

		this.init("shuriken");
		this.setGameObjectType(GameObjectTypes.SHURIKEN);

		direction = GameProperties.pixelToMeter(clickPoint.sub(getPosition()));
		addToGameWorld(this);

		setGravityScale(0);
		applyImpulse(direction.nor().scl(7));
	}

	public void run() {
		if (ttl-- <= 0)
			dispose();
	}

	@Override
	public boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		if (start) {
			
			switch(other.getGameObjectType()) {
			
			case GameObjectTypes.GROUND :
				ttl = 5;
				return true;
			case GameObjectTypes.ENEMY :
				((Enemy) other).setStun();
				dispose();
				return true;
			}
		}

		return false;
	}

}
